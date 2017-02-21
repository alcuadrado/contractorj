package contractorj.construction.queries.invariant;

import contractorj.construction.corral.QueryResult;
import contractorj.construction.queries.Answer;
import contractorj.construction.queries.Query;
import contractorj.model.Action;
import contractorj.model.State;
import contractorj.model.Transition;
import jbct.model.Method;
import java.util.Optional;

public abstract class InvariantQuery extends Query {

  protected InvariantQuery(final State source, final Action mainAction, final Method invariant) {

    super(source, mainAction, invariant);
  }

  protected abstract boolean throwsException();

  @Override
  public Answer getAnswer(final QueryResult queryResult) {

    switch (queryResult) {
      case TRUE_BUG:
        return Answer.YES;

      case NO_BUG:
        return Answer.NO;

      default:
        return Answer.MAYBE;
    }
  }

  @Override
  public Optional<Transition> getTransition(final Answer answer) {

    if (answer.equals(Answer.NO)) {
      return Optional.empty();
    }

    return Optional.of(
        new Transition(
            source, mainAction, State.ERROR, answer.equals(Answer.MAYBE), throwsException()));
  }

  @Override
  protected String getQueryCore() {

    return "query_assertion_1:\n" + getInvariantAssertion();
  }
}
