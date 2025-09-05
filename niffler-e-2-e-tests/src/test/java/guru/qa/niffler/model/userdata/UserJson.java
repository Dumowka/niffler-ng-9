package guru.qa.niffler.model.userdata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.CurrencyValues;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
public record UserJson(
        UUID id,
        String username,
        CurrencyValues currency,
        String firstname,
        String surname,
        String fullname,
        byte[] photo,
        byte[] photoSmall,
        @Nullable FriendshipStatus friendshipStatus,
        @JsonIgnore
        @Nullable TestData testData
) {
    public UserJson(@Nonnull String username, @Nullable TestData testData) {
        this(null, username, null, null, null, null, null, null, null, testData);
    }

    public static @Nonnull UserJson fromEntity(UserEntity entity, @Nullable FriendshipStatus friendshipStatus) {
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

    public @Nonnull UserJson addTestData(TestData testData) {
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