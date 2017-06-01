package examples.inheritance;

public class Herbivoro extends Mamifero {
    public void comer(){
        comida++;
    }

    public void testInvocacionesVirtuales(Herbivoro herb){
        herb.comer();
        herb.respirar(); // SOOT SABE QUE ESTA EXPRESION DE INVOCACION VIRTUAL LLEVA AL METODO Mamifero.respirar()
    }
    
    /* JIMPLE
    public void testInvocacionesVirtuales(examples.inheritance.Herbivoro)
    {
        examples.inheritance.Herbivoro r0, r1;

        r0 := @this: examples.inheritance.Herbivoro;
        r1 := @parameter0: examples.inheritance.Herbivoro;
        virtualinvoke r1.<examples.inheritance.Herbivoro: void comer()>();
        virtualinvoke r1.<examples.inheritance.Herbivoro: void respirar()>();
        return;
    }*/
    /* BOOGIE
        procedure examples.inheritance.Herbivoro#testInvocacionesVirtuales$examples.inheritance.Herbivoro($this : Ref, param00 : Ref)
        {
            var r0 : Ref;
            var r1 : Ref;


            r1 := param00;

            examples.inheritance.Herbivoro#testInvocacionesVirtuales$examples.inheritance.Herbivoro_0:
                r0 := $this;

                call examples.inheritance.Herbivoro#comer(r1);
                if ($Exception != null) {
                    return;
                }
                call examples.inheritance.Mamifero#respirar(r1);
                if ($Exception != null) {
                    return;
                }
                return;
        }
     */

    public void testInvocacionesVirtuales2(Mamifero m){
        m.respirar();
        m.comer();
    }

    // para testear como se traducen estas funciones de la clase Object
    public void testObjectClass(){
        this.notifyAll();
    }
}
