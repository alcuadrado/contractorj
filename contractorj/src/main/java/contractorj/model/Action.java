package contractorj.model;

import j2bpl.Method;

import java.util.Objects;

/**
 * An actions models a method with its precondition.
 */
public class Action {

    private final Method precondition;

    private final Method method;

    public Action(Method precondition, Method method) {

        this.precondition = precondition;
        this.method = method;
    }

    @Override
    public String toString() {

        return getMethod().getJavaNameWithArgumentTypes();
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
        return Objects.equals(getPrecondition(), action.getPrecondition()) &&
                Objects.equals(getMethod(), action.getMethod());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getPrecondition(), getMethod());
    }

    public Method getPrecondition() {

        return precondition;
    }

    public Method getMethod() {

        return method;
    }
}
