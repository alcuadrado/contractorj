package j2bpl.epas;

public class Edge {

    public final State from;

    public final State to;

    public final Contract transition;

    public final boolean isUncertain;

    public Edge(State from, State to, Contract transition, boolean isUncertain) {

        assert from.contracts.contains(transition);

        this.from = from;
        this.to = to;
        this.transition = transition;
        this.isUncertain = isUncertain;
    }

}
