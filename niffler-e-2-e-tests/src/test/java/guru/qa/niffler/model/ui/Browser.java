package guru.qa.niffler.model.ui;

import com.codeborne.selenide.SelenideDriver;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

import static guru.qa.niffler.utils.SelenideUtils.chromeConfig;
import static guru.qa.niffler.utils.SelenideUtils.firefoxConfig;

public enum Browser {
    CHROME, FIREFOX;

    public static class BrowserConverter implements ArgumentConverter {

        public BrowserConverter() {
        }

        @Override
        public Object convert(Object source, ParameterContext context) throws ArgumentConversionException {
            if (source == null) {
                throw new ArgumentConversionException("null не поддерживается");
            }
            if (!SelenideDriver.class.isAssignableFrom(context.getParameter().getType())) {
                throw new ArgumentConversionException(
                        "Этот конвертер может конвертировать только к SelenideDriver");
            }
            return switch ((Browser) source) {
                case CHROME -> new SelenideDriver(chromeConfig);
                case FIREFOX -> new SelenideDriver(firefoxConfig);
            };
        }
    }
}
