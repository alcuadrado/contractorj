package contractorj.model;

public class Transition implements Comparable<Transition> {

  private final State source;

  private final State target;

  private final Action action;

  private final boolean isUncertain;

  private final boolean isThrowing;

  public Transition(
      State source, Action action, State target, boolean isUncertain, final boolean isThrowing) {

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

  @Override
  public int compareTo(Transition o) {

    int compareBySourceState = o.source.getStateName().compareTo(source.getStateName());
    if (compareBySourceState == 0) {

      int compareByAction = o.action.compareTo(action);
      if (compareByAction == 0) {
        int compareByTarget = o.target.getStateName().compareTo(target.getStateName());
        if (compareByTarget == 0) {
          if (isThrowing() && !o.isThrowing()) return 1;
          else return -1;

        } else {
          return compareByTarget;
        }

      } else {
        return compareByAction;
      }
    } else {
      return compareBySourceState;
    }
  }
}
