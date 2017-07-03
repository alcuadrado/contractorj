package examples.inheritance;

public class Nieto extends Hijo {

    static public void testeandoUnMetodoSoloPadre(Nieto nieto){
        nieto.unMetodoSoloPadre();
    }

    /*procedure examples.inheritance.Nieto.testeandoUnMetodoSoloPadre$examples.inheritance.Nieto(param00 : Ref)
    {
        var r0 : Ref;


        r0 := param00;

        examples.inheritance.Nieto.testeandoUnMetodoSoloPadre$examples.inheritance.Nieto_0:

        call examples.inheritance.Padre#unMetodoSoloPadre(r0);
        if ($Exception != null) {
            return;
        }
        return;

    }*/

    static public void testeandoOtroMetodoPadre(Nieto nieto){
        nieto.otroMetodoPadre();
    }
}
