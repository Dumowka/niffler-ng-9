package guru.qa.niffler.model.userdata;

import guru.qa.niffler.data.entity.userdata.UserdataUserEntity;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.auth.AuthUserJson;

import java.util.UUID;

public record UserdataUserJson(
        UUID id,
        String username,
        CurrencyValues currency,
        String firstname,
        String surname,
        String fullname,
        byte[] photo,
        byte[] photoSmall
) {
  public static UserdataUserJson fromAuthUserJson(AuthUserJson authUserJson) {
      return new UserdataUserJson(
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

  public static UserdataUserJson fromEntity(UserdataUserEntity entity) {
      return new UserdataUserJson(
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