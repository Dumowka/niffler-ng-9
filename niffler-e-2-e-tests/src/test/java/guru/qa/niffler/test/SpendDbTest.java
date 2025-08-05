package guru.qa.niffler.test;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.service.impl.SpendDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SpendDbTest {
    private final SpendDbClient spendDbClient = new SpendDbClient();

    private String username = "test-2";
    private String description = "vitae omnis";

    @Test
    void checkCreateSend() {
        SpendJson spendJson = spendDbClient.create(generateNewSpend());
        System.out.println(spendJson);
        assertNotNull(spendJson);
    }

    @Test
    void checkSpendFindByUsernameAndUpdate() {
        SpendJson spendJson = spendDbClient.findByUsernameAndSpendDescription(username, description).get();

        SpendJson updatedSpend = new SpendJson(
                spendJson.id(),
                spendJson.spendDate(),
                spendJson.category(),
                spendJson.currency(),
                123.00,
                spendJson.description(),
                spendJson.username()
        );
        spendDbClient.update(updatedSpend);
        SpendJson updateJson = spendDbClient.findByUsernameAndSpendDescription(username, description).get();
        assertSpends(updatedSpend, updateJson);
    }

    @Test
    void checkSpendFindById() {
        SpendJson spendJson = spendDbClient.create(generateNewSpend());
        SpendJson spendJsonById = spendDbClient.findById(spendJson.id()).get();
        assertSpends(spendJson, spendJsonById);
    }

    // Падает тест при реализации Hibernate с ошибкой guru.qa.niffler.data.entity.spend.SpendEntity#a61ddd8f-38ea-4f8d-8e4b-cde0a15b5db6
    @Test
    void checkRemoveSpend() {
        SpendJson spendJson = spendDbClient.create(generateNewSpend());
        spendDbClient.remove(spendJson);
        SpendJson afterRemoving = spendDbClient.findById(spendJson.id()).orElse(null);
        assertNull(afterRemoving);
    }

    @Test
    void checkCategoryCreate() {
        CategoryJson categoryJson = spendDbClient.createCategory(generateNewCategory());
        assertNotNull(categoryJson);
        System.out.println(categoryJson);
    }

    @Test
    void checkCategoryFindByUsernameAndUpdate() {
        CategoryJson categoryJson = spendDbClient.createCategory(generateNewCategory());
        CategoryJson updatedCategory = new CategoryJson(
                categoryJson.id(),
                categoryJson.name(),
                categoryJson.username(),
                !categoryJson.archived()
        );

        CategoryJson receivedCategory = spendDbClient.updateCategory(updatedCategory);
        assertEquals(updatedCategory, receivedCategory);
    }

    @Test
    void checkCategoryFindById() {
        CategoryJson categoryJson = spendDbClient.createCategory(generateNewCategory());
        CategoryJson categoryById = spendDbClient.findCategoryById(categoryJson.id()).get();
        assertEquals(categoryJson, categoryById);
    }

    // Падает с ошибкой java.lang.IllegalArgumentException: Removing a detached instance guru.qa.niffler.data.entity.spend.CategoryEntity#6182b611-4f63-416b-a55d-0edeafdab1dc
    @Test
    void checkRemoveCategory() {
        CategoryJson categoryJson = spendDbClient.createCategory(generateNewCategory());
        spendDbClient.removeCategory(categoryJson);
        CategoryJson categoryAfterRemoving = spendDbClient.findCategoryById(categoryJson.id()).orElse(null);
        assertNull(categoryAfterRemoving);
    }

    // Падает с ошибкой java.lang.IllegalArgumentException: Removing a detached instance guru.qa.niffler.data.entity.spend.CategoryEntity#f911948c-717c-4619-af30-947e7bea7fbc
    @Test
    void checkRemoveCategoryWithSpend() {
        SpendJson spendJson = spendDbClient.create(generateNewSpend());
        CategoryJson categoryJson = spendJson.category();
        UUID categoryId = categoryJson.id();
        spendDbClient.removeCategory(categoryJson);
        SpendJson afterRemovingSpend = spendDbClient.findById(spendJson.id()).orElse(null);
        CategoryJson afterRemovingCategory = spendDbClient.findCategoryById(categoryId).orElse(null);
        assertNull(afterRemovingSpend);
        assertNull(afterRemovingCategory);
    }

    private CategoryJson generateNewCategory() {
        return new CategoryJson(
                null,
                RandomDataUtils.randomCategoryName(),
                username,
                true
        );
    }

    private SpendJson generateNewSpend() {
        return new SpendJson(
                null,
                new Date(),
                new CategoryJson(
                        null,
                        RandomDataUtils.randomCategoryName(),
                        username,
                        false
                ),
                CurrencyValues.RUB,
                100.00,
                RandomDataUtils.randomSentence(2),
                username
        );
    }

    private void assertSpends(SpendJson spend1, SpendJson spend2) {
        assertEquals(spend1.id(), spend2.id());
        assertEquals(
                new java.sql.Date(spend1.spendDate().getTime()),
                new java.sql.Date(spend1.spendDate().getTime())
        );
        assertEquals(spend1.category(), spend2.category());
        assertEquals(spend1.currency(), spend2.currency());
        assertEquals(spend1.amount(), spend2.amount());
        assertEquals(spend1.description(), spend2.description());
        assertEquals(spend1.username(), spend2.username());
    }
}
