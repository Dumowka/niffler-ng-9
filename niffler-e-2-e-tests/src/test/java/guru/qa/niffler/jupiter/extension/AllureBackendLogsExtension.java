package guru.qa.niffler.jupiter.extension;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.TestResult;
import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public class AllureBackendLogsExtension implements SuiteExtension {

    public static final String caseName = "Niffler backend logs";
    public static final List<String> services = List.of(
            "niffler-auth",
            "niffler-currency",
            "niffler-gateway",
            "niffler-spend",
            "niffler-userdata"
    );

    @SneakyThrows
    @Override
    public void afterSuite() {
        final AllureLifecycle allureLifecycle = Allure.getLifecycle();
        final String caseId = UUID.randomUUID().toString();
        allureLifecycle.scheduleTestCase(new TestResult().setUuid(caseId).setName(caseName));
        allureLifecycle.startTestCase(caseId);

        for (String service : services) {
            addAttachmentForService(allureLifecycle, service);
        }

        allureLifecycle.stopTestCase(caseId);
        allureLifecycle.writeTestCase(caseId);
    }

    private static void addAttachmentForService(AllureLifecycle allureLifecycle, String serviceName) throws IOException {
        allureLifecycle.addAttachment(
                serviceName + " log",
                "text/html",
                ".log",
                Files.newInputStream(
                        Path.of("./logs/" + serviceName + "/app.log")
                )
        );
    }
}
