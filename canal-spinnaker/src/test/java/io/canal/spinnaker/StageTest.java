package io.canal.spinnaker;

import io.canal.spinnaker.stage.DeployService;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Collection;

import static io.canal.spinnaker.Stage.*;

class StageTest {
  @Test
  void stageGraph() {
    new Pipeline() {
      @Override
      public Stage stages() {
        Defaults devDefaults = defaults()
          .account("montclair")
          .region("development > development")
          .build();

        devDefaults.applyTo(waitFor(Duration.ofMinutes(1)).build())
          .then(cloudFoundry().deploy().name("Deploy to dev"));

        Stage graph = devDefaults.applyTo( // option 1
          waitFor(Duration.ofMinutes(1))
            .then(
              createService("Deploy Mongo"),
              createService("Deploy Rabbit"),
              createService("Deploy MySQL")
            )
            .then(cloudFoundry().deploy().name("Deploy to Dev"))
            .then(waitFor(Duration.ofMinutes(2)).name("Cool Off"))
            .then(rollback().name("Rollback"))
        );

        System.out.println(graph.toJson());

        return graph;
      }

      private DeployService createService(String serviceName) {
        return deployService()
          .name(serviceName)
          .build();
      }
    }.stages();
  }

  @Test
  void toJson() {
  }
}

class AcmePipeline extends Pipeline {
  @Override
  public Stage stages() {
    return null;
  }

  @Override
  public Collection<Trigger> triggers() {
    return super.triggers();
  }
}
