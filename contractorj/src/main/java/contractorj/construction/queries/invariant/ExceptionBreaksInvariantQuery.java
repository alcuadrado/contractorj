package contractorj.construction.queries.invariant;

import contractorj.construction.queries.Query;
import contractorj.model.Action;
import contractorj.model.State;
import jbct.model.Method;

public class ExceptionBreaksInvariantQuery extends InvariantQuery {

  public ExceptionBreaksInvariantQuery(
      final State source, final Action mainAction, final Method invariant) {

    super(source, mainAction, invariant);
  }

  @Override
  protected boolean throwsException() {

    return true;
  }

  @Override
  protected String getMainActionCallExceptionHandling() {

    return "assume $Exception != null;" + "$Exception := null;";
  }

  @Override
  public String getName() {

    return "exception_breaks_invariant" + Query.NAME_PART_SEPARATOR + super.getName();
  }
}
