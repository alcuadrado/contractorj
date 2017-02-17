package annotator;

import java.util.List;

class StatePrecondition extends Invariant {

  private final String methodName;

  StatePrecondition(final List<String> conditions, final String methodName) {

    super(conditions);
    this.methodName = methodName;
  }

  @Override
  public String toString() {

    return "StatePrecondition(for=" + methodName + ") " + super.toString();
  }

  @Override
  public String toMethod() {

    return "public boolean " + methodName + "_pre() " + super.toMethod();
  }
}
