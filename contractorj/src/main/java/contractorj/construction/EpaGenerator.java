package contractorj.construction;

import contractorj.construction.corral.CorralRunner;
import contractorj.construction.corral.RunnerResult;
import contractorj.construction.queries.Answer;
import contractorj.construction.queries.Query;
import contractorj.model.Action;
import contractorj.model.Epa;
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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class EpaGenerator {

  private final Set<java.lang.Class<? extends Query>> queryClasses = new HashSet<>();

  private final Map<java.lang.Class<? extends Query>, Map<Answer, List<Duration>>> queryingTimes =
      new HashMap<>();

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

  public Epa generateEpa(Class theClass, Set<String> methodNames) {

    queryingTimes.clear();

    final LocalDateTime start = LocalDateTime.now();

    final ActionsExtractor actionsExtractor = new ActionsExtractor(theClass, methodNames);

    actions = actionsExtractor.getInstanceActions();
    invariant = actionsExtractor.getInvariant();
    constructors = actionsExtractor.getConstructorActions();

    final Epa epa = generateEpaImplementation(theClass);

    totalTime = Duration.between(start, LocalDateTime.now());

    return epa;
  }

  protected abstract Epa generateEpaImplementation(final Class theClass);

  protected RunnerResult runQuery(final Query query) {

    final String absolutePathToBoogieSourceFile =
        appendToThreadLocalBoogieFile(query.getBoogieCode());

    final RunnerResult runnerResult =
        corralRunner.run(absolutePathToBoogieSourceFile, query.getName());

    recordQueryRun(query, runnerResult);

    return runnerResult;
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

    try (final FileWriter fw = new FileWriter(file, true);
        final BufferedWriter bw = new BufferedWriter(fw);
        final PrintWriter out = new PrintWriter(bw)) {
      out.print(content + "\n\n");
      out.flush();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public long getTotalNumberOfQueries() {

    return queryingTimes
        .values()
        .stream()
        .flatMap(answerDurationMap -> answerDurationMap.values().stream())
        .mapToInt(List::size)
        .sum();
  }

  public long getNumberOfQueriesByClass(java.lang.Class<? extends Query> queryClass) {

    return queryingTimes
        .getOrDefault(queryClass, new HashMap<>())
        .values()
        .stream()
        .mapToInt(List::size)
        .sum();
  }

  public long getNumberOfQueriesByClassAndAnswer(
      java.lang.Class<? extends Query> queryClass, Answer answer) {

    return queryingTimes
        .getOrDefault(queryClass, new HashMap<>())
        .getOrDefault(answer, new ArrayList<>())
        .size();
  }

  public Duration getTotalTime() {

    return totalTime;
  }

  public Duration getTotalQueryingTime() {

    return queryingTimes
        .values()
        .stream()
        .flatMap(answerDurationMap -> answerDurationMap.values().stream())
        .flatMap(Collection::stream)
        .reduce(Duration::plus)
        .orElse(Duration.ZERO);
  }

  public Duration getQueryingTimeByClass(java.lang.Class<? extends Query> queryClass) {

    return queryingTimes
        .getOrDefault(queryClass, new HashMap<>())
        .values()
        .stream()
        .flatMap(Collection::stream)
        .reduce(Duration::plus)
        .orElse(Duration.ZERO);
  }

  public Duration getAverageQueryingTimeByClass(java.lang.Class<? extends Query> queryClass) {

    return getQueryingTimeByClass(queryClass).dividedBy(getNumberOfQueriesByClass(queryClass));
  }

  public Duration getQueryingTimeByClassAndAnswer(
      java.lang.Class<? extends Query> queryClass, Answer answer) {

    return queryingTimes
        .getOrDefault(queryClass, new HashMap<>())
        .getOrDefault(answer, new ArrayList<>())
        .stream()
        .reduce(Duration::plus)
        .orElse(Duration.ZERO);
  }

  public Set<java.lang.Class<? extends Query>> getQueryClasses() {

    return queryClasses;
  }

  private synchronized void recordQueryRun(Query query, RunnerResult runnerResult) {

    final Answer answer = query.getAnswer(runnerResult.queryResult);
    final java.lang.Class<? extends Query> queryClass = query.getClass();

    if (!queryingTimes.containsKey(queryClass)) {
      queryClasses.add(queryClass);
      queryingTimes.put(queryClass, new HashMap<>());
    }

    final Map<Answer, List<Duration>> answerDurationsMap = queryingTimes.get(queryClass);

    if (!answerDurationsMap.containsKey(answer)) {
      answerDurationsMap.put(answer, new LinkedList<>());
    }

    final List<Duration> durations = answerDurationsMap.get(answer);

    durations.add(runnerResult.runningTime);
  }
}
