import java.util.LinkedList;
import java.util.List;

public class Example1 {

    public int member = 123;

    public boolean booleanMember = false;

    public Object objectMember = null;

    public Object[] membersArray = new Object[10];

    public int[] staticArray = new int[10];

    public void method() {

        int i = 123123;
        i += 1;
        i = i - 1;
        int j = 12;
        j = 123123;
        j /= 2;

        if (i - j == 0) {
            j++;
        }

        j = member;
        Integer.valueOf(j);
        Integer.valueOf(i);

        try {
            j = 10;
        } catch (IllegalArgumentException e) {
            j = 12316;
        } finally {
            i = 0;
        }

        metodoConUnParametro(conRet(0, null));
    }

    public void metodoConUnParametro(int i) {
        if (i != 123) {
            assert false;
        }

        Example1 c1 = new Example1();
    }

    public int conRet(int i, Example1 asd) {
        asd.objectMember = objectMember;
        return 123;
    }

    public void cosasConArrays() {
        int[] numbers; // declare numbers as an int array of any size
        numbers = new int[10];
        numbers[5] = 123;

        int i = numbers[5];

        Object[] objects = new Object[1];
        objects[0] = null;

        staticArray[0] = objects.length;


        membersArray[1] = objects[0];
        membersArray[2] = new Object();

    }


}
