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

    private final String className;

    public Epa(String className) {

        this.className = className;
    }

    public synchronized void addTransition(Transition transition) {

        transitions.add(transition);
    }

    public List<Transition> getTransitions() {

        return transitions;
    }

    public String getClassName() {

        return className;
    }
}
