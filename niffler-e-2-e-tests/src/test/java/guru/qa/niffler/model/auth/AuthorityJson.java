package guru.qa.niffler.model.auth;

import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
public record AuthorityJson(
  UUID id,
  Authority authority,
  UUID userId
) {
  public static @Nonnull AuthorityJson fromEntity(AuthorityEntity entity) {
    return new AuthorityJson(
            entity.getId(),
            entity.getAuthority(),
            entity.getUser().getId()
    );
  }
}
