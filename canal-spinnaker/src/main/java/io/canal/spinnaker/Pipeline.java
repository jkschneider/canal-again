package io.canal.spinnaker;

import java.util.Collection;
import java.util.Collections;

public abstract class Pipeline {
  public abstract Stage stages();

  public Collection<Trigger> triggers() {
    return Collections.emptyList();
  }
}
