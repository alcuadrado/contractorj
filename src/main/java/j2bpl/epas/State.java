package j2bpl.epas;

import com.google.common.base.Joiner;

import java.util.Set;

public class State {

    public final Set<Contract> contracts;

    public State(Set<Contract> contracts) {

        this.contracts = contracts;
    }

    @Override
    public String toString() {

        return "State{" + Joiner.on(", ").join(contracts) + '}';
    }
}
