package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

public class BaseComponent<T extends BaseComponent<?>> {

    @Getter
    private final SelenideElement self;

    public BaseComponent(SelenideElement self) {
        this.self = self;
    }
}
