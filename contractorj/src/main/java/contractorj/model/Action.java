package contractorj.model;

import j2bpl.Method;

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
