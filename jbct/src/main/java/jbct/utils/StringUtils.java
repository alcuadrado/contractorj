package jbct.utils;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import java.util.ArrayList;
import java.util.List;

public class StringUtils {

  /** Indents a list of strings (@see #indent) and joins them with '\n' in between. */
  public static String indentList(List<String> lines) {

    return indent(Joiner.on("\n").join(lines));
  }

  /** Indents a string, adding 4 spaces before every line. */
  public static String indent(String str) {

    final Iterable<String> lines = Splitter.on("\n").split(str);

    final ArrayList<String> indentedLines = new ArrayList<>();

    for (final String line : lines) {
      indentedLines.add("    " + line);
    }

    return Joiner.on("\n").join(indentedLines);
  }

  /**
   * Replace all illegal identifier characters with '?'. This method is idempotent.
   *
   * <p>
   *
   * <p>Note that this method can have collisions, which should be extremely rare in real java.
   */
  public static String scapeIllegalIdentifierCharacters(String name) {

    return name.replaceAll("[<>{},()\\[\\]]", "?").replace(" ", "");
  }
}
