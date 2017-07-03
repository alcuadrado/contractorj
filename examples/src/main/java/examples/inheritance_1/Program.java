package examples.inheritance_1;

public class Program {
    public static void Increment(A a){
        a.Increment();
    }

    static void Main(){
        A b = new B();
        Increment(b);
        A c = new C();
        Increment(c);
    }
}
