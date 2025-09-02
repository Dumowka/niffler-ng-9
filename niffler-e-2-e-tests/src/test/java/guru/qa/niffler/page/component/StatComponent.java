package guru.qa.niffler.page.component;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.condition.StatConditions;
import guru.qa.niffler.model.ui.Bubble;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static guru.qa.niffler.jupiter.extension.ScreenShotTestExtension.ASSERT_SCREEN_MESSAGE;
import static guru.qa.niffler.utils.PageUtils.getElementScreenshot;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class StatComponent extends BaseComponent<StatComponent> {

    public StatComponent() {
        super($("#stat"));
    }

    private final SelenideElement spendingChart = $("#chart");
    private final SelenideElement statisticsCanvas = $("canvas[role='img']");
    private final SelenideElement spendingLegend = $("#legend-container");

    private final ElementsCollection bubbles = getSelf().$("#legend-container").$$("li");

    public StatComponent checkThatComponentLoaded() {
        spendingChart.shouldBe(visible);
        spendingLegend.shouldBe(visible);
        return this;
    }

    @Step("Проверка, что изображение Statistics соответствует ожидаемому")
    public StatComponent checkStatisticsImage(BufferedImage expected) throws IOException {
        Selenide.sleep(3000);
        BufferedImage actual = getElementScreenshot(statisticsCanvas);
        assertFalse(
                new ScreenDiffResult(
                        expected,
                        actual
                ),
                ASSERT_SCREEN_MESSAGE
        );
        return this;
    }

    @Step("Проверка, что в статистике имеется {0}")
    public StatComponent checkStatisticBubblesContains(String... texts) {
        bubbles.shouldHave(CollectionCondition.texts(texts));
        return this;
    }

    @Step("Проверка, что статистика состоит из {0}")
    public StatComponent checkBubbles(Bubble... expectedBubbles) {
        bubbles.shouldBe(StatConditions.statBubbles(expectedBubbles));
        return this;
    }

    @Step("Проверка, что статистика состоит в любом порядке из {0}")
    public StatComponent checkBubblesInAnyOrder(Bubble... expectedBubbles) {
        bubbles.shouldBe(StatConditions.statBubblesInAnyOrder(expectedBubbles));
        return this;
    }

    @Step("Проверка, что в статистике имеется {0}")
    public StatComponent checkBubbleContains(Bubble... expectedBubbles) {
        bubbles.shouldBe(StatConditions.statBubblesContains(expectedBubbles));
        return this;
    }
}
