package contractorj.model;

public class Transition {

    public final State source;

    public final State target;

    public final Action action;

    public final boolean isUncertain;

    public final boolean isThrowing;

    public Transition(State source, Action action, State target, boolean isUncertain, final boolean isThrowing) {

        this.isThrowing = isThrowing;

        assert source.enabledActions.contains(action);

        this.source = source;
        this.target = target;
        this.action = action;
        this.isUncertain = isUncertain;
    }

}
