package io.canal.spinnaker;

import com.squareup.moshi.*;
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory;
import io.canal.spinnaker.stage.*;
import io.canal.spinnaker.stage.cf.CloudFoundryDeploy;
import io.canal.spinnaker.stage.cf.CloudFoundryStages;
import io.canal.spinnaker.stage.internal.Parallel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Delegate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public abstract class Stage {
  private final Collection<Stage> precededBy = new ArrayList<>();

  Collection<Stage> getPrecededBy() {
    return precededBy;
  }

  @Nonnull
  private String name;

  public Stage(String name) {
    this.name = name;
  }

  public abstract String getType();

  public abstract void withDefaults(Defaults defaults);

  public final Stage then(Stage... stages) {
    for (Stage stage : stages) {
      stage.precededBy.add(this);
    }

    switch (stages.length) {
      case 0:
        return this;
      case 1:
        return stages[0];
      default:
        return new Parallel(stages);
    }
  }

  public final Stage then(StageBuilder<?, ?>... stages) {
    return then(Arrays.stream(stages).map(StageBuilder::build).toArray(Stage[]::new));
  }

  public static Defaults.Builder defaults() {
    return Defaults.builder();
  }

  public static RollbackCluster.Builder rollback() {
    return new RollbackCluster.Builder();
  }

  public static Wait.Builder waitFor(Duration waitTime) {
    return new Wait.Builder()
      .time(waitTime);
  }

  public static Wait.Builder waitFor(String waitTime) {
    return new Wait.Builder()
      .time(waitTime);
  }

  public static CloudFoundryStages cloudFoundry() {
    return CloudFoundryStages.INSTANCE;
  }

  public static DeployService.Builder deployService() {
    return new DeployService.Builder();
  }

  public static Webhook.Builder webhook() {
    return new Webhook.Builder();
  }

  public final String toJson() {
    return new Moshi.Builder()
      .add(new StageAdapter())
      .build()
      .adapter(Stage.class)
      .toJson(this);
  }

  public static abstract class StageBuilder<B, S extends Stage> {
    protected String name;

    public StageBuilder(String defaultName) {
      this.name = defaultName;
    }

    public B name(String name) {
      this.name = name;
      return (B) this;
    }

    public abstract S build();

    public final Stage then(Stage... stages) {
      return build().then(stages);
    }

    public final Stage then(StageBuilder<?, ?>... stages) {
      return build().then(stages);
    }
  }

  @Builder(builderClassName = "Builder")
  public static class Defaults {
    static Defaults NO_DEFAULTS = Defaults.builder().build();

    @Nullable
    protected String account;

    @Nullable
    protected String region;

    public <S extends Stage> S applyTo(S stage) {
      stage.withDefaults(this);
      return stage;
    }

    public Stage[] applyTo(Stage... stages) {
      for (Stage stage : stages) {
        stage.withDefaults(this);
      }
      return stages;
    }

    public String orDefaultAccount(@Nullable String account) {
      return account == null ? this.account : account;
    }

    public String orDefaultRegion(@Nullable String region) {
      return region == null ? this.region : region;
    }
  }
}

class StageAdapter {
  private final JsonAdapter<StageWithRelationships> stageJsonAdapter = new Moshi.Builder()
    .add(PolymorphicJsonAdapterFactory.of(Stage.class, "type")
      .withSubtype(CloudFoundryDeploy.class, CloudFoundryDeploy.TYPE)
      .withSubtype(DeployService.class, DeployService.TYPE)
      .withSubtype(ManualJudgment.class, ManualJudgment.TYPE)
      .withSubtype(RollbackCluster.class, RollbackCluster.TYPE)
      .withSubtype(Wait.class, Wait.TYPE)
      .withSubtype(Webhook.class, Webhook.TYPE)
    )
    .add(PolymorphicJsonAdapterFactory.of(Stage.class, "type")
      .withSubtype(CloudFoundryDeploy.class, CloudFoundryDeploy.TYPE)
      .withSubtype(DeployService.class, DeployService.TYPE)
      .withSubtype(ManualJudgment.class, ManualJudgment.TYPE)
      .withSubtype(RollbackCluster.class, RollbackCluster.TYPE)
      .withSubtype(Wait.class, Wait.TYPE)
      .withSubtype(Webhook.class, Webhook.TYPE)
    )
//    .add(PolymorphicJsonAdapterFactory.of(Cluster.class, "cloudProvider")
//      .withSubtype(CloudFoundryCluster.class, CloudFoundryCluster.CLOUD_PROVIDER))
    .build()
    .adapter(StageWithRelationships.class);

  @ToJson
  public void writeStageGraph(JsonWriter writer, Stage stages) throws IOException {
    Map<Stage, String> stageIds = new HashMap<>();
    assignStageIds(stages, stageIds);

    writer.beginObject();

    writer.name("stages");
    writer.beginArray();
    writeStages(writer, stages, stageIds);
    writer.endArray();

    writer.endObject();
  }

  @FromJson
  public Stage fromJson(JsonReader reader) {
    throw new UnsupportedOperationException("deserialization of pipelines is not supported");
  }

  private void writeStages(JsonWriter writer, Stage stage, Map<Stage, String> stageGraphIds) throws IOException {
    for (Stage subgraph : stage.getPrecededBy()) {
      writeStages(writer, subgraph, stageGraphIds);
    }

    if (!(stage instanceof Parallel)) {
      writer.value(stageJsonAdapter.toJson(new StageWithRelationships(
        stage.getPrecededBy().stream().map(stageGraphIds::get).collect(Collectors.toList()), stage)));
    }
  }

  private void assignStageIds(Stage stage, Map<Stage, String> stageGraphIds) {
    for (Stage subgraph : stage.getPrecededBy()) {
      if(!(stage instanceof Parallel) && stageGraphIds.get(stage) == null) {
        stageGraphIds.put(stage, Integer.toString(stageGraphIds.size() + 1));
      }
      assignStageIds(subgraph, stageGraphIds);
    }
  }

  private static class StageWithRelationships {
    @Getter
    private final Collection<String> requisiteStageRefIds;

    @Delegate
    private final Stage stage;

    StageWithRelationships(Collection<String> requisiteStageRefIds, Stage stage) {
      this.requisiteStageRefIds = requisiteStageRefIds;
      this.stage = stage;
    }
  }
}
