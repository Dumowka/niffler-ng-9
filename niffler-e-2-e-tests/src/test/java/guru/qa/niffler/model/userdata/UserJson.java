package guru.qa.niffler.model.userdata;

import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.auth.AuthUserJson;

import java.util.UUID;

public record UserJson(
        UUID id,
        String username,
        CurrencyValues currency,
        String firstname,
        String surname,
        String fullname,
        byte[] photo,
        byte[] photoSmall
) {
  public static UserJson fromAuthUserJson(AuthUserJson authUserJson) {
      return new UserJson(
              authUserJson.getId(),
              authUserJson.getUsername(),
              CurrencyValues.RUB,
              null,
              null,
              null,
              null,
              null
      );
  }

  public static UserJson fromEntity(UserEntity entity) {
      return new UserJson(
              entity.getId(),
              entity.getUsername(),
              entity.getCurrency(),
              entity.getFirstname(),
              entity.getSurname(),
              entity.getFullname(),
              entity.getPhoto(),
              entity.getPhotoSmall()
      );
  }
}