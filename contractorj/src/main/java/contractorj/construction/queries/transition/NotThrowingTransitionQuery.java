package contractorj.construction.queries.transition;

import contractorj.model.Action;
import contractorj.model.State;
import jbct.model.Method;

public class NotThrowingTransitionQuery extends TransitionQuery {

  public NotThrowingTransitionQuery(
      final State source, final Action mainAction, final State target, final Method invariant) {

    super(source, mainAction, target, invariant);
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

    return super.getName() + NAME_PART_SEPARATOR + "not_throwing";
  }
}
