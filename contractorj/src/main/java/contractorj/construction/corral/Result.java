package contractorj.construction.corral;

public enum Result {
    NO_BUG,
    MAYBE_BUG,
    BUG_IN_QUERY,
    TRANSITION_MAY_THROW,
    TRANSITION_MAY_NOT_THROW,
    BROKEN_INVARIANT,
    PRES_OR_INV_MAY_THROW;

    public boolean isError() {

        return equals(BROKEN_INVARIANT) || equals(PRES_OR_INV_MAY_THROW);
    }
}
