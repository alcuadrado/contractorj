package contractorj.epas;

import com.google.common.base.Joiner;

import java.util.Set;

public class State {

    public final Set<Action> actions;

    public State(Set<Action> actions) {

        this.actions = actions;
    }

    @Override
    public String toString() {

        return "State{" + Joiner.on(", ").join(actions) + '}';
    }
}
