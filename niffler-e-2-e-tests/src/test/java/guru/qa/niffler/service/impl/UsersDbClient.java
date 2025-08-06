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
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UsersDbClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

//    private final AuthUserRepository authUserRepository = new AuthUserRepositoryJdbc();
//    private final AuthUserRepository authUserRepository = new AuthUserRepositorySpring();
    private final AuthUserRepository authUserRepository = new AuthUserRepositoryHibernate();
//    private final UserdataUserRepository userdataUserRepository = new UserdataUserRepositoryJdbc();
//    private final UserdataUserRepository userdataUserRepository = new UserdataUserRepositorySpring();
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
    public UserJson createUser(String username, String password) {
        return xaTransactionTemplate.execute(() -> {
            authUserRepository.create(authUserEntity(username, password));
            return UserJson.fromEntity(
                    userdataUserRepository.create(userEntity(username)),
                    null
            );
        });
    }

    @Override
    public AuthUserJson update(AuthUserJson authUserJson) {
        return xaTransactionTemplate.execute(() -> AuthUserJson.fromEntity(authUserRepository.update(AuthUserEntity.fromJson(authUserJson))));
    }

    @Override
    public Optional<AuthUserJson> getAuthUserById(UUID id) {
        return authUserRepository.findById(id).map(AuthUserJson::fromEntity);
    }

    @Override
    public Optional<AuthUserJson> getAuthUserByName(String username) {
        return authUserRepository.findByUsername(username).map(AuthUserJson::fromEntity);
    }

    @Override
    public List<AuthUserJson> findAll() {
        return xaTransactionTemplate.execute(() -> {
                    List<AuthUserEntity> authUserEntities = authUserRepository.findAll();
                    return authUserEntities.stream()
                            .map(AuthUserJson::fromEntity)
                            .toList();
                }
        );
    }

    @Override
    public UserJson update(UserJson userJson) {
        return xaTransactionTemplate.execute(() -> UserJson.fromEntity(userdataUserRepository.update(UserEntity.fromJson(userJson)), null));
    }

    @Override
    public Optional<UserJson> getUserById(UUID id) {
        return userdataUserRepository.findById(id).map(user -> UserJson.fromEntity(user, null));
    }

    @Override
    public Optional<UserJson> getUserByName(String username) {
        return userdataUserRepository.findByUsername(username).map(user -> UserJson.fromEntity(user, null));
    }

    @Override
    public List<UserJson> addIncomeInvitation(UserJson targetUser, int count) {
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
    public List<UserJson> addOutcomeInvitation(UserJson targetUser, int count) {
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
    public List<UserJson> addFriend(UserJson targetUser, int count) {
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
    public void addFriend(UserJson requester, UserJson addressee) {
        xaTransactionTemplate.execute(() -> {
            userdataUserRepository.addFriend(UserEntity.fromJson(requester), UserEntity.fromJson(addressee));
            return null;
        });
    }

    private UserEntity userEntity(String username) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setCurrency(CurrencyValues.RUB);
        return userEntity;
    }

    private AuthUserEntity authUserEntity(String username, String password) {
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
