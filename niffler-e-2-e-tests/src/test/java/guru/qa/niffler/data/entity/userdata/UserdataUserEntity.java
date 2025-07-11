package guru.qa.niffler.data.entity.userdata;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.userdata.UserdataUserJson;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class UserdataUserEntity implements Serializable {
  private UUID id;
  private String username;
  private CurrencyValues currency;
  private String firstname;
  private String surname;
  private String fullname;
  private byte[] photo;
  private byte[] photoSmall;

  public static UserdataUserEntity fromJson(UserdataUserJson json) {
    UserdataUserEntity userdataUserEntity = new UserdataUserEntity();
    userdataUserEntity.setId(json.id());
    userdataUserEntity.setUsername(json.username());
    userdataUserEntity.setCurrency(json.currency());
    userdataUserEntity.setFirstname(json.firstname());
    userdataUserEntity.setSurname(json.surname());
    userdataUserEntity.setFullname(json.fullname());
    userdataUserEntity.setPhoto(json.photo());
    userdataUserEntity.setPhotoSmall(json.photoSmall());
    return userdataUserEntity;
  }
}