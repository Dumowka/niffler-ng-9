package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.impl.hibernate.SpendRepositoryHibernate;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.service.SpendClient;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class SpendDbClient implements SpendClient {

    private static final Config CFG = Config.getInstance();

    private static final SpendRepository spendRepository = new SpendRepositoryHibernate();

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    @Override
    @Step("Создание расхода: {spend.description} через БД")
    public @Nonnull SpendJson create(SpendJson spend) {
        return Objects.requireNonNull(xaTransactionTemplate.execute(() -> {
                    SpendEntity spendEntity = SpendEntity.fromJson(spend);
                    if (spendEntity.getCategory().getId() == null) {
                        CategoryEntity categoryEntity = spendRepository.createCategory(spendEntity.getCategory());
                        spendEntity.setCategory(categoryEntity);
                    }
                    return SpendJson.fromEntity(spendRepository.create(spendEntity));
                }
        ));
    }

    @Override
    @Step("Обновление расхода: {spend.description} через БД")
    public @Nonnull SpendJson update(SpendJson spend) {
        return Objects.requireNonNull(xaTransactionTemplate.execute(() -> {
                    SpendEntity spendEntity = SpendEntity.fromJson(spend);
                    return SpendJson.fromEntity(spendRepository.update(spendEntity));
                }
        ));
    }

    @Override
    @Step("Создание категории: {category.name} через БД")
    public @Nonnull CategoryJson createCategory(CategoryJson category) {
        return Objects.requireNonNull(xaTransactionTemplate.execute(() -> {
                    CategoryEntity categoryEntity = spendRepository.createCategory(CategoryEntity.fromJson(category));
                    return CategoryJson.fromEntity(categoryEntity);
                }
        ));
    }

    @Override
    @Step("Обновление категории: {category.name} через БД")
    public @Nonnull CategoryJson updateCategory(CategoryJson category) {
        return Objects.requireNonNull(xaTransactionTemplate.execute(() -> {
                    CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
                    return CategoryJson.fromEntity(spendRepository.updateCategory(categoryEntity));
                }
        ));
    }

    @Override
    @Step("Поиск категории по ID: {id} через БД")
    public @Nonnull Optional<CategoryJson> findCategoryById(UUID id) {
        return spendRepository.findCategoryById(id).map(CategoryJson::fromEntity);
    }

    @Override
    @Step("Поиск категории по имени пользователя: {username} и названию: {Name} через БД")
    public @Nonnull Optional<CategoryJson> findCategoryByUsernameAndName(String username, String Name) {
        return spendRepository.findCategoryByUsernameAndName(username, Name).map(CategoryJson::fromEntity);
    }

    @Override
    @Step("Поиск расхода по ID: {id} через БД")
    public @Nonnull Optional<SpendJson> findById(UUID id) {
        return spendRepository.findById(id).map(SpendJson::fromEntity);
    }

    @Override
    @Step("Поиск расхода по имени пользователя: {username} и описанию: {spendDescription} через БД")
    public @Nonnull Optional<SpendJson> findByUsernameAndSpendDescription(String username, String spendDescription) {
        return spendRepository.findByUsernameAndSpendDescription(username, spendDescription).map(SpendJson::fromEntity);
    }

    @Override
    @Step("Удаление расхода: {spend.description} через БД")
    public void remove(SpendJson spend) {
        xaTransactionTemplate.execute(() -> {
            spendRepository.remove(SpendEntity.fromJson(spend));
            return null;
        });
    }

    @Override
    @Step("Удаление категории: {category.name} через БД")
    public void removeCategory(CategoryJson category) {
        xaTransactionTemplate.execute(() -> {
            spendRepository.removeCategory(CategoryEntity.fromJson(category));
            return null;
        });
    }
}
