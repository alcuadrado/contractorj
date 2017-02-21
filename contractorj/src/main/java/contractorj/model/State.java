package contractorj.model;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class State {

  private final Set<Action> enabledActions;

  private final Set<Action> disabledActions;

  private boolean isConstructorsState = false;

  public static final State ERROR = new State(new HashSet<>(), new HashSet<>());

  public State(Set<Action> enabledActions, Set<Action> disabledActions) {
    this.enabledActions = enabledActions;
    this.disabledActions = disabledActions;

    if (!Sets.intersection(enabledActions, disabledActions).isEmpty()) {
      throw new IllegalArgumentException(
          "Invalid state: enabled and disabled actions can't overlap");
    }

    ensureAllOrNoneActionsAreConstructors();
  }

  public boolean isConstructorsState() {
    return isConstructorsState;
  }

  public Set<Action> getAllActions() {
    return Sets.union(getEnabledActions(), getDisabledActions());
  }

  private void ensureAllOrNoneActionsAreConstructors() {

    long numberOfConstructors =
        getAllActions().stream().filter(action -> action.getMethod().isConstructor()).count();

    if (numberOfConstructors != 0 && numberOfConstructors != getTotalNumberOfActions()) {
      throw new IllegalArgumentException(
          "Invalid state: can't mix methods and constructors." + " State " + this.toString());
    }

    if (numberOfConstructors > 0) {
      isConstructorsState = true;
    }
  }

  private int getTotalNumberOfActions() {

    return getEnabledActions().size() + getDisabledActions().size();
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
    return Objects.equals(getEnabledActions(), state.getEnabledActions())
        && Objects.equals(getDisabledActions(), state.getDisabledActions());
  }

  @Override
  public int hashCode() {

    return Objects.hash(getEnabledActions(), getDisabledActions());
  }

  @Override
  public String toString() {

    return "State{"
        + "enabledActions="
        + Joiner.on(", ").join(getEnabledActions())
        + ", disabledActions="
        + Joiner.on(", ").join(getDisabledActions())
        + '}';
  }

  public Set<Action> getEnabledActions() {

    return enabledActions;
  }

  public Set<Action> getDisabledActions() {

    return disabledActions;
  }
}
