package contractorj.construction.corral;

public enum Result {
    NO,
    YES,
    MAYBE,
    APPLICATION_BUG,
    UNHANDLED_EXCEPTION;

    public boolean isError() {

        return equals(APPLICATION_BUG) || equals(UNHANDLED_EXCEPTION);
    }
}
