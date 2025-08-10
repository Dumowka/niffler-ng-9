package guru.qa.niffler.model.auth;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ParametersAreNonnullByDefault
public class AuthUserJson {
    private UUID id;
    private String username;
    private String password;
    private Boolean enabled;
    private Boolean accountNonExpired;
    private Boolean accountNonLocked;
    private Boolean credentialsNonExpired;
    private List<AuthorityJson> authorities = new ArrayList<>();

    public static @Nonnull AuthUserJson fromEntity(AuthUserEntity entity) {
        AuthUserJson authUserJson = new AuthUserJson();
        authUserJson.setId(entity.getId());
        authUserJson.setUsername(entity.getUsername());
        authUserJson.setPassword(entity.getPassword());
        authUserJson.setEnabled(entity.getEnabled());
        authUserJson.setAccountNonExpired(entity.getAccountNonExpired());
        authUserJson.setAccountNonLocked(entity.getAccountNonLocked());
        authUserJson.setCredentialsNonExpired(entity.getCredentialsNonExpired());
        entity.getAuthorities().stream().map(AuthorityJson::fromEntity).forEach(authorityJson -> authUserJson.authorities.add(authorityJson));
        return authUserJson;
    }
}
