package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Date;

import static guru.qa.niffler.jupiter.extension.TestMethodContextExtension.context;

public class SpendingExtension implements BeforeEachCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendingExtension.class);

  private final SpendApiClient spendApiClient = new SpendApiClient();

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
      AnnotationSupport.findAnnotation(
              context.getRequiredTestMethod(),
              User.class
      ).ifPresent(userAnnotation -> {
          for (Spending spending : userAnnotation.spendings()) {
              context.getStore(NAMESPACE).put(
                      context.getUniqueId(),
                      spendApiClient.addSpend(createSpendJson(userAnnotation, spending))
              );
              break; // он здесь нужен из-за того, что мы пока обрабатываем только один spending
          }
      });
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(SpendJson.class);
  }

  @Override
  public SpendJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return createdSpending();
  }

  private SpendJson createSpendJson(User userAnnotation, Spending spending) {
      CategoryJson categoryJson = new CategoryJson(
              null,
              spending.category(),
              userAnnotation.username(),
              false
      );

      return new SpendJson(
              null,
              new Date(),
              categoryJson,
              spending.currency(),
              spending.amount(),
              spending.description(),
              userAnnotation.username()
      );
  }

    public static SpendJson createdSpending() {
        final ExtensionContext methodContext = context();
        return methodContext.getStore(NAMESPACE)
                .get(methodContext.getUniqueId(), SpendJson.class);
    }
}
