package examples.inheritance;

public class Padre {
    public int j;
    public int h;
    public void metodoA(){
        j = 10;
    }
    public int k;

    public void unMetodoSoloPadre(){
        k = 1000;
    }

    public void otroMetodoPadre(){
        k = 1000;
    }

    public void invocacion(Padre padre){
        padre.metodoA();

        // ERROR: en este caso metodoA podrìa llegar a ser el de Hijo
        /*
        procedure examples.inheritance.Padre#invocacion$examples.inheritance.Padre($this : Ref, param00 : Ref)
        {
            var r0 : Ref;
            var r1 : Ref;


            r1 := param00;

            examples.inheritance.Padre#invocacion$examples.inheritance.Padre_0:
                r0 := $this;

                call examples.inheritance.Padre#metodoA(r1);
                if ($Exception != null) {
                    return;
                }
                return;

        }
         */
    }


}
