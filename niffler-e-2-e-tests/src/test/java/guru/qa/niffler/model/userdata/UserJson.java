package guru.qa.niffler.model.userdata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.CurrencyValues;

import java.util.UUID;

public record UserJson(
        UUID id,
        String username,
        CurrencyValues currency,
        String firstname,
        String surname,
        String fullname,
        byte[] photo,
        byte[] photoSmall,
        FriendshipStatus friendshipStatus,
        @JsonIgnore
        TestData testData
) {
    public static UserJson fromEntity(UserEntity entity, FriendshipStatus friendshipStatus) {
        return new UserJson(
                entity.getId(),
                entity.getUsername(),
                entity.getCurrency(),
                entity.getFirstname(),
                entity.getSurname(),
                entity.getFullname(),
                entity.getPhoto(),
                entity.getPhotoSmall(),
                friendshipStatus,
                null
        );
    }

    public UserJson addTestData(TestData testData) {
        return new UserJson(
                id,
                username,
                currency,
                firstname,
                surname,
                fullname,
                photo,
                photoSmall,
                friendshipStatus,
                testData
        );
    }
}