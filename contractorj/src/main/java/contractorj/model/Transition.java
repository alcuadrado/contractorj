package contractorj.model;

public class Transition {

    private final State source;

    private final State target;

    private final Action action;

    private final boolean isUncertain;

    private final boolean isThrowing;

    public Transition(State source, Action action, State target, boolean isUncertain, final boolean isThrowing) {

        this.isThrowing = isThrowing;

        assert source.getEnabledActions().contains(action);

        this.source = source;
        this.target = target;
        this.action = action;
        this.isUncertain = isUncertain;
    }

    public State getSource() {

        return source;
    }

    public State getTarget() {

        return target;
    }

    public Action getAction() {

        return action;
    }

    public boolean isUncertain() {

        return isUncertain;
    }

    public boolean isThrowing() {

        return isThrowing;
    }
}
