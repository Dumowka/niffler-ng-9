package guru.qa.niffler.test;

import guru.qa.niffler.jupiter.extension.ClientResolver;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ClientResolver.class)
public class SpendDbTest {

    private SpendClient spendClient;

    private String username = "test-2";
    private String description = "vitae omnis";

    @Test
    void checkCreateSend() {
        SpendJson spendJson = spendClient.create(generateNewSpend());
        System.out.println(spendJson);
        assertNotNull(spendJson);
    }

    @Test
    void checkSpendFindByUsernameAndUpdate() {
        SpendJson spendJson = spendClient.findByUsernameAndSpendDescription(username, description).get();

        SpendJson updatedSpend = new SpendJson(
                spendJson.id(),
                spendJson.spendDate(),
                spendJson.category(),
                spendJson.currency(),
                123.00,
                spendJson.description(),
                spendJson.username()
        );
        spendClient.update(updatedSpend);
        SpendJson updateJson = spendClient.findByUsernameAndSpendDescription(username, description).get();
        assertSpends(updatedSpend, updateJson);
    }

    @Test
    void checkSpendFindById() {
        SpendJson spendJson = spendClient.create(generateNewSpend());
        SpendJson spendJsonById = spendClient.findById(spendJson.id()).get();
        assertSpends(spendJson, spendJsonById);
    }

    // Падает тест при реализации Hibernate с ошибкой guru.qa.niffler.data.entity.spend.SpendEntity#a61ddd8f-38ea-4f8d-8e4b-cde0a15b5db6
    @Test
    void checkRemoveSpend() {
        SpendJson spendJson = spendClient.create(generateNewSpend());
        spendClient.remove(spendJson);
        SpendJson afterRemoving = spendClient.findById(spendJson.id()).orElse(null);
        assertNull(afterRemoving);
    }

    @Test
    void checkCategoryCreate() {
        CategoryJson categoryJson = spendClient.createCategory(generateNewCategory());
        assertNotNull(categoryJson);
        System.out.println(categoryJson);
    }

    @Test
    void checkCategoryFindByUsernameAndUpdate() {
        CategoryJson categoryJson = spendClient.createCategory(generateNewCategory());
        CategoryJson updatedCategory = new CategoryJson(
                categoryJson.id(),
                categoryJson.name(),
                categoryJson.username(),
                !categoryJson.archived()
        );

        CategoryJson receivedCategory = spendClient.updateCategory(updatedCategory);
        assertEquals(updatedCategory, receivedCategory);
    }

    @Test
    void checkCategoryFindById() {
        CategoryJson categoryJson = spendClient.createCategory(generateNewCategory());
        CategoryJson categoryById = spendClient.findCategoryById(categoryJson.id()).get();
        assertEquals(categoryJson, categoryById);
    }

    @Test
    void checkRemoveCategory() {
        CategoryJson categoryJson = spendClient.createCategory(generateNewCategory());
        spendClient.removeCategory(categoryJson);
        CategoryJson categoryAfterRemoving = spendClient.findCategoryById(categoryJson.id()).orElse(null);
        assertNull(categoryAfterRemoving);
    }

    @Test
    void checkRemoveCategoryWithSpend() {
        SpendJson createdSpend = spendClient.create(generateNewSpend());
        spendClient.remove(createdSpend);
        spendClient.removeCategory(createdSpend.category());

        Optional<SpendJson> removedSpend = spendClient.findById(createdSpend.id());
        Optional<CategoryJson> removedCategory = spendClient.findCategoryById(createdSpend.category().id());

        assertTrue(removedSpend.isEmpty());
        assertTrue(removedCategory.isEmpty());
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
