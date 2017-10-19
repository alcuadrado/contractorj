package contractorj.construction.queries.necessary_actions;

import contractorj.model.Action;
import contractorj.model.State;
import jbct.model.Method;

// this may not is the best implementation
// This is a new class because the logging is different
// It's a NecessarilyEnabledActionQuery with a State of one action
// it uses a state that's not part of the EPA

public class GlobalNecessarilyEnabledActionQuery extends NecessarilyEnabledActionQuery {
  public GlobalNecessarilyEnabledActionQuery(
      State source, Action mainAction, Action testedAction, Method invariant) {
    super(source, mainAction, testedAction, invariant);
  }

  @Override
  public String getName() {
    return "global_" + super.getName();
  }
}
