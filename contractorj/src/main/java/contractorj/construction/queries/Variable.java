package contractorj.construction.queries;

import java.util.Objects;

public class Variable {

    public final String translatedType;

    public final String name;

    public Variable(final String translatedType, final String name) {

        this.translatedType = translatedType;
        this.name = name;
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }
        if (!(o instanceof Variable)) {
            return false;
        }
        final Variable variable = (Variable) o;
        return Objects.equals(translatedType, variable.translatedType) &&
                Objects.equals(name, variable.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(translatedType, name);
    }
}
