package io.canal.spinnaker.stage.cf;

public class CloudFoundryStages {
  public static final CloudFoundryStages INSTANCE = new CloudFoundryStages();

  private CloudFoundryStages() {
  }

  public CloudFoundryDeploy.Builder deploy() {
    return new CloudFoundryDeploy.Builder();
  }
}
