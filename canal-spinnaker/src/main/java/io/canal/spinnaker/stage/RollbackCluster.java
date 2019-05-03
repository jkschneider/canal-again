package io.canal.spinnaker.stage;

import io.canal.spinnaker.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@Getter
public class RollbackCluster extends Stage {
  public static final String TYPE = "rollback";

  private String account;
  private Collection<String> regions;
  private final String cluster;

  private RollbackCluster(String name, String account, Collection<String> regions, String cluster) {
    super(name);
    this.account = account;
    this.regions = regions;
    this.cluster = cluster;
  }

  @Override
  public String getType() {
    return TYPE;
  }

  @Override
  public void withDefaults(Defaults defaults) {
    this.account = defaults.orDefaultAccount(account);
    this.regions = this.regions == null ? Collections.singletonList(defaults.orDefaultRegion(null)) :
      this.regions;
  }

  @Accessors(fluent = true, chain = true)
  @Setter
  public static class Builder extends StageBuilder<Builder, RollbackCluster> {
    private String account;
    private Collection<String> regions = Collections.emptyList();
    private String cluster;

    public Builder() {
      super("Rollback Cluster");
    }

    public Builder regions(String... regions) {
      this.regions = Arrays.asList(regions);
      return this;
    }

    public RollbackCluster build() {
      return new RollbackCluster(name, account, regions, cluster);
    }
  }
}
