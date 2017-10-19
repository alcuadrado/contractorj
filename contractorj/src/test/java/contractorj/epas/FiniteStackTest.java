package contractorj.epas;

import com.binarytweed.test.Quarantine;
import com.binarytweed.test.QuarantiningRunner;
import java.io.File;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(QuarantiningRunner.class)
@Quarantine({"soot.", "jbct.", "contractorj."})
public class FiniteStackTest extends EpaTest {
  @Test
  public void testFiniteStackEpa() throws Exception {
    testEpa("FiniteStack");
  }

  @After
  public void deleteFiles() {
    deleteFolder(out.toFile());
    deleteFolder(sootOutput);

    final File log = new File(System.getProperty("user.dir") + "/log");
    log.delete();
  }
}
