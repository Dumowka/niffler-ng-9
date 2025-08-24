package guru.qa.niffler.jupiter.extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.model.allure.ScreenDif;
import io.qameta.allure.Allure;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.platform.commons.support.AnnotationSupport;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Base64;

public class ScreenShotTestExtension implements ParameterResolver, TestExecutionExceptionHandler {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ScreenShotTestExtension.class);
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return AnnotationSupport.isAnnotated(extensionContext.getRequiredTestMethod(), ScreenShotTest.class) &&
                parameterContext.getParameter().getType().isAssignableFrom(BufferedImage.class);
    }

    @SneakyThrows
    @Override
    public BufferedImage resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getTestMethod()
                .map(m -> m.getAnnotation(ScreenShotTest.class))
                .map(ScreenShotTest::value)
                .map(path -> {
                    try {
                        return ImageIO.read(new ClassPathResource(path).getInputStream());
                    } catch (IOException e) {
                        throw new ParameterResolutionException("Не удалось загрузить изображение: " + path, e);
                    }
                })
                .orElseThrow(() -> new ParameterResolutionException("Аннотация @ScreenShotTest не найдена"));
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        ScreenShotTest annotation = AnnotationSupport.findAnnotation(
                context.getRequiredTestMethod(),
                ScreenShotTest.class
        ).orElseThrow(() -> new ParameterResolutionException("Аннотация @ScreenShotTest не найдена"));

        if (annotation.rewriteExpected()) {
            final BufferedImage actual = getActual();
            if (actual != null) {
                ImageIO.write(
                        actual,
                        "png",
                        Paths.get("src/test/resources/" + annotation.value()).toFile()
                );
            }
        } else {
            ScreenDif screenDif = new ScreenDif(
                    "data:image/png;base64," + Base64.getEncoder().encodeToString(imageToBytes(getExpected())),
                    "data:image/png;base64," + Base64.getEncoder().encodeToString(imageToBytes(getActual())),
                    "data:image/png;base64," + Base64.getEncoder().encodeToString(imageToBytes(getDiff()))
            );

            Allure.addAttachment(
                    "Screenshot diff",
                    "application/vnd.allure.image.diff",
                    OBJECT_MAPPER.writeValueAsString(screenDif)
            );
        }

        throw throwable;
    }

    public static void setExpected(BufferedImage expected) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put("expected", expected);
    }

    public static BufferedImage getExpected() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("expected", BufferedImage.class);
    }

    public static void setActual(BufferedImage expected) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put("actual", expected);
    }

    public static BufferedImage getActual() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("actual", BufferedImage.class);
    }

    public static void setDiff(BufferedImage expected) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put("diff", expected);
    }

    public static BufferedImage getDiff() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("diff", BufferedImage.class);
    }

    private static byte[] imageToBytes(BufferedImage image) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
