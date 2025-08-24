package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.SpendClient;
import org.apache.commons.lang.ArrayUtils;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static guru.qa.niffler.jupiter.extension.TestMethodContextExtension.context;
import static java.util.Arrays.stream;

public class SpendingExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendingExtension.class);

    private final SpendClient spendClient = SpendClient.getInstance();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(
                context.getRequiredTestMethod(),
                User.class
        ).ifPresent(userAnnotation -> {
            if (ArrayUtils.isNotEmpty(userAnnotation.spendings())) {
                UserJson createdUser = UserExtension.createdUser();
                String username = createdUser != null ? createdUser.username() : userAnnotation.username();

                final List<CategoryJson> existingCategories = createdUser != null
                        ? createdUser.testData().categories()
                        : stream(CategoryExtension.createdCategory()).toList();

                List<SpendJson> result = new ArrayList<>();
                if (!"".equals(username)) {
                    for (Spending spending : userAnnotation.spendings()) {
                        final Optional<CategoryJson> matchedCategory = existingCategories.stream()
                                .filter(category -> category.name().equals(spending.category()))
                                .findFirst();

                        result.add(
                                createSpend(
                                        spending,
                                        username,
                                        matchedCategory.orElseGet(() -> new CategoryJson(
                                                null,
                                                spending.category(),
                                                username,
                                                false
                                        ))
                                )
                        );
                    }
                }

                if (createdUser != null) {
                    createdUser.testData().spendings().addAll(result);
                } else {
                    context.getStore(NAMESPACE).put(
                            context.getUniqueId(),
                            result.stream().toArray(SpendJson[]::new)
                    );
                }
            }
        });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(SpendJson.class);
    }

    @Override
    public SpendJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return createdSpending();
    }

    private SpendJson createSpend(Spending spending, String username, CategoryJson category) {
        return spendClient.create(
                new SpendJson(
                        null,
                        new Date(),
                        category,
                        spending.currency(),
                        spending.amount(),
                        spending.description(),
                        username
                )
        );
    }

    public static SpendJson[] createdSpending() {
        final ExtensionContext methodContext = context();
        return methodContext.getStore(NAMESPACE)
                .get(methodContext.getUniqueId(), SpendJson[].class);
    }
}
