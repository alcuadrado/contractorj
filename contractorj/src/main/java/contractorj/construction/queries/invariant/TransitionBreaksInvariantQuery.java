package contractorj.construction.queries.invariant;

import contractorj.model.Action;
import contractorj.model.State;
import j2bpl.Method;

public class TransitionBreaksInvariantQuery extends InvariantQuery {

    public TransitionBreaksInvariantQuery(final State source, final Action transition, final Method invariant) {

        super(source, transition, invariant);
    }

    @Override
    protected String getTransitionCallExceptionHandling() {

        return "assume $Exception == null;";
    }

    @Override
    public String getName() {

        return "transition_breaks_invariant" + NAME_PART_SEPARATOR + super.getName();
    }
}
