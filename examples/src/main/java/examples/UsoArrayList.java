package examples;

import examples.arrayList.ArrayList;

public class UsoArrayList {

    public ArrayList arreglo;
    public int index;
    public int i;

    UsoArrayList(){
        arreglo = new ArrayList(4);
        i = 0;
    }

    public void add(int i){
        arreglo.add(index,i);
        index++;
    }

    public void conCinco(){
        i++;
    }

    public void sinCinco(){
        i--;
    }

    public boolean add_pre() {
        return index+1 < 4;
    }

    public boolean conCinco_pre() {
        boolean result = false;

        for (int i = 0; i < index; i++){
            if (((int)arreglo.elementData[i]) == 5)
                return true;
        }

        return result;
    }
    public boolean sinCinco_pre() {
        return !conCinco_pre();
    }
    public boolean inv(){return arreglo!= null && arreglo.size == 4 && index >= 0 && index < 4;}
}
