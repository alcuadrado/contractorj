package examples;

/**
 * Created by Usuario on 06/07/2017.
 */
public class CharTest {
    public void charComoParametro(char a){

    }

    public void charTestDeAsignacion(){
        char x = 'a';
        char y = 'b';

        char z = (char) (x + y);
    }

    public void charTestDeAsignacion1(char b){
        char x = 'a';
        char y = 'b';

        char z = (char) (x + y + b);
    }

    public void charTestDeAsignacion2(int b){
        char x = 'a';
        char y = 'b';

        char z = (char) (x + y + b);
    }
}
