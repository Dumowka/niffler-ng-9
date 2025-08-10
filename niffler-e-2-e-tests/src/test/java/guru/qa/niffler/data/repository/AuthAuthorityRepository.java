package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface AuthAuthorityRepository {
    void createAuthority(AuthorityEntity... authorityEntities);
    @Nonnull List<AuthorityEntity> findAll();
}
