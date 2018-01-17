package examples.SocketJP;

public class MSocketImplFactory {

	/**
	 * Creates a new {@code SocketImpl} instance.
	 *
	 * @return a new instance of {@code SocketImpl}.
	 * @see java.net.SocketImpl
	 */
	public MSocketImpl createSocketImpl() {
		return new MSocketImpl();
	}

}
