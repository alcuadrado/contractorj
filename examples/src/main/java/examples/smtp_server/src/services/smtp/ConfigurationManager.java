package examples.smtp_server.src.services.smtp;

import java.util.Random;

/** Created by Usuario on 16/08/2017. */
public class ConfigurationManager {
  public User getUser(EmailAddress address) {
    Random rand = new Random();
    if (rand.nextBoolean()) return new User();
    return null;
  }

  public int getMaximumMessageSize() {
    Random rand = new Random();
    return rand.nextInt();
  }

  public String getMailDirectory() {
    return "unDir";
  }

  public int getDeliveryAttemptThreshold() {
    Random rand = new Random();
    return rand.nextInt();
  }

  public boolean isDefaultUserEnabled() {
    Random rand = new Random();
    return rand.nextBoolean();
  }

  public EmailAddress getDefaultUser() {
    return new EmailAddress();
  }
}
