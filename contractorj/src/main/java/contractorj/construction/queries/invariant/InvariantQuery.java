package contractorj.construction.queries.invariant;

import contractorj.construction.corral.QueryResult;
import contractorj.construction.queries.Answer;
import contractorj.construction.queries.Query;
import contractorj.model.Action;
import contractorj.model.State;
import j2bpl.Method;

public abstract class InvariantQuery extends Query {

    protected InvariantQuery(final State source, final Action transition, final Method invariant) {

        super(source, transition, invariant);
    }

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
    protected String getQueryCore() {

        return getInvariantAssertion();
    }

}
