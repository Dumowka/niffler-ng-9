package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.apache.commons.lang.ArrayUtils;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.List;

import static guru.qa.niffler.jupiter.extension.TestMethodContextExtension.context;

public class CategoryExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);

    private final SpendClient spendClient = SpendClient.getInstance();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(
                context.getRequiredTestMethod(),
                User.class
        ).ifPresent(userAnnotation -> {
            if (ArrayUtils.isNotEmpty(userAnnotation.categories())) {
                UserJson createdUser = UserExtension.createdUser();
                String username = createdUser != null ? createdUser.username() : userAnnotation.username();
                List<CategoryJson> result = new ArrayList<>();

                if (!"".equals(username)) {
                    for (Category category : userAnnotation.categories()) {
                        result.add(createCategory(userAnnotation, username, category.archived()));
                    }
                }
                if (createdUser != null) {
                    createdUser.testData().categories().addAll(result);
                } else {
                    context.getStore(NAMESPACE).put(
                            context.getUniqueId(),
                            result.stream().toArray(CategoryJson[]::new)
                    );
                }
            }
        });
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        CategoryJson[] categories = createdCategory();
        if (categories != null) {
            for (CategoryJson category : categories) {
                if (category != null && !category.archived()) {
                    spendClient.updateCategory(
                            new CategoryJson(
                                    category.id(),
                                    category.name(),
                                    category.username(),
                                    true
                            )
                    );
                }
            }
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
    }

    @Override
    public CategoryJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return createdCategory();
    }

    public static CategoryJson[] createdCategory() {
        final ExtensionContext methodContext = context();
        return methodContext.getStore(NAMESPACE)
                .get(methodContext.getUniqueId(), CategoryJson[].class);
    }

    private CategoryJson createCategory(User userAnnotation, String username, boolean isArchived) {
        CategoryJson categoryJson = new CategoryJson(
                null,
                RandomDataUtils.randomCategoryName(),
                username,
                isArchived
        );
        return spendClient.createCategory(categoryJson);
    }
}
