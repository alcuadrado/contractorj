package contractorj.construction.queries.transition;

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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class TransitionQuery extends Query {

    private State target;

    public TransitionQuery(final State source, final Action mainAction, final State target, final Method invariant) {

        super(source, mainAction, invariant);

        this.target = target;
    }

    protected abstract boolean throwsException();

    @Override
    public Optional<Transition> getTransition(final Answer answer) {

        if (answer.equals(Answer.NO)) {
            return Optional.empty();
        }

        return Optional.of(new Transition(
                source,
                mainAction, target,
                answer.equals(Answer.MAYBE),
                throwsException()
        ));
    }

    @Override
    public String getName() {

        final String name = "transition" + NAME_PART_SEPARATOR + super.getName() + NAME_PART_SEPARATOR
                + "to" + NAME_PART_SEPARATOR + getStateName(target) + NAME_PART_SEPARATOR;

        return StringUtils.scapeIllegalIdentifierCharacters(name);
    }

    @Override
    public Answer getAnswer(final QueryResult queryResult) {

        switch (queryResult) {

            case TRUE_BUG:
                return Answer.YES;

            case NO_BUG:
                return Answer.NO;

            case MAYBE_BUG:
                return Answer.MAYBE;
        }

        throw new IllegalArgumentException("QueryResult " + queryResult.toString() + " is an error for query type "
                + getClass().getName());
    }

    @Override
    protected String getQueryCore() {

        return getStateGuardCalls(target, AFTER_MAIN_ACTION_ARGS_SUFFIX) + "\n" +
                "\n" +
                "\n" +

                getInvariantAssumption() + "\n" +
                "\n" +
                "\n" +

                getNegatedStateGuardAssertion(target) + "\n";
    }

    @Override
    protected List<Variable> getLocalVariables() {

        return Stream.concat(
                super.getLocalVariables().stream(),
                target.getAllActions().stream().map(action -> getVariableForMethodResult(action.precondition).get()))
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    protected List<Variable> getQueryArguments() {

        final List<Variable> arguments = super.getQueryArguments();

        for (final Action action : target.getAllActions()) {
            final List<Variable> actionArguments = getArgumentListForMethod(
                    action.method,
                    AFTER_MAIN_ACTION_ARGS_SUFFIX
            );

            if (!action.method.isStatic()) {
                actionArguments.remove(0);
            }

            arguments.addAll(actionArguments);
        }

        return arguments;
    }

}
