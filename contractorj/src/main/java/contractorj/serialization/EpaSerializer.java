package contractorj.serialization;

import contractorj.model.Epa;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public interface EpaSerializer {

    String serialize(Epa epa);

    default void serializeToFile(Epa epa, File output) throws IOException {
        Files.write(output.toPath(), serialize(epa).getBytes());
    }

}
