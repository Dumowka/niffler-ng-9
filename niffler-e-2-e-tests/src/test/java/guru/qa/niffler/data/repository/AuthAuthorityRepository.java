package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import java.util.List;

public interface AuthAuthorityRepository {
    void createAuthority(AuthorityEntity... authorityEntities);
    List<AuthorityEntity> findAll();
}
