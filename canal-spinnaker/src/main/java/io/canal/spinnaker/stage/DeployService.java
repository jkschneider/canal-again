package io.canal.spinnaker.stage;

import io.canal.spinnaker.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
public class DeployService extends Stage {
  public static final String TYPE = "deployService";

  private final String provider = "cloudFoundry";
  private String account;
  private String region;

  private DeployService(String name, String account, String region) {
    super(name);
    this.account = account;
    this.region = region;
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
  public static class Builder extends StageBuilder<Builder, DeployService> {
    private String account;
    private String region;

    public Builder() {
      super("Deploy Service");
    }

    public DeployService build() {
      return new DeployService(name, account, region);
    }
  }
}
