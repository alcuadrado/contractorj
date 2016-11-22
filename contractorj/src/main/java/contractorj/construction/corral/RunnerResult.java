package contractorj.construction.corral;

import java.time.Duration;

public class RunnerResult {

    public final QueryResult queryResult;

    public final Duration runningTime;

    public final String output;

    RunnerResult(final QueryResult queryResult, final Duration runningTime, final String output) {

        this.queryResult = queryResult;
        this.runningTime = runningTime;
        this.output = output;
    }
}
