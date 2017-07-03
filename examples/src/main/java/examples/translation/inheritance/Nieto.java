package examples.translation.inheritance;

public class Nieto extends Hijo {

    static public void testeandoUnMetodoSoloPadre(Nieto nieto){
        nieto.unMetodoSoloPadre();
    }

    /*procedure examples.translation.inheritance.Nieto.testeandoUnMetodoSoloPadre$examples.translation.inheritance.Nieto(param00 : Ref)
    {
        var r0 : Ref;


        r0 := param00;

        examples.translation.inheritance.Nieto.testeandoUnMetodoSoloPadre$examples.translation.inheritance.Nieto_0:

        call examples.translation.inheritance.Padre#unMetodoSoloPadre(r0);
        if ($Exception != null) {
            return;
        }
        return;

    }*/

    static public void testeandoOtroMetodoPadre(Nieto nieto){
        nieto.otroMetodoPadre();
    }
}
