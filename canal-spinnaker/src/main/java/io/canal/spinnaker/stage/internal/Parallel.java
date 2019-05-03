package io.canal.spinnaker.stage.internal;

import io.canal.spinnaker.Stage;

public class Parallel extends Stage {
  public Parallel(Stage[] stages) {
    super("Parallel");
    for (Stage stage : stages) {
      stage.then(this);
    }
  }

  @Override
  public String getType() {
    return "__parallel__";
  }
}
