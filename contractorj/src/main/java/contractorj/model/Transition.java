package contractorj.model;

public class Transition {

    public final State source;

    public final State target;

    public final Action transition;

    public final boolean isUncertain;

    public Transition(State source, State target, Action transition, boolean isUncertain) {

        assert source.enabledActions.contains(transition);

        this.source = source;
        this.target = target;
        this.transition = transition;
        this.isUncertain = isUncertain;
    }


}
