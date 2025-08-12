package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.userdata.TestData;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.List;

import static guru.qa.niffler.jupiter.extension.TestMethodContextExtension.context;

public class UserExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);
    public static final String DEFAULT_PASSWORD = "12345";

    private final UsersClient usersClient = UsersClient.getInstanse();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(
                context.getRequiredTestMethod(),
                User.class
        ).ifPresent(userAnnotation -> {
            if ("".equals(userAnnotation.username())) {
                final String username = RandomDataUtils.randomUsername();
                UserJson created = usersClient.createUser(username, DEFAULT_PASSWORD);
                final List<UserJson> incomes = usersClient.addIncomeInvitation(created, userAnnotation.incomeInvitations());
                final List<UserJson> outcomes = usersClient.addOutcomeInvitation(created, userAnnotation.outcomeInvitations());
                final List<UserJson> friends = usersClient.addFriend(created, userAnnotation.friends());

                TestData testData = new TestData(
                        DEFAULT_PASSWORD,
                        friends,
                        incomes,
                        outcomes,
                        new ArrayList<>(),
                        new ArrayList<>()
                );
                context.getStore(NAMESPACE).put(
                        context.getUniqueId(),
                        created.addTestData(testData)
                );
            }
        });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
    }

    @Override
    public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return createdUser();
    }

    public static UserJson createdUser() {
        final ExtensionContext methodContext = context();
        return methodContext.getStore(NAMESPACE)
                .get(methodContext.getUniqueId(), UserJson.class);
    }
}
