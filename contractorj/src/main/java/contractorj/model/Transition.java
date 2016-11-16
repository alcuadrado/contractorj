package contractorj.model;

public class Transition {

    public final State source;

    public final State target;

    public final Action transition;

    public final boolean isUncertain;

    public final boolean isThrowing;

    public Transition(State source, State target, Action transition, boolean isUncertain, final boolean isThrowing) {

        this.isThrowing = isThrowing;

        assert source.enabledActions.contains(transition);

        this.source = source;
        this.target = target;
        this.transition = transition;
        this.isUncertain = isUncertain;
    }


}
