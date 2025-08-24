package guru.qa.niffler.utils;

import com.codeborne.selenide.SelenideElement;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class PageUtils {

    public static BufferedImage getElementScreenshot(SelenideElement element) throws IOException {
        return ImageIO.read(element.screenshot());
    }
}
