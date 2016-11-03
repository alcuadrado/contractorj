package j2bpl.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CombinationsGenerator<E> {

    public Set<Set<E>> combinations(Set<E> elements) {

        final ImmutableList<E> asList = ImmutableList.copyOf(elements);

        final Set<Set<E>> combinations = new HashSet<>();

        combinations.add(new HashSet<E>());

        for (int i = 0; i < elements.size(); i++) {

            final List<E> withoutI = listWithoutI(asList, i);

            final HashSet<E> setWithoutI = new HashSet<>(withoutI);

            for (Set<E> combination : combinations(setWithoutI)) {
                combination.add(asList.get(i));
                combinations.add(combination);
            }

        }

        return combinations;
    }

    private List<E> listWithoutI(List<E> asList, int i) {

        final ArrayList<E> copy = Lists.newArrayList(asList);
        copy.remove(i);
        return copy;
    }

}
