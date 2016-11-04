package contractorj.util;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class CombinationsGeneratorTest {

    @Test
    public void testWithEmptySet() throws Exception {

        final Set<Set<Integer>> expected = new HashSet<>();
        expected.add(new HashSet<Integer>());

        final CombinationsGenerator<Integer> generator = new CombinationsGenerator<>();
        final Set<Set<Integer>> combinations = generator.combinations(new HashSet<Integer>());

        assertEquals(expected, combinations);
    }

    @Test
    public void testWithOneElement() throws Exception {

        final Set<Set<Integer>> expected = new HashSet<>();
        expected.add(new HashSet<Integer>());
        expected.add(ImmutableSet.of(1));

        final CombinationsGenerator<Integer> generator = new CombinationsGenerator<>();
        final Set<Set<Integer>> combinations = generator.combinations(ImmutableSet.of(1));

        assertEquals(expected, combinations);
    }

    @Test
    public void testWithTwoElements() throws Exception {

        final Set<Set<Integer>> expected = new HashSet<>();
        expected.add(new HashSet<Integer>());
        expected.add(ImmutableSet.of(1));
        expected.add(ImmutableSet.of(2));
        expected.add(ImmutableSet.of(1, 2));

        final CombinationsGenerator<Integer> generator = new CombinationsGenerator<>();
        final Set<Set<Integer>> combinations = generator.combinations(ImmutableSet.of(1, 2));

        assertEquals(expected, combinations);
    }

    @Test
    public void testWithThreeElements() throws Exception {

        final Set<Set<Integer>> expected = new HashSet<>();
        expected.add(new HashSet<Integer>());
        expected.add(ImmutableSet.of(1));
        expected.add(ImmutableSet.of(2));
        expected.add(ImmutableSet.of(3));
        expected.add(ImmutableSet.of(1, 2));
        expected.add(ImmutableSet.of(1, 3));
        expected.add(ImmutableSet.of(2, 3));
        expected.add(ImmutableSet.of(1, 2, 3));

        final CombinationsGenerator<Integer> generator = new CombinationsGenerator<>();
        final Set<Set<Integer>> combinations = generator.combinations(ImmutableSet.of(1, 2, 3));

        assertEquals(expected, combinations);
    }

}