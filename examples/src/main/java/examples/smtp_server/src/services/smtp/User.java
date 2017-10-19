package examples.smtp_server.src.services.smtp;

import java.io.File;
import java.util.Random;

/** Created by Usuario on 16/08/2017. */
public class User {
  public File getUserDirectory() {
    return new File("dsadsa");
  }

  public EmailAddress[] getDeliveryAddresses() {
    return new EmailAddress[(new Random().nextInt())];
  }
}
