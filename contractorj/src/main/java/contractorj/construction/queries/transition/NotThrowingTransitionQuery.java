package contractorj.construction.queries.transition;

import contractorj.construction.queries.transition.TransitionQuery;
import contractorj.model.Action;
import contractorj.model.State;
import j2bpl.Method;

public class NotThrowingTransitionQuery extends TransitionQuery {

    public NotThrowingTransitionQuery(final State source,
                                      final Action transition,
                                      final State target,
                                      final Method invariant) {

        super(source, transition, target, invariant);
    }

    @Override
    protected String getTransitionCallExceptionHandling() {

        return "assume $Exception == null;";
    }

    @Override
    public String getName() {

        return super.getName() + NAME_PART_SEPARATOR + "not_throwing";
    }
}
