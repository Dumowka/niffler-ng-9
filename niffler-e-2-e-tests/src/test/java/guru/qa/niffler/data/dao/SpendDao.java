package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.spend.SpendEntity;

import java.util.List;
import java.util.Optional;

public interface SpendDao {
    SpendEntity create(SpendEntity spend);

    Optional<SpendEntity> findSpendById(Long id);

    List<SpendEntity> findAllSpendsByUsername(String username);

    void deleteSpend(SpendEntity spend);
}
