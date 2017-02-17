package annotator;

import static org.junit.Assert.*;

import org.junit.Test;

public class InvariantsExtractorTest {

  @Test
  public void testIdentifiersRegexp() throws Exception {
    assertTrue(InvariantsExtractor.idfentifiersPattern.matcher("a").matches());
    assertTrue(InvariantsExtractor.idfentifiersPattern.matcher("this.a").matches());
    assertTrue(InvariantsExtractor.idfentifiersPattern.matcher("this.a.Asds").matches());
    assertTrue(InvariantsExtractor.idfentifiersPattern.matcher("_").matches());
    assertTrue(InvariantsExtractor.idfentifiersPattern.matcher("_0").matches());
    assertTrue(InvariantsExtractor.idfentifiersPattern.matcher("_.asd0").matches());
    assertTrue(InvariantsExtractor.idfentifiersPattern.matcher("$").matches());
    assertTrue(InvariantsExtractor.idfentifiersPattern.matcher("this.$a").matches());
  }
}
