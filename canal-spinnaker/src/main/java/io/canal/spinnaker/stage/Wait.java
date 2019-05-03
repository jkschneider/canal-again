package io.canal.spinnaker.stage;

import io.canal.spinnaker.Stage;
import lombok.Getter;

import java.time.Duration;

@Getter
public class Wait extends Stage {
  public static final String TYPE = "wait";

  private final String waitTime;

  private Wait(String name, String waitTime) {
    super(name);
    this.waitTime = waitTime;
  }

  @Override
  public String getType() {
    return TYPE;
  }

  @Override
  public void withDefaults(Defaults defaults) {
  }

  public static class Builder extends StageBuilder<Builder, Wait> {
    private String waitTime = "30";

    public Builder() {
      super("Wait");
    }

    public Builder time(Duration waitTime) {
      this.waitTime = Long.toString(waitTime.getSeconds());
      return this;
    }

    public Builder time(String waitTime) {
      this.waitTime = waitTime;
      return this;
    }

    public Wait build() {
      return new Wait(name, waitTime);
    }
  }
}
