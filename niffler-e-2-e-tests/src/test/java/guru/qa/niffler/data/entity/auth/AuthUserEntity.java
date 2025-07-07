package guru.qa.niffler.data.entity.auth;

import guru.qa.niffler.model.auth.AuthUserJson;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AuthUserEntity implements Serializable {
  private UUID id;
  private String username;
  private String password;
  private Boolean enabled;
  private Boolean accountNonExpired;
  private Boolean accountNonLocked;
  private Boolean credentialsNonExpired;
  private List<AuthorityEntity> authorities = new ArrayList<>();

  public static AuthUserEntity fromJson(AuthUserJson json) {
    AuthUserEntity entity = new AuthUserEntity();
    entity.setId(json.getId());
    entity.setUsername(json.getUsername());
    entity.setPassword(json.getPassword());
    entity.setEnabled(json.getEnabled());
    entity.setAccountNonExpired(json.getAccountNonExpired());
    entity.setAccountNonLocked(json.getAccountNonLocked());
    entity.setCredentialsNonExpired(json.getCredentialsNonExpired());
    json.getAuthorities().stream().map(AuthorityEntity::fromJson).forEach(authorityEntry -> entity.getAuthorities().add(authorityEntry));
    return entity;
  }
}
