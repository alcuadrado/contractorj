package contractorj.construction;

import contractorj.construction.corral.CorralRunner;
import contractorj.construction.corral.Result;
import contractorj.model.Action;
import contractorj.model.Epa;
import contractorj.util.ColorPrinter;
import j2bpl.Class;
import j2bpl.Method;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class EpaGenerator {

    private final Map<Query.Type, Map<Result, List<Duration>>> queryingTimes = new HashMap<>();

    private final String baseTranslation;

    protected final int numberOfThreads;

    private final CorralRunner corralRunner;

    private Duration totalTime;

    private ThreadLocal<File> boogieFile = new ThreadLocal<>();

    protected Set<Action> constructors;

    protected Method invariant;

    protected Set<Action> actions;

    public EpaGenerator(String baseTranslation, int numberOfThreads, CorralRunner corralRunner) {

        this.baseTranslation = baseTranslation;
        this.numberOfThreads = numberOfThreads;
        this.corralRunner = corralRunner;
    }

    public Epa generateEpa(Class theClass) {

        queryingTimes.clear();

        final LocalDateTime start = LocalDateTime.now();

        final ActionsExtractor actionsExtractor = new ActionsExtractor(theClass);
        actions = actionsExtractor.getInstanceActions();
        invariant = actionsExtractor.getInvariant();
        constructors = actionsExtractor.getConstructorActions();

        final Epa epa = generateEpaImplementation(theClass);

        totalTime = Duration.between(start, LocalDateTime.now());

        return epa;
    }

    protected abstract Epa generateEpaImplementation(final Class theClass);

    protected Result runQuery(final Query query) {

        final String absolutePathToBoogieSourceFile = appendToThreadLocalBoogieFile(query.getBoogieCode());

        final CorralRunner.RunnerResult runnerResult = corralRunner.run(absolutePathToBoogieSourceFile,
                query.getName());

        recordQueryRun(query, runnerResult);

        printCorralCommand(query, absolutePathToBoogieSourceFile, runnerResult);

        return runnerResult.queryResult;
    }

    private void printCorralCommand(Query query, final String absolutePathToBoogieSourceFile,
                                    CorralRunner.RunnerResult runnerResult) {

        final Result result = runnerResult.queryResult;

        String toPrint = corralRunner.getConsoleCommandToRun(absolutePathToBoogieSourceFile, query.getName());

        if (result.isError()) {
            toPrint += result + "\n\n" + result.toString() + "\n\n" + runnerResult.output;
            System.exit(1);
        }

        ColorPrinter.printInColor("\n" + toPrint, getResultColor(result));
    }

    private ColorPrinter.Color getResultColor(Result result) {

        switch (result) {

            case BUG_IN_QUERY:
                return ColorPrinter.Color.GREEN;

            case MAYBE_BUG:
                return ColorPrinter.Color.BLUE;

            case BROKEN_INVARIANT:
                return ColorPrinter.Color.PURPLE;

            case TRANSITION_MAY_NOT_THROW:
            case TRANSITION_MAY_THROW:
                return ColorPrinter.Color.CYAN;

            case PRES_OR_INV_MAY_THROW:
                return ColorPrinter.Color.RED;
        }

        return ColorPrinter.Color.WHITE;
    }

    private String appendToThreadLocalBoogieFile(String boogieCode) {

        File file = boogieFile.get();

        if (file == null) {

            try {
                file = File.createTempFile("epa-" + Thread.currentThread().getName() + "-", ".bpl");
                appendToFile(file, baseTranslation);
                boogieFile.set(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        appendToFile(file, boogieCode);

        return file.getAbsolutePath();
    }

    private void appendToFile(final File file, final String content) {

        try (
                final FileWriter fw = new FileWriter(file, true);
                final BufferedWriter bw = new BufferedWriter(fw);
                final PrintWriter out = new PrintWriter(bw)
        ) {
            out.print(content + "\n\n");
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public long getTotalNumerOfQueries() {

        return queryingTimes.values().stream()
                .flatMap(resultDurationMap -> resultDurationMap.values().stream())
                .mapToInt(List::size)
                .sum();
    }

    public long getNumberOfQueriesByType(Query.Type type) {

        return queryingTimes.getOrDefault(type, new HashMap<>()).values().stream()
                .mapToInt(List::size)
                .sum();
    }

    public long getNumberOfQueriesByTypeAndResult(Query.Type type, Result result) {

        return queryingTimes.getOrDefault(type, new HashMap<>())
                .getOrDefault(result, new ArrayList<>())
                .size();
    }

    public Duration getTotalTime() {

        return totalTime;
    }

    public Duration getTotalQueryingTime() {

        return queryingTimes.values().stream()
                .flatMap(resultDurationMap -> resultDurationMap.values().stream())
                .flatMap(Collection::stream)
                .reduce(Duration::plus)
                .orElse(Duration.ZERO);
    }

    public Duration getQueryingTimeByType(Query.Type type) {

        return queryingTimes.getOrDefault(type, new HashMap<>()).values().stream()
                .flatMap(Collection::stream)
                .reduce(Duration::plus)
                .orElse(Duration.ZERO);
    }

    public Duration getAverageQueryingTimeByType(Query.Type type) {

        return getQueryingTimeByType(type).dividedBy(getNumberOfQueriesByType(type));
    }

    public Duration getQueryingTimeByTypeAndResult(Query.Type type, Result result) {

        return queryingTimes.getOrDefault(type, new HashMap<>())
                .getOrDefault(result, new ArrayList<>())
                .stream()
                .reduce(Duration::plus)
                .orElse(Duration.ZERO);
    }

    private synchronized void recordQueryRun(Query query, CorralRunner.RunnerResult runnerResult) {

        if (!queryingTimes.containsKey(query.type)) {
            queryingTimes.put(query.type, new HashMap<>());
        }

        final Map<Result, List<Duration>> resultDurationMap = queryingTimes.get(query.type);

        if (!resultDurationMap.containsKey(runnerResult.queryResult)) {
            resultDurationMap.put(runnerResult.queryResult, new LinkedList<>());
        }

        final List<Duration> durations = resultDurationMap.get(runnerResult.queryResult);

        durations.add(runnerResult.runningTime);
    }

}
