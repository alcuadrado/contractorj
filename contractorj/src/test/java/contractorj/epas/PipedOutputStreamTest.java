package contractorj.epas;

//import org.bitstrings.test.junit.runner.ClassLoaderPerTestRunner;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

//@RunWith( ClassLoaderPerTestRunner.class )
import com.binarytweed.test.Quarantine;
import com.binarytweed.test.QuarantiningRunner;

@RunWith(QuarantiningRunner.class)
@Quarantine({"soot.", "jbct.", "contractorj."})
public class PipedOutputStreamTest extends EpaTest {

    @Test
    public void testPipedOutputStream() throws Exception {
        testEpa("redundant_request.PipedOutputStream");
    }

    @After
    public void deleteFiles(){
        deleteFolder(out.toFile());
        deleteFolder(sootOutput);

        final File log = new File(System.getProperty("user.dir") + "/log");
        log.delete();
    }
}
