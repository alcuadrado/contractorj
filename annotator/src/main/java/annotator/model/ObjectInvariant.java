package annotator.model;

import java.util.List;

public class ObjectInvariant extends Invariant {

  ObjectInvariant(final List<String> conditions) {

    super(conditions);
  }

  @Override
  public String toString() {

    return "ObjectInvariant " + super.toString();
  }

  @Override
  public String toMethod() {

    return "public boolean inv() " + super.toMethod();
  }
}
