package examples.translation;

/**
 * Created by Usuario on 11/09/2017.
 */
public class Switches {

    public void test1(int a){
        switch(a){
            case 1:
                foo2();
                break;
            case 2:
                foo3();
                break;
        }
    }


    public void foo2(){

    }

    public void foo3(){

    }

    public void test2(int a){
        switch(a){
            case 5:
                foo2();
            case 6:
                foo3();
                break;
        }
    }

    public void test3(int a){
        switch(a){
            case 5:
                foo2();
            case 6:
                foo3();
            default:
                test1(a);
        }
    }

    public void test4(int a){
        switch(a){
            case 5:
                foo2();
            case 6:
                foo3();
            default:
                test1(a);
                break;
        }
    }

    public void test5(int a){
        switch(a){
            case 5:
                foo2();
            case 6:
                foo3();
            default:
                test1(a);
                break;
        }

        test3(a);
    }

    public void test6(int a){
        switch(a){
            case 5:
            {
                int x = 0;
                foo2();
                x = a;
                if (a == 4){
                    return;
                }

                try{
                    //throw new Exception();
                }catch(Exception ex){
                    x = 10;
                } finally
                {
                    test1(x);
                }
            }
            case 6:
                foo3();
            default:
                test1(a);
                break;
        }

        test3(a);
    }
}
