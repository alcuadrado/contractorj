package examples.translation;

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
