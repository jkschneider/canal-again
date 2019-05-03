package io.canal.spinnaker.stage;

import io.canal.spinnaker.Stage;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.annotation.Nullable;

public class ManualJudgment extends Stage {
  public static final String TYPE = "manualJudgment";

  @Nullable
  private final String instructions;

  @Nullable
  private final String judgmentInputs;

  private ManualJudgment(String name, String instructions, String judgmentInputs) {
    super(name);
    this.instructions = instructions;
    this.judgmentInputs = judgmentInputs;
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
  public static class Builder extends StageBuilder<Builder, ManualJudgment> {
    private String instructions;
    private String judgementInputs;

    public Builder() {
      super("Manual Judgment");
    }

    @Override
    public ManualJudgment build() {
      return new ManualJudgment(name, instructions, judgementInputs);
    }
  }
}
