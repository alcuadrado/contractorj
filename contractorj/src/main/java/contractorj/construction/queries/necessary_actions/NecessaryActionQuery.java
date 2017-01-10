package contractorj.construction.queries.necessary_actions;

import contractorj.construction.corral.QueryResult;
import contractorj.construction.queries.Answer;
import contractorj.construction.queries.Query;
import contractorj.construction.queries.Variable;
import contractorj.model.Action;
import contractorj.model.State;
import contractorj.model.Transition;
import j2bpl.Method;
import j2bpl.StringUtils;

import java.util.List;
import java.util.Optional;

public abstract class NecessaryActionQuery extends Query {

    private final Action testedAction;

    public NecessaryActionQuery(final State source,
                                final Action mainAction,
                                final Action testedAction,
                                final Method invariant) {

        super(source, mainAction, invariant);
        this.testedAction = testedAction;
    }

    protected abstract boolean isTestingEnabledness();

    @Override
    public Optional<Transition> getTransition(final Answer answer) {

        return Optional.empty();
    }

    public Action getTestedAction() {
        return testedAction;
    }

    @Override
    public Answer getAnswer(final QueryResult queryResult) {

        if (queryResult.equals(QueryResult.NO_BUG)) {
            return Answer.YES;
        }

        return Answer.NO;
    }

    @Override
    public String getName() {

        final String name = super.getName() + NAME_PART_SEPARATOR
                + "testing" + NAME_PART_SEPARATOR + testedAction.getMethod().getJavaNameWithArgumentTypes();

        return StringUtils.scapeIllegalIdentifierCharacters(name);
    }

    @Override
    protected String getQueryCore() {

        return getStatePreconditionCall(testedAction)
                .map(
                        s -> s + "\n"
                                + "\n"
                                + "\n"
                ).orElse("")

                + "query_assertion_1:\n"
                + getInvariantAssertion() + "\n"
                + "\n"
                + "\n"

                + "query_assertion_2:\n"
                + "assert " + (isTestingEnabledness() ? "" : "!") + getTestedActionReturnVariable().name + ";";
    }

    private Variable getTestedActionReturnVariable() {

        if (!testedAction.getStatePrecondition().isPresent()) {
            throw new IllegalStateException(testedAction + " has no state precondition so it's always enabled");
        }

        return getVariableForStatePreconditionResult(testedAction).get();
    }

    @Override
    protected List<Variable> getLocalVariables() {

        final Variable testedActionReturn = getTestedActionReturnVariable();
        final List<Variable> localVariables = super.getLocalVariables();

        if (!localVariables.contains(testedActionReturn)) {
            localVariables.add(testedActionReturn);
        }

        return localVariables;
    }

    @Override
    protected String getMainActionCallExceptionHandling() {

        return "$Exception := null;";
    }

}
