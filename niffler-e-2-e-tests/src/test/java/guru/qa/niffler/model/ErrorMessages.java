package guru.qa.niffler.model;

import lombok.Getter;

@Getter
public enum ErrorMessages {
    BAD_CREDENTIALS("Неверные учетные данные пользователя"),
    PASSWORDS_SHOULD_BE_EQUAL("Passwords should be equal");

    private final String text;

    ErrorMessages(String text) {
        this.text = text;
    }
}
