package examples.SocketJP;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class MInetAddress {

	/*
	 * Returns the InetAddress representing anyLocalAddress (typically 0.0.0.0
	 * or ::0)
	 */
	public static InetAddress anyLocalAddress() {
		try {
			return InetAddress.getByName("0.0.0.0");
		} catch (UnknownHostException e) {
			// unreachable
			throw new Error("This code should be unreachable");
		}
	}

	/**
	 * Returns the loopback address.
	 * <p>
	 * The InetAddress returned will represent the IPv4 loopback address,
	 * 127.0.0.1, or the IPv6 loopback address, ::1. The IPv4 loopback address
	 * returned is only one of many in the form 127.*.*.*
	 *
	 * @return the InetAddress loopback instance.
	 * @since 1.7
	 */
	public static InetAddress getLoopbackAddress() {
		try {
			return InetAddress.getByName("127.0.0.1");
		} catch (UnknownHostException e) {
			// unreachable
			throw new Error("This code should be unreachable");
		}
	}

}
