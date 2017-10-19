package contractorj.model;

import java.util.Objects;
import java.util.Optional;
import jbct.model.Method;

/** An actions models a method with its precondition. */
public class Action implements Comparable<Action> {

  private final Method statePrecondition;

  private final Method paramsPrecondition;

  private final Method method;

  public Action(Method method, Method statePrecondition, Method paramsPrecondition) {
    this.method = method;
    this.statePrecondition = statePrecondition;
    this.paramsPrecondition = paramsPrecondition;
  }

  @Override
  public String toString() {

    return getMethod().getJavaNameWithArgumentTypes();
  }

  @Override
  public boolean equals(final Object o) {

    if (this == o) {
      return true;
    }
    if (!(o instanceof Action)) {
      return false;
    }
    final Action action = (Action) o;
    return Objects.equals(statePrecondition, action.statePrecondition)
        && Objects.equals(paramsPrecondition, action.paramsPrecondition)
        && Objects.equals(method, action.method);
  }

  @Override
  public int hashCode() {

    return Objects.hash(statePrecondition, paramsPrecondition, method);
  }

  public Method getPrecondition() {

    return statePrecondition;
  }

  public Method getMethod() {

    return method;
  }

  public Optional<Method> getStatePrecondition() {

    return Optional.ofNullable(statePrecondition);
  }

  public Optional<Method> getParamsPrecondition() {

    return Optional.ofNullable(paramsPrecondition);
  }

  @Override
  public int compareTo(Action a) {
    return method.getJavaNameWithArgumentTypes().compareTo(a.method.getJavaNameWithArgumentTypes());
  }
}
