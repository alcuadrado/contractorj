package contractorj.construction.queries.transition;

import contractorj.construction.queries.Query;
import contractorj.model.Action;
import contractorj.model.State;
import j2bpl.Method;

public class ThrowingTransitionQuery extends TransitionQuery {

    public ThrowingTransitionQuery(final State source,
                                   final Action transition,
                                   final State target,
                                   final Method invariant) {

        super(source, transition, target, invariant);
    }

    @Override
    protected String getTransitionCallExceptionHandling() {

        return "assume $Exception != null;\n" +
                "$Exception := null;";
    }

    @Override
    public String getName() {

        return super.getName() + Query.NAME_PART_SEPARATOR + "throwing";
    }
}
