package examples.translation.inheritance.virtualcall;

/**
 * Created by Usuario on 07/06/2017.
 */
public class A {
    public void unMetodoVirtual(){

    }

    public static void invocacionUnMetodoVirtual(A a){
        a.unMetodoVirtual();
    }

    public static void testInvocacionA()
    {
        A a = new A();
        invocacionUnMetodoVirtual(a);
    }

    public static void testInvocacionB()
    {
        A a = new B();
        invocacionUnMetodoVirtual(a);
    }

    public static void testInvocacionC()
    {
        A a = new C();
        invocacionUnMetodoVirtual(a);
    }

    public static void testInvocacionD()
    {
        A a = new D();
        invocacionUnMetodoVirtual(a);
    }

    public static void testInvocacionE()
    {
        A a = new E();
        invocacionUnMetodoVirtual(a);
    }
}
