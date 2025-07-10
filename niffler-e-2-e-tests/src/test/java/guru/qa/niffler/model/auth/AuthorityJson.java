package guru.qa.niffler.model.auth;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import java.util.UUID;

public record AuthorityJson(
  UUID id,
  Authority authority,
  AuthUserEntity user
) {
  public static AuthorityJson fromEntity(AuthorityEntity entity) {
    return new AuthorityJson(
            entity.getId(),
            entity.getAuthority(),
            entity.getUser()
    );
  }
}
