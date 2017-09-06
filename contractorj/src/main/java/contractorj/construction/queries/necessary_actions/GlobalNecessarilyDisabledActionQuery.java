package contractorj.construction.queries.necessary_actions;

import contractorj.model.Action;
import contractorj.model.State;
import jbct.model.Method;

/**
 * Created by Usuario on 04/09/2017.
 */
public class GlobalNecessarilyDisabledActionQuery extends NecessarilyDisabledActionQuery {
    public GlobalNecessarilyDisabledActionQuery(State source, Action mainAction, Action testedAction, Method invariant) {
        super(source, mainAction, testedAction, invariant);
    }
}
