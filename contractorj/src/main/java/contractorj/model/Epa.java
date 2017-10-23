package contractorj.model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Epa {

  private final List<Transition> transitions = new LinkedList<>();

  private final String className;

  private final State initialState;

  private final Set<State> states = new HashSet<>();

  public Epa(String className, final State initialState) {

    this.className = className;
    this.initialState = initialState;
  }

  public synchronized void addTransition(Transition transition) {

    if (transition.getSource().isConstructorsState() && transition.isThrowing()){
      // if there is a throwing transition from the constructor, we return to the constructor.
      //  State source, Action action, State target, boolean isUncertain, final boolean isThrowing) {
      transition = new Transition(transition.getSource(),transition.getAction(),transition.getSource(),transition.isUncertain(), transition.isThrowing());
    } else {
      states.add(transition.getSource());
      states.add(transition.getTarget());
    }

    transitions.add(transition);
  }

  public List<Transition> getTransitions() {

    return transitions;
  }

  public String getClassName() {

    return className;
  }

  public State getInitialState() {

    return initialState;
  }

  public Set<State> getStates() {

    return states;
  }

  public Set<Action> getActions() {
    return states
        .stream()
        .flatMap(
            state ->
                Stream.concat(
                    state.getEnabledActions().stream(), state.getDisabledActions().stream()))
        .collect(Collectors.toSet());
  }

  public Set<Transition> getTransitionsWithSource(State source) {
    return transitions
        .stream()
        .filter(transition -> transition.getSource().equals(source))
        .collect(Collectors.toSet());
  }
}
