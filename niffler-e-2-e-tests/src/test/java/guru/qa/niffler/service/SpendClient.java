package guru.qa.niffler.service;

import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.service.impl.SpendDbClient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface SpendClient {
    @Nonnull
    static SpendClient getInstanse() {
        return new SpendDbClient();
    }

    @Nullable
    SpendJson create(SpendJson spend);

    @Nullable
    SpendJson update(SpendJson spend);

    @Nullable
    CategoryJson createCategory(CategoryJson category);

    @Nullable
    CategoryJson updateCategory(CategoryJson category);

    @Nonnull
    Optional<CategoryJson> findCategoryById(UUID id);

    @Nonnull
    Optional<CategoryJson> findCategoryByUsernameAndName(String username, String spendName);

    @Nonnull
    Optional<SpendJson> findById(UUID id);

    @Nonnull
    Optional<SpendJson> findByUsernameAndSpendDescription(String username, String spendDescription);

    void remove(SpendJson spend);

    void removeCategory(CategoryJson category);
}
