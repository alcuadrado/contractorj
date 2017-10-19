package examples;

public class InstanceOf {
  // Exception in thread "main" java.lang.UnsupportedOperationException: Unsupported value type soot.jimple.internal.JInstanceOfExpr
  // JBCT no sabe traducir esto a boogie, entra en un defaultCase
  public void instanceOfTest1(Object ob) {
    //if (ob instanceof Controlador){
    //    ((Controlador)ob).abrir();
    //}
  }
}
