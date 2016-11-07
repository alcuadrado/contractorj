package contractorj.model;

import j2bpl.Method;

/**
 * An actions models a method with its precondition.
 */
public class Action {

    public final Method precondition;

    public final Method method;

    public Action(Method precondition, Method method) {

        this.precondition = precondition;
        this.method = method;
    }

    @Override
    public String toString() {

        return method.getJavaName();
    }
}
