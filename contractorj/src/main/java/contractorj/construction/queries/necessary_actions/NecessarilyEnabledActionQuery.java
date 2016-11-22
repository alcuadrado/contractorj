package contractorj.construction.queries.necessary_actions;

import contractorj.construction.queries.Query;
import contractorj.model.Action;
import contractorj.model.State;
import j2bpl.Method;

public class NecessarilyEnabledActionQuery extends NecessaryActionQuery {

    public NecessarilyEnabledActionQuery(final State source,
                                         final Action transition,
                                         final Action testedAction,
                                         final Method invariant) {

        super(source, transition, testedAction, invariant);
    }

    @Override
    protected boolean isTestingEnabledness() {

        return true;
    }

    @Override
    public String getName() {

        return "necessarily_enabled_test" + Query.NAME_PART_SEPARATOR + super.getName();
    }
}
