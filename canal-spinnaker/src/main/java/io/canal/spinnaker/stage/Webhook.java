package io.canal.spinnaker.stage;

import io.canal.spinnaker.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
public class Webhook extends Stage {
  public static final String TYPE = "webhook";

  private final String method;
  private final String url;
  private final String user;
  private final boolean waitForCompletion;

  private Webhook(String name, String method, String url, String user, boolean waitForCompletion) {
    super(name);
    this.method = method;
    this.url = url;
    this.user = user;
    this.waitForCompletion = waitForCompletion;
  }

  @Override
  public String getType() {
    return TYPE;
  }

  @Override
  public void withDefaults(Defaults defaults) {
  }

  @Accessors(fluent = true, chain = true)
  @Setter
  public static class Builder extends StageBuilder<Builder, Webhook> {
    private String method = "GET";
    private String url;
    private String user;
    private boolean waitForCompletion = true;

    public Builder() {
      super("Webhook");
    }

    public Webhook build() {
      return new Webhook(name, method, url, user, waitForCompletion);
    }
  }
}