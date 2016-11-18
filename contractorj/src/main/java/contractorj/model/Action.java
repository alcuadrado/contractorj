package contractorj.model;

import j2bpl.Method;

import java.util.Objects;

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

        return method.getJavaNameWithArgumentTypes();
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof Action)) {
            return false;
        }

        final Action action = (Action) o;
        return Objects.equals(precondition, action.precondition) &&
                Objects.equals(method, action.method);
    }

    @Override
    public int hashCode() {

        return Objects.hash(precondition, method);
    }

}
