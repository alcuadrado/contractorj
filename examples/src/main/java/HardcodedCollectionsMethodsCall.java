import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class HardcodedCollectionsMethodsCall {

    public void method(List list,
                       LinkedList linkedList,
                       ArrayList arrayList,
                       Collection collection) {

        collection.clear();
        collection.add(null);
        collection.remove(1);
        collection.remove(null);

        list.clear();
        list.add(null);
        list.remove(0);
        list.remove(null);

        linkedList.clear();
        linkedList.add(null);
        linkedList.remove(0);
        linkedList.remove(null);

        arrayList.clear();
        arrayList.add(null);
        arrayList.remove(0);
        arrayList.remove(null);

        final LinkedList<Object> emptyLinkedList = new LinkedList<>();
        final ArrayList<Object> emptyArrayList = new ArrayList<>();
        final ArrayList<Object> initializedArrayList = new ArrayList<>(4);
    }

}
