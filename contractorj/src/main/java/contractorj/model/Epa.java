package contractorj.model;

import com.google.common.base.Joiner;
import j2bpl.Method;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Epa {

    private final List<Transition> transitions = new LinkedList<>();

    private final String title;

    public Epa(String title) {

        this.title = title;
    }

    public synchronized void addTransition(Transition transition) {

        transitions.add(transition);
    }

    public String toDot() {

        final HashSet<State> usedStates = new HashSet<>();

        final Map<State, Map<State, List<String>>> collapsedEdges = new HashMap<>();

        for (Transition transition : transitions) {

            final State from = transition.source;
            final State to = transition.target;

            usedStates.add(from);
            usedStates.add(to);

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

        stringBuilder.append("digraph EPA {\n");
        stringBuilder.append("\tlabel=\"" + title + "\";\n");
        stringBuilder.append("\tfontsize=22;\n");
        stringBuilder.append("\tlabelloc=top;\n");
        stringBuilder.append("\tlabeljust=center;\n");

        for (State usedState : usedStates) {
            stringBuilder.append("\t")
                    .append("n" + Math.abs(usedState.hashCode()))
                    .append("[label=\"")
                    .append(getStateDotName(usedState))
                    .append("\",style=filled,color=\"")
                    .append(getLightColor())
                    .append("\"];\n");
        }

        for (State from : collapsedEdges.keySet()) {

            final Map<State, List<String>> startingInFrom = collapsedEdges.get(from);

            for (State to : startingInFrom.keySet()) {

                final List<String> methodNames = startingInFrom.get(to);

                final String color = getDarkishColor();

                stringBuilder.append("\t")
                        .append("n" + Math.abs(from.hashCode()))
                        .append(" -> ")
                        .append("n" + Math.abs(to.hashCode()))
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

    private String getTransitionName(final Transition transition) {

        final Method transitionMethod = transition.transition.method;
        return transitionMethod.getJavaNameWithArgumentTypes()
                + (transition.isUncertain ? "?" : "") + (transition.isThrowing ? "\u26A1" : "");
    }

    private String getStateDotName(State state) {

        if (state.enabledActions.isEmpty()) {
            return "EMPTY";
        }

        final ArrayList<String> names = new ArrayList<>(state.enabledActions.size());

        for (Action action : state.enabledActions) {
            names.add(action.method.getJavaNameWithArgumentTypes());
        }

        Collections.sort(names);

        return Joiner.on("\\n").join(names);
    }

    private String getLightColor() {

        return getColor(180, 230);
    }

    private String getDarkishColor() {

        return getColor(64, 180);
    }

    private String getColor(int minValue, int maxValue) {

        final Random rand = new Random();

        int r = rand.nextInt(maxValue - minValue + 1) + minValue;
        int g = rand.nextInt(maxValue - minValue + 1) + minValue;
        int b = rand.nextInt(maxValue - minValue + 1) + minValue;

        return String.format("#%02x%02x%02x", r, g, b);
    }

}
