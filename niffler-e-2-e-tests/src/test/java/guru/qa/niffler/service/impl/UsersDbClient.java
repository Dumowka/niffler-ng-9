package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.repository.impl.hibernate.AuthUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.hibernate.UserdataUserRepositoryHibernate;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.auth.AuthUserJson;
import guru.qa.niffler.model.userdata.FriendshipStatus;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.utils.RandomDataUtils;
import io.qameta.allure.Step;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class UsersDbClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserRepository authUserRepository = new AuthUserRepositoryHibernate();
    private final UserdataUserRepository userdataUserRepository = new UserdataUserRepositoryHibernate();

    private final TransactionTemplate txTemplate = new TransactionTemplate(
            new JdbcTransactionManager(
                    DataSources.dataSource(CFG.authJdbcUrl())
            )
    );

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    @Override
    @Step("Создание пользователя {username} в сервисах Auth и Userdata через БД")
    public @Nonnull UserJson createUser(String username, String password) {
        return Objects.requireNonNull(xaTransactionTemplate.execute(() -> {
            authUserRepository.create(authUserEntity(username, password));
            return UserJson.fromEntity(
                    userdataUserRepository.create(userEntity(username)),
                    null
            );
        }));
    }

    @Override
    @Step("Обновление данных пользователя {authUserJson.username} в сервисе Auth через БД")
    public @Nonnull AuthUserJson update(AuthUserJson authUserJson) {
        return Objects.requireNonNull(xaTransactionTemplate.execute(() ->
                AuthUserJson.fromEntity(authUserRepository.update(AuthUserEntity.fromJson(authUserJson)))
        ));
    }

    @Override
    @Step("Получение пользователя Auth по ID: {id} в сервисе Auth через БД")
    public @Nonnull Optional<AuthUserJson> getAuthUserById(UUID id) {
        return authUserRepository.findById(id).map(AuthUserJson::fromEntity);
    }

    @Override
    @Step("Получение пользователя Auth по имени: {username} в сервисе Auth через БД")
    public @Nonnull Optional<AuthUserJson> getAuthUserByName(String username) {
        return authUserRepository.findByUsername(username).map(AuthUserJson::fromEntity);
    }

    @Override
    @Step("Получение всех пользователей в сервисе Auth через БД")
    public @Nonnull List<AuthUserJson> findAll() {
        return Objects.requireNonNull(xaTransactionTemplate.execute(() -> {
                    List<AuthUserEntity> authUserEntities = authUserRepository.findAll();
                    return authUserEntities.stream()
                            .map(AuthUserJson::fromEntity)
                            .toList();
                }
        ));
    }

    @Override
    @Step("Обновление данных пользователя {userJson.username} в сервисе Userdata через БД")
    public @Nonnull UserJson update(UserJson userJson) {
        return Objects.requireNonNull(xaTransactionTemplate.execute(() ->
                UserJson.fromEntity(userdataUserRepository.update(UserEntity.fromJson(userJson)), null
                )));
    }

    @Override
    @Step("Получение пользователя User по ID: {id} в сервисе Userdata через БД")
    public @Nonnull Optional<UserJson> getUserById(UUID id) {
        return userdataUserRepository.findById(id).map(user -> UserJson.fromEntity(user, null));
    }

    @Override
    @Step("Получение пользователя User по имени: {username} в сервисе Userdata через БД")
    public @Nonnull Optional<UserJson> getUserByName(String username) {
        return userdataUserRepository.findByUsername(username).map(user -> UserJson.fromEntity(user, null));
    }

    @Override
    @Step("Добавление входящих заявок в друзья пользователю: {targetUser.username}, количество: {count} через БД")
    public @Nonnull List<UserJson> addIncomeInvitation(UserJson targetUser, int count) {
        final List<UserJson> result = new ArrayList<>();
        if (count > 0) {
            UserEntity targetEntity = userdataUserRepository.findById(
                    targetUser.id()
            ).orElseThrow();
            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                    String username = RandomDataUtils.randomUsername();
                    AuthUserEntity authUser = authUserEntity(username, "12345");
                    authUserRepository.create(authUser);
                    UserEntity addressee = userdataUserRepository.create(userEntity(username));
                    userdataUserRepository.addInvitation(targetEntity, addressee);
                    result.add(UserJson.fromEntity(
                            addressee, FriendshipStatus.INVITE_RECEIVED
                    ));
                    return null;
                });
            }
        }
        return result;
    }

    @Override
    @Step("Добавление исходящих заявок в друзья пользователю: {targetUser.username}, количество: {count} через БД")
    public @Nonnull List<UserJson> addOutcomeInvitation(UserJson targetUser, int count) {
        final List<UserJson> result = new ArrayList<>();
        if (count > 0) {
            UserEntity targetEntity = userdataUserRepository.findById(
                    targetUser.id()
            ).orElseThrow();
            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                    String username = RandomDataUtils.randomUsername();
                    AuthUserEntity authUser = authUserEntity(username, "12345");
                    authUserRepository.create(authUser);
                    UserEntity addressee = userdataUserRepository.create(userEntity(username));
                    userdataUserRepository.addInvitation(addressee, targetEntity);
                    result.add(UserJson.fromEntity(
                            addressee, FriendshipStatus.INVITE_RECEIVED
                    ));
                    return null;
                });
            }
        }
        return result;
    }

    @Override
    @Step("Удаление пользователя: {authUserJson.username} в сервисах Auth и Userdata через БД")
    public void removeUser(AuthUserJson authUserJson) {
        xaTransactionTemplate.execute(() -> {
                    AuthUserEntity authUser = AuthUserEntity.fromJson(authUserJson);
                    authUserRepository.remove(authUser);
                    Optional<UserEntity> userEntity = userdataUserRepository.findByUsername(authUser.getUsername());
                    userdataUserRepository.remove(userEntity.get());
                    return null;
                }
        );
    }

    @Override
    @Step("Добавление друзей пользователю: {targetUser.username}, количество: {count} через БД")
    public @Nonnull List<UserJson> addFriend(UserJson targetUser, int count) {
        final List<UserJson> result = new ArrayList<>();
        if (count > 0) {
            UserEntity targetEntity = userdataUserRepository.findById(
                    targetUser.id()
            ).orElseThrow();
            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                    String username = RandomDataUtils.randomUsername();
                    AuthUserEntity authUser = authUserEntity(username, "12345");
                    authUserRepository.create(authUser);
                    UserEntity addressee = userdataUserRepository.create(userEntity(username));
                    userdataUserRepository.addFriend(targetEntity, addressee);
                    result.add(UserJson.fromEntity(
                            addressee, FriendshipStatus.FRIEND
                    ));
                    return null;
                });
            }
        }
        return result;
    }

    @Override
    @Step("Добавление в друзья: {requester.username} → {addressee.username} через БД")
    public void addFriend(UserJson requester, UserJson addressee) {
        xaTransactionTemplate.execute(() -> {
            userdataUserRepository.addFriend(UserEntity.fromJson(requester), UserEntity.fromJson(addressee));
            return null;
        });
    }

    private @Nonnull UserEntity userEntity(String username) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setCurrency(CurrencyValues.RUB);
        return userEntity;
    }

    private @Nonnull AuthUserEntity authUserEntity(String username, String password) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(username);
        authUser.setPassword(pe.encode(password));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);
        authUser.setAuthorities(
                Arrays.stream(Authority.values()).map(
                        e -> {
                            AuthorityEntity ae = new AuthorityEntity();
                            ae.setUser(authUser);
                            ae.setAuthority(e);
                            return ae;
                        }
                ).toList()
        );
        return authUser;
    }
}
