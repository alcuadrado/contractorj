package contractorj.serialization;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import contractorj.model.Epa;
import contractorj.model.State;
import contractorj.model.Transition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class DotEpaSerializer implements EpaSerializer {

    @Override
    public String serialize(final Epa epa) {

        final Map<State, Map<State, List<String>>> collapsedEdges = new HashMap<>();

        for (Transition transition : epa.getTransitions()) {

            final State from = transition.source;
            final State to = transition.target;

            if (!collapsedEdges.containsKey(from)) {
                collapsedEdges.put(from, new HashMap<>());
            }

            final Map<State, List<String>> startingInFrom = collapsedEdges.get(from);

            if (!startingInFrom.containsKey(to)) {
                startingInFrom.put(to, new LinkedList<>());
            }

            final String transitionName = getTransitionName(transition);
            startingInFrom.get(to).add(transitionName);
        }

        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("digraph EPA {\n")
                .append("\tlabel=\"").append(epa.getClassName()).append("\";\n")
                .append("\tfontsize=22;\n")
                .append("\tlabelloc=top;\n")
                .append("\tlabeljust=center;\n");

        for (State state : getSortedStates(epa)) {
            stringBuilder.append("\t")
                    .append(getStateDeclaration(epa, state))
                    .append("\n");
        }

        for (State from : collapsedEdges.keySet()) {

            final Map<State, List<String>> startingInFrom = collapsedEdges.get(from);

            for (State to : startingInFrom.keySet()) {

                final List<String> methodNames = Lists.newArrayList(
                        Sets.newHashSet(
                                startingInFrom.get(to).iterator()
                        ).iterator()
                );
                Collections.sort(methodNames);

                final String color = getDarkishColor();

                stringBuilder.append("\t")
                        .append(getStateNode(from))
                        .append(" -> ")
                        .append(getStateNode(to))
                        .append("[label=\"")
                        .append(Joiner.on("\\n").join(methodNames))
                        .append("\",color=\"")
                        .append(color)
                        .append("\",fontcolor=\"")
                        .append(color)
                        .append("\"];\n");
            }
        }

        stringBuilder.append("}");

        return stringBuilder.toString();
    }

    private String getStateDeclaration(final Epa epa, final State state) {

        return getStateNode(state)
                + "["
                + "label=\"" + getStateDotName(state) + "\","
                + "style=filled," +
                "color=\"" + (state.equals(State.ERROR) ? getErrorColor() : getLightColor()) + "\"" +
                (state.equals(epa.getInitialState()) ? ",peripheries=2" : "") +
                "]";

    }

    private List<State> getSortedStates(Epa epa) {

        final ArrayList<State> states = new ArrayList<>();

        final Queue<State> statesToVisit = new LinkedList<>();
        statesToVisit.add(epa.getInitialState());

        while (!statesToVisit.isEmpty()) {

            final State state = statesToVisit.remove();
            states.add(state);

            epa.getTransitionsWithSource(state).stream()
                    .map(transition -> transition.target)
                    .filter(targetState -> !statesToVisit.contains(targetState))
                    .filter(targetState -> !states.contains(targetState))
                    .forEach(statesToVisit::add);
        }

        return states;
    }

    private String getStateNode(final State state) {

        return "n" + Math.abs(state.hashCode());
    }

    private String getTransitionName(final Transition transition) {

        return (transition.isThrowing ? "\u26A1" : "")
                + transition.transition.method.getJavaNameWithArgumentTypes()
                + (transition.isUncertain ? "?" : "");
    }

    private String getStateDotName(State state) {

        if (state.equals(State.ERROR)) {
            return "ERROR";
        }

        final Set<String> names = state.enabledActions.stream()
                .map(action -> action.method.getJavaNameWithArgumentTypes())
                .collect(Collectors.toSet());

        return Joiner.on("\\n").join(names);
    }

    private String getLightColor() {

        return getColor(180, 230);
    }

    private String getDarkishColor() {

        return getColor(64, 180);
    }

    private String getErrorColor() {
        return "#db3d4d";
    }

    private String getColor(int minValue, int maxValue) {

        final Random rand = new Random();

        int r = rand.nextInt(maxValue - minValue + 1) + minValue;
        int g = rand.nextInt(maxValue - minValue + 1) + minValue;
        int b = rand.nextInt(maxValue - minValue + 1) + minValue;

        return String.format("#%02x%02x%02x", r, g, b);
    }
}
