package guru.qa.niffler.api;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.service.RestClient;
import guru.qa.niffler.service.SpendClient;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public final class SpendApiClient extends RestClient implements SpendClient {

    private final SpendApi spendApi;

    public SpendApiClient() {
        super(CFG.spendUrl());
        spendApi = create(SpendApi.class);
    }

    @Override
    @Step("Создание расхода через API: {spend.description}")
    public @Nullable SpendJson create(SpendJson spend) {
        final Response<SpendJson> response;
        try {
            response = spendApi.addSpend(spend).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(201, response.code());
        return response.body();
    }

    @Override
    @Step("Обновление расхода через API: {spend.description}")
    public @Nullable SpendJson update(SpendJson spend) {
        final Response<SpendJson> response;
        try {
            response = spendApi.editSpend(spend).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    @Override
    @Step("Создание категории через API: {category.name}")
    public @Nullable CategoryJson createCategory(CategoryJson category) {
        final Response<CategoryJson> response;
        try {
            response = spendApi.addCategory(category).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    @Override
    @Step("Обновление категории через API: {category.name}")
    public @Nullable CategoryJson updateCategory(CategoryJson category) {
        final Response<CategoryJson> response;
        try {
            response = spendApi.updateCategory(category).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    @Override
    @Step("Поиск категории по ID через API: {id}")
    public @Nonnull Optional<CategoryJson> findCategoryById(UUID id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    @Step("Поиск категории по имени пользователя {username} и названию {spendName} через API")
    public @Nonnull Optional<CategoryJson> findCategoryByUsernameAndName(String username, String spendName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    @Step("Поиск расхода по ID через API: {id}")
    public @Nonnull Optional<SpendJson> findById(UUID id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    @Step("Поиск расхода по имени пользователя {username} и описанию {spendDescription} через API")
    public @Nonnull Optional<SpendJson> findByUsernameAndSpendDescription(String username, String spendDescription) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    @Step("Удаление расхода через API: {spend.description}")
    public void remove(SpendJson spend) {
        final Response<Void> response;
        try {
            response = spendApi.removeSpends(spend.username(), List.of(spend.id().toString())).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
    }

    @Override
    @Step("Удаление категории через API: {category.name}")
    public void removeCategory(CategoryJson category) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Step("Получение расхода через API (по объекту): {spendJson.description}")
    public @Nullable SpendJson getSpend(SpendJson spendJson) {
        final Response<SpendJson> response;
        try {
            response = spendApi.editSpend(spendJson).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    @Step("Поиск расхода через API (по ID: {id}, username: {username})")
    public @Nullable SpendJson findSpend(String id, String username) {
        final Response<SpendJson> response;
        try {
            response = spendApi.getSpend(id, username).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    @Step("Поиск расходов через API для пользователя {username} с фильтром: валюта={filterCurrency}, от={from}, до={to}")
    public @Nonnull List<SpendJson> findSpends(
            String username,
            @Nullable CurrencyValues filterCurrency,
            @Nullable Date from,
            @Nullable Date to
    ) {
        final Response<List<SpendJson>> response;
        try {
            response = spendApi.getSpends(username, filterCurrency, from, to).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body() != null
                ? response.body()
                : Collections.emptyList();
    }

    @Step("Поиск списка категорий через API для пользователя {username}, исключить архивные: {excludeArchived}")
    public @Nonnull List<CategoryJson> findCategories(String username, boolean excludeArchived) {
        final Response<List<CategoryJson>> response;
        try {
            response = spendApi.getCategories(username, excludeArchived).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body() != null
                ? response.body()
                : Collections.emptyList();
    }
}
