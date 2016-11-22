package contractorj.model;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class State {

    public final Set<Action> enabledActions;

    public final Set<Action> disabledActions;

    private boolean isConstructorsState = false;

    public static State ERROR = new State(new HashSet<>(), new HashSet<>());

    public State(Set<Action> enabledActions, Set<Action> disabledActions) {
        this.enabledActions = enabledActions;
        this.disabledActions = disabledActions;

        if (!Sets.intersection(enabledActions, disabledActions).isEmpty()) {
            throw new IllegalArgumentException("Invalid state: enabled and disabled actions can't overlap");
        }

        ensureAllOrNoneActionsAreConstructors();
    }

    public boolean isConstructorsState() {
        return isConstructorsState;
    }

    public Set<Action> getAllActions() {
        return Sets.union(enabledActions, disabledActions);
    }

    private void ensureAllOrNoneActionsAreConstructors() {

        long numberOfConstructors = getAllActions().stream()
                .filter(action -> action.method.isConstructor())
                .count();

        if (numberOfConstructors != 0 && numberOfConstructors != enabledActions.size() + disabledActions.size()) {
            throw new IllegalArgumentException("Invalid state: can't mix methods and constructors");
        }

        if (numberOfConstructors > 0) {
            isConstructorsState = true;
        }
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }
        if (!(o instanceof State)) {
            return false;
        }

        final State state = (State) o;
        return Objects.equals(enabledActions, state.enabledActions) &&
                Objects.equals(disabledActions, state.disabledActions);
    }

    @Override
    public int hashCode() {

        return Objects.hash(enabledActions, disabledActions);
    }

    @Override
    public String toString() {

        return "State{" +
                "enabledActions=" + Joiner.on(", ").join(enabledActions) +
                ", disabledActions=" + Joiner.on(", ").join(disabledActions) +
                '}';
    }
}
