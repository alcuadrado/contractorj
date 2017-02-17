package examples;

public class PreconditionOnParametersTest {

  public static boolean PreconditionOnParametersTest_pre() {
    return true;
  }

  public PreconditionOnParametersTest() {}

  public boolean inv() {
    return true;
  }

  public boolean alwaysEnabledMethod_pre(Object p) {
    return p != null;
  }

  public void alwaysEnabledMethod(Object p) {}
}
