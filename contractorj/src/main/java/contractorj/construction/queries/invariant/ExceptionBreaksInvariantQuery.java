package contractorj.construction.queries.invariant;

import contractorj.construction.queries.Query;
import contractorj.model.Action;
import contractorj.model.State;
import j2bpl.Method;

public class ExceptionBreaksInvariantQuery extends InvariantQuery {

    public ExceptionBreaksInvariantQuery(final State source, final Action transition, final Method invariant) {

        super(source, transition, invariant);
    }

    @Override
    protected String getTransitionCallExceptionHandling() {

        return "assume $Exception != null;"
                + "$Exception := null;";
    }

    @Override
    public String getName() {

        return "exception_breaks_invariant" + Query.NAME_PART_SEPARATOR + super.getName();
    }
}
