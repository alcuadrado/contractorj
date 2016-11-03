package j2bpl.epas;

import j2bpl.translation.Method;

public class Contract {

    public final Method precondition;

    public final Method method;

    public Contract(Method precondition, Method method) {

        this.precondition = precondition;
        this.method = method;
    }

    @Override
    public String toString() {

        return method.getJavaName();
    }
}
