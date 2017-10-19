package examples.smtp_server.src.services.smtp;

import java.util.Random;

/** Created by Usuario on 16/08/2017. */
public class DeliveryService {
  public static DeliveryService getDeliveryService() {
    return new DeliveryService();
  }

  public boolean acceptAddress(EmailAddress address, String clientIp, EmailAddress fromAddress) {
    return true;
  }

  public boolean isLocalAddress(EmailAddress address) {
    Random rand = new Random();
    return rand.nextBoolean();
  }
}
