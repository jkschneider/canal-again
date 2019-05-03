package io.canal.spinnaker.stage.cf;

import io.canal.spinnaker.Stage;
import io.canal.spinnaker.stage.DeployStrategy;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.annotation.Nullable;

@Getter
public class CloudFoundryDeploy extends Stage {
  public static final String TYPE = "deploy";

  private String account;
  private String region;

  @Nullable
  private final String stack;

  @Nullable
  private final String detail;

  private final boolean startApplication;

  private final DeployStrategy strategy;

  private CloudFoundryDeploy(String name, String account, String region, @Nullable String stack,
                             @Nullable String detail, boolean startApplication,
                             DeployStrategy strategy) {
    super(name);
    this.account = account;
    this.region = region;
    this.stack = stack;
    this.detail = detail;
    this.startApplication = startApplication;
    this.strategy = strategy;
  }

  @Override
  public String getType() {
    return TYPE;
  }

  @Override
  public void withDefaults(Defaults defaults) {
    this.account = defaults.orDefaultAccount(account);
    this.region = defaults.orDefaultRegion(region);
  }

  @Accessors(fluent = true, chain = true)
  @Setter
  public static class Builder extends StageBuilder<Builder, CloudFoundryDeploy> {
    private String account;
    private String region;

    @Nullable
    private String stack;

    @Nullable
    private String detail;

    private boolean startApplication;

    private DeployStrategy strategy;

    public Builder() {
      super("Deploy");
    }

    @Override
    public CloudFoundryDeploy build() {
      return new CloudFoundryDeploy(name, account, region, stack, detail, startApplication,
        strategy);
    }
  }
}
