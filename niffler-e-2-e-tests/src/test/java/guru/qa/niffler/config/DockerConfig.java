package guru.qa.niffler.config;


import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

enum DockerConfig implements Config {
  INSTANCE;

  @Override
  public @Nonnull String frontUrl() {
    return "";
  }

  @Override
  public @Nonnull String authUrl() {
    return "";
  }

  @Override
  public @Nonnull String authJdbcUrl() {
    return "";
  }

  @Override
  public @Nonnull String gatewayUrl() {
    return "";
  }

  @Override
  public @Nonnull String userdataUrl() {
    return "";
  }

  @Override
  public @Nonnull String userdataJdbcUrl() {
    return "";
  }

  @Override
  public @Nonnull String spendUrl() {
    return "";
  }

  @Override
  public @Nonnull String spendJdbcUrl() {
    return "";
  }

  @NotNull
  @Override
  public String currencyGrpcAddress() {
    return "";
  }

  @Override
  public @Nonnull String currencyJdbcUrl() {
    return "";
  }

  @Override
  public @Nonnull String ghUrl() {
    return "";
  }
}
