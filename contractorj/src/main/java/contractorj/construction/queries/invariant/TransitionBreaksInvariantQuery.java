package contractorj.construction.queries.invariant;

import contractorj.model.Action;
import contractorj.model.State;
import jbct.model.Method;

public class TransitionBreaksInvariantQuery extends InvariantQuery {

  public TransitionBreaksInvariantQuery(
      final State source, final Action mainAction, final Method invariant) {

    super(source, mainAction, invariant);
  }

  @Override
  protected boolean throwsException() {

    return false;
  }

  @Override
  protected String getMainActionCallExceptionHandling() {

    return "assume $Exception == null;";
  }

  @Override
  public String getName() {

    return "transition_breaks_invariant" + NAME_PART_SEPARATOR + super.getName();
  }
}
