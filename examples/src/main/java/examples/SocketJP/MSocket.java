package examples.SocketJP;

import java.io.IOException;

/*
 * Copyright (c) 1995, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.Stack;

//import org.evosuite.epa.EpaAction;
//import org.evosuite.epa.EpaState;

/**
 * This class implements client sockets (also called just "sockets"). A socket
 * is an endpoint for communication between two machines.
 * <p>
 * The actual work of the socket is performed by an instance of the
 * {@code SocketImpl} class. An application, by changing the socket factory that
 * creates the socket implementation, can configure itself to create sockets
 * appropriate to the local firewall.
 *
 * @author unascribed
 * @see java.net.Socket#setSocketImplFactory(java.net.SocketImplFactory)
 * @see java.net.SocketImpl
 * @see java.nio.channels.SocketChannel
 * @since JDK1.0
 */
public class MSocket implements java.io.Closeable {
	/**
	 * Various states of this socket.
	 */
	private boolean created = false;
	private boolean bound = false;
	private boolean connected = false;
	private boolean closed = false;
	private Object closeLock = new Object();
	private boolean shutIn = false;
	private boolean shutOut = false;

	/**
	 * The implementation of this Socket.
	 */
	private MSocketImpl impl;

	/**
	 * Are we using an older SocketImpl?
	 */
	private boolean oldImpl = false;

	/**
	 * Creates an unconnected socket, with the system-default type of
	 * SocketImpl.
	 *
	 * @since JDK1.1
	 * @revised 1.4
	 */
	//@EpaAction(name = "Socket")
	public MSocket() {
		setImpl();
	}

	/**
	 * Creates the socket implementation.
	 *
	 * @param stream
	 *            a {@code boolean} value : {@code true} for a TCP socket,
	 *            {@code false} for UDP.
	 * @throws IOException
	 *             if creation fails
	 * @since 1.4
	 */
	private void createImpl(boolean stream) throws SocketException {
		if (impl == null)
			setImpl();
		try {
			impl.create(stream);
			created = true;
		} catch (IOException e) {
			throw new SocketException(e.getMessage());
		}
	}

	private void checkOldImpl() {
		if (impl == null)
			return;
		// SocketImpl.connect() is a protected method, therefore we need to use
		// getDeclaredMethod, therefore we need permission to access the member

		oldImpl = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
			public Boolean run() {
				Class<?> clazz = impl.getClass();
				while (true) {
					try {
						clazz.getDeclaredMethod("connect", SocketAddress.class, int.class);
						return Boolean.FALSE;
					} catch (NoSuchMethodException e) {
						clazz = clazz.getSuperclass();
						// java.net.SocketImpl class will always have this
						// abstract method.
						// If we have not found it by now in the hierarchy then
						// it does not
						// exist, we are an old style impl.
						if (clazz.equals(java.net.SocketImpl.class)) {
							return Boolean.TRUE;
						}
					}
				}
			}
		});
	}

	/**
	 * Sets impl to the system-default type of SocketImpl.
	 * 
	 * @since 1.4
	 */
	private void setImpl() {
		if (factory != null) {
			impl = factory.createSocketImpl();
			checkOldImpl();
		} else {
			// No need to do a checkOldImpl() here, we know it's an up to date
			// SocketImpl!
			impl = new MSocketImpl();
		}
		if (impl != null)
			impl.setSocket(this);
	}

	/**
	 * Get the {@code SocketImpl} attached to this socket, creating it if
	 * necessary.
	 *
	 * @return the {@code SocketImpl} attached to that ServerSocket.
	 * @throws SocketException
	 *             if creation fails
	 * @since 1.4
	 */
	private MSocketImpl getImpl() throws SocketException {
		if (!created)
			createImpl(true);
		return impl;
	}

	/**
	 * Connects this socket to the server.
	 *
	 * @param endpoint
	 *            the {@code SocketAddress}
	 * @throws IOException
	 *             if an error occurs during the connection
	 * @throws java.nio.channels.IllegalBlockingModeException
	 *             if this socket has an associated channel, and the channel is
	 *             in non-blocking mode
	 * @throws IllegalArgumentException
	 *             if endpoint is null or is a SocketAddress subclass not
	 *             supported by this socket
	 * @since 1.4
	 * @spec JSR-51
	 */
	//@EpaAction(name = "connect", enabledExceptionList = "java.io.Exception", notEnabledExceptionList = "java.net.SocketException,java.lang.IllegalArgumentException")
	public void connect(SocketAddress endpoint) throws IOException {
		try {
			internal_connect(endpoint, 0);
		} catch (Exception ex) {
			throw ex;
		}
	}

	/**
	 * Connects this socket to the server with a specified timeout value. A
	 * timeout of zero is interpreted as an infinite timeout. The connection
	 * will then block until established or an error occurs.
	 *
	 * @param endpoint
	 *            the {@code SocketAddress}
	 * @param timeout
	 *            the timeout value to be used in milliseconds.
	 * @throws IOException
	 *             if an error occurs during the connection
	 * @throws SocketTimeoutException
	 *             if timeout expires before connecting
	 * @throws java.nio.channels.IllegalBlockingModeException
	 *             if this socket has an associated channel, and the channel is
	 *             in non-blocking mode
	 * @throws IllegalArgumentException
	 *             if endpoint is null or is a SocketAddress subclass not
	 *             supported by this socket
	 * @since 1.4
	 * @spec JSR-51
	 */
	//@EpaAction(name = "connect", enabledExceptionList = "java.io.IOException", notEnabledExceptionList = "java.net.SocketException,java.lang.IllegalArgumentException")
	public void connect(SocketAddress endpoint, int timeout) throws IOException {
		try {
			internal_connect(endpoint, timeout);
		} catch (Exception ex) {
			throw ex;
		}
	}

	/**
	 * Connects this socket to the server with a specified timeout value. A
	 * timeout of zero is interpreted as an infinite timeout. The connection
	 * will then block until established or an error occurs.
	 *
	 * @param endpoint
	 *            the {@code SocketAddress}
	 * @param timeout
	 *            the timeout value to be used in milliseconds.
	 * @throws IOException
	 *             if an error occurs during the connection
	 * @throws SocketTimeoutException
	 *             if timeout expires before connecting
	 * @throws java.nio.channels.IllegalBlockingModeException
	 *             if this socket has an associated channel, and the channel is
	 *             in non-blocking mode
	 * @throws IllegalArgumentException
	 *             if endpoint is null or is a SocketAddress subclass not
	 *             supported by this socket
	 * @since 1.4
	 * @spec JSR-51
	 */
	private void internal_connect(SocketAddress endpoint, int timeout) throws IOException, IllegalArgumentException {
		try {
			if (endpoint == null)
				throw new IllegalArgumentException("connect: The address can't be null");

			if (timeout < 0)
				throw new IllegalArgumentException("connect: timeout can't be negative");

			if (isClosed())
				throw new SocketException("Socket is closed");

			if (!oldImpl && isConnected())
				throw new SocketException("already connected");

			if (!(endpoint instanceof InetSocketAddress))
				throw new IllegalArgumentException("Unsupported address type");

			InetSocketAddress epoint = (InetSocketAddress) endpoint;
			InetAddress addr = epoint.getAddress();
			int port = epoint.getPort();
			checkAddress(addr, "connect");

			if (!created)
				createImpl(true);
			if (!oldImpl)
				impl.connect(epoint, timeout);
			else if (timeout == 0) {
				if (epoint.isUnresolved())
					impl.connect(addr.getHostName(), port);
				else
					impl.connect(addr, port);
			} else
				throw new UnsupportedOperationException("SocketImpl.connect(addr, timeout)");
			connected = true;
			/*
			 * If the socket was not bound before the connect, it is now because
			 * the kernel will have picked an ephemeral port & a local address
			 */
			bound = true;
		} finally {
			if (!connected) {
				this.internal_close();
			}
		}
	}

	/**
	 * Binds the socket to a local address.
	 * <P>
	 * If the address is {@code null}, then the system will pick up an ephemeral
	 * port and a valid local address to bind the socket.
	 *
	 * @param bindpoint
	 *            the {@code SocketAddress} to bind to
	 * @throws IOException
	 *             if the bind operation fails, or if the socket is already
	 *             bound.
	 * @throws IllegalArgumentException
	 *             if bindpoint is a SocketAddress subclass not supported by
	 *             this socket
	 * @throws SecurityException
	 *             if a security manager exists and its {@code checkListen}
	 *             method doesn't allow the bind to the local port.
	 *
	 * @since 1.4
	 * @see #isBound
	 */
	//@EpaAction(name = "bind", enabledExceptionList = "java.io.Exception", notEnabledExceptionList = "java.net.SocketException,java.lang.IllegalArgumentException")
	public void bind(SocketAddress bindpoint) throws IOException {
		try {
			internal_bind(bindpoint);
		} catch (Exception ex) {
			throw ex;
		}
	}

	private void internal_bind(SocketAddress bindpoint) throws SocketException, IOException {
		if (isClosed())
			throw new SocketException("Socket is closed");
		if (!oldImpl && isBound())
			throw new SocketException("Already bound");

		if (bindpoint != null && (!(bindpoint instanceof InetSocketAddress)))
			throw new IllegalArgumentException("Unsupported address type");
		InetSocketAddress epoint = (InetSocketAddress) bindpoint;
		if (epoint != null && epoint.isUnresolved())
			throw new SocketException("Unresolved address");
		if (epoint == null) {
			epoint = new InetSocketAddress(0);
		}
		InetAddress addr = epoint.getAddress();
		int port = epoint.getPort();
		checkAddress(addr, "bind");
		// SecurityManager security = System.getSecurityManager();
		// if (security != null) {
		// security.checkListen(port);
		// }
		getImpl().bind(addr, port);
		bound = true;
	}

	private void checkAddress(InetAddress addr, String op) {
		if (addr == null) {
			return;
		}
		if (!(addr instanceof Inet4Address || addr instanceof Inet6Address)) {
			throw new IllegalArgumentException(op + ": invalid address type");
		}
	}

	/**
	 * Returns an input stream for this socket.
	 *
	 * <p>
	 * If this socket has an associated channel then the resulting input stream
	 * delegates all of its operations to the channel. If the channel is in
	 * non-blocking mode then the input stream's {@code read} operations will
	 * throw an {@link java.nio.channels.IllegalBlockingModeException}.
	 *
	 * <p>
	 * Under abnormal conditions the underlying connection may be broken by the
	 * remote host or the network software (for example a connection reset in
	 * the case of TCP connections). When a broken connection is detected by the
	 * network software the following applies to the returned input stream :-
	 *
	 * <ul>
	 *
	 * <li>
	 * <p>
	 * The network software may discard bytes that are buffered by the socket.
	 * Bytes that aren't discarded by the network software can be read using
	 * {@link java.io.InputStream#read read}.
	 *
	 * <li>
	 * <p>
	 * If there are no bytes buffered on the socket, or all buffered bytes have
	 * been consumed by {@link java.io.InputStream#read read}, then all
	 * subsequent calls to {@link java.io.InputStream#read read} will throw an
	 * {@link java.io.IOException IOException}.
	 *
	 * <li>
	 * <p>
	 * If there are no bytes buffered on the socket, and the socket has not been
	 * closed using {@link #close close}, then
	 * {@link java.io.InputStream#available available} will return {@code 0}.
	 *
	 * </ul>
	 *
	 * <p>
	 * Closing the returned {@link java.io.InputStream InputStream} will close
	 * the associated socket.
	 *
	 * @return an input stream for reading bytes from this socket.
	 * @exception IOException
	 *                if an I/O error occurs when creating the input stream, the
	 *                socket is closed, the socket is not connected, or the
	 *                socket input has been shutdown using
	 *                {@link #shutdownInput()}
	 *
	 * @revised 1.4
	 * @spec JSR-51
	 */
	//@EpaAction(name = "getInputStream", enabledExceptionList = "java.io.Exception", notEnabledExceptionList = "java.net.SocketException")
	public InputStream getInputStream() throws IOException {
		try {
			return internal_getInputStream();
		} catch (Exception ex) {
			throw ex;
		}
	}

	private InputStream internal_getInputStream() throws SocketException, IOException {
		if (isClosed())
			throw new SocketException("Socket is closed");
		if (!isConnected())
			throw new SocketException("Socket is not connected");
		if (isInputShutdown())
			throw new SocketException("Socket input is shutdown");
		// final MSocket s = this;
		InputStream is = null;
		try {
			is = AccessController.doPrivileged(new PrivilegedExceptionAction<InputStream>() {
				public InputStream run() throws IOException {
					return impl.getInputStream();
				}
			});
		} catch (java.security.PrivilegedActionException e) {
			throw (IOException) e.getException();
		}
		return is;
	}

	/**
	 * Returns an output stream for this socket.
	 *
	 * <p>
	 * If this socket has an associated channel then the resulting output stream
	 * delegates all of its operations to the channel. If the channel is in
	 * non-blocking mode then the output stream's {@code write} operations will
	 * throw an {@link java.nio.channels.IllegalBlockingModeException}.
	 *
	 * <p>
	 * Closing the returned {@link java.io.OutputStream OutputStream} will close
	 * the associated socket.
	 *
	 * @return an output stream for writing bytes to this socket.
	 * @exception IOException
	 *                if an I/O error occurs when creating the output stream or
	 *                if the socket is not connected.
	 * @revised 1.4
	 * @spec JSR-51
	 */
	//@EpaAction(name = "getOutputStream", enabledExceptionList = "java.io.Exception", notEnabledExceptionList = "java.net.SocketException")
	public OutputStream getOutputStream() throws IOException {
		try {
			return internal_getOutputStream();
		} catch (Exception ex) {
			throw ex;
		}
	}

	private OutputStream internal_getOutputStream() throws SocketException, IOException {
		if (isClosed())
			throw new SocketException("Socket is closed");
		if (!isConnected())
			throw new SocketException("Socket is not connected");
		if (isOutputShutdown())
			throw new SocketException("Socket output is shutdown");
		// final MSocket s = this;
		OutputStream os = null;
		try {
			os = AccessController.doPrivileged(new PrivilegedExceptionAction<OutputStream>() {
				public OutputStream run() throws IOException {
					return impl.getOutputStream();
				}
			});
		} catch (java.security.PrivilegedActionException e) {
			throw (IOException) e.getException();
		}
		return os;
	}

	/**
	 * Closes this socket.
	 * <p>
	 * Any thread currently blocked in an I/O operation upon this socket will
	 * throw a {@link SocketException}.
	 * <p>
	 * Once a socket has been closed, it is not available for further networking
	 * use (i.e. can't be reconnected or rebound). A new socket needs to be
	 * created.
	 *
	 * <p>
	 * Closing this socket will also close the socket's
	 * {@link java.io.InputStream InputStream} and {@link java.io.OutputStream
	 * OutputStream}.
	 *
	 * <p>
	 * If this socket has an associated channel then the channel is closed as
	 * well.
	 *
	 * @exception IOException
	 *                if an I/O error occurs when closing this socket.
	 * @revised 1.4
	 * @spec JSR-51
	 * @see #isClosed
	 */
	private synchronized void internal_close() throws IOException {
		synchronized (closeLock) {
			if (isClosed())
				return;
			if (created)
				impl.close();
			closed = true;
		}
	}

	//@EpaAction(name = "close", enabledExceptionList = "java.io.Exception" )
	public synchronized void close() throws IOException {
		try {
			this.internal_close();
		} catch (Exception ex) {
			throw ex;
		}
	}

	/**
	 * Places the input stream for this socket at "end of stream". Any data sent
	 * to the input stream side of the socket is acknowledged and then silently
	 * discarded.
	 * <p>
	 * If you read from a socket input stream after invoking this method on the
	 * socket, the stream's {@code available} method will return 0, and its
	 * {@code read} methods will return {@code -1} (end of stream).
	 *
	 * @exception IOException
	 *                if an I/O error occurs when shutting down this socket.
	 *
	 * @since 1.3
	 * @see java.net.Socket#shutdownOutput()
	 * @see java.net.Socket#close()
	 * @see java.net.Socket#setSoLinger(boolean, int)
	 * @see #isInputShutdown
	 */
	//@EpaAction(name = "shutdownInput", enabledExceptionList = "java.io.Exception", notEnabledExceptionList = "java.net.SocketException")
	public void shutdownInput() throws IOException {
		try {
			internal_shutdownInput();
		} catch (Exception ex) {
			throw ex;
		}
	}

	private void internal_shutdownInput() throws SocketException, IOException {
		if (isClosed())
			throw new SocketException("Socket is closed");
		if (!isConnected())
			throw new SocketException("Socket is not connected");
		if (isInputShutdown())
			throw new SocketException("Socket input is already shutdown");
		getImpl().shutdownInput();
		shutIn = true;
	}

	/**
	 * Disables the output stream for this socket. For a TCP socket, any
	 * previously written data will be sent followed by TCP's normal connection
	 * termination sequence.
	 *
	 * If you write to a socket output stream after invoking shutdownOutput() on
	 * the socket, the stream will throw an IOException.
	 *
	 * @exception IOException
	 *                if an I/O error occurs when shutting down this socket.
	 *
	 * @since 1.3
	 * @see java.net.Socket#shutdownInput()
	 * @see java.net.Socket#close()
	 * @see java.net.Socket#setSoLinger(boolean, int)
	 * @see #isOutputShutdown
	 */
	//@EpaAction(name = "shutdownOutput", enabledExceptionList = "java.io.Exception", notEnabledExceptionList = "java.net.SocketException")
	public void shutdownOutput() throws IOException {
		try {
			internal_shutdownOutput();
		} catch (Exception ex) {
			throw ex;
		}
	}

	private void internal_shutdownOutput() throws SocketException, IOException {
		if (isClosed())
			throw new SocketException("Socket is closed");
		if (!isConnected())
			throw new SocketException("Socket is not connected");
		if (isOutputShutdown())
			throw new SocketException("Socket output is already shutdown");
		getImpl().shutdownOutput();
		shutOut = true;
	}

	/**
	 * Returns the connection state of the socket.
	 * <p>
	 * Note: Closing a socket doesn't clear its connection state, which means
	 * this method will return {@code true} for a closed socket (see
	 * {@link #isClosed()}) if it was successfuly connected prior to being
	 * closed.
	 *
	 * @return true if the socket was successfuly connected to a server
	 * @since 1.4
	 */
	private boolean isConnected() {
		// Before 1.3 Sockets were always connected during creation
		return connected || oldImpl;
	}

	/**
	 * Returns the binding state of the socket.
	 * <p>
	 * Note: Closing a socket doesn't clear its binding state, which means this
	 * method will return {@code true} for a closed socket (see
	 * {@link #isClosed()}) if it was successfuly bound prior to being closed.
	 *
	 * @return true if the socket was successfuly bound to an address
	 * @since 1.4
	 * @see #bind
	 */
	private boolean isBound() {
		// Before 1.3 Sockets were always bound during creation
		return bound || oldImpl;
	}

	/**
	 * Returns the closed state of the socket.
	 *
	 * @return true if the socket has been closed
	 * @since 1.4
	 * @see #close
	 */
	private boolean isClosed() {
		synchronized (closeLock) {
			return closed;
		}
	}

	/**
	 * Returns whether the read-half of the socket connection is closed.
	 *
	 * @return true if the input of the socket has been shutdown
	 * @since 1.4
	 * @see #shutdownInput
	 */
	private boolean isInputShutdown() {
		return shutIn;
	}

	/**
	 * Returns whether the write-half of the socket connection is closed.
	 *
	 * @return true if the output of the socket has been shutdown
	 * @since 1.4
	 * @see #shutdownOutput
	 */
	private boolean isOutputShutdown() {
		return shutOut;
	}

	/**
	 * The factory for all client sockets.
	 */
	private static MSocketImplFactory factory = null;

	private final Stack<Boolean> ioExceptions = new Stack<Boolean>();

	public void pushIOException(boolean shouldThrowIOException) {
		ioExceptions.push(shouldThrowIOException);
	}

	public boolean shouldThrowIOException() {
		if (ioExceptions.isEmpty()) {
			return false;
		} else {
			boolean b = ioExceptions.pop();
			return b;
		}
	}

	/*
	 * -------------------------------------------------------------------------
	 * Instrumentation for state checking
	 * -------------------------------------------------------------------------
	 */
	/*@EpaState(name = "S1")
	private boolean isS1() {
		return isConnectEnabled() && isBindEnabled() && isClosedEnabled() && !isGetInputStreamEnabled()
				&& !isGetOutputStreamEnabled() && !isShutdownInputEnabled() && !isShutdownOutputEnabled();
	}

	@EpaState(name = "S2")
	private boolean isS2() {
		return isConnectEnabled() && !isBindEnabled() && isClosedEnabled() && !isGetInputStreamEnabled()
				&& !isGetOutputStreamEnabled() && !isShutdownInputEnabled() && !isShutdownOutputEnabled();
	}

	@EpaState(name = "S3")
	private boolean isS3() {
		return !isConnectEnabled() && !isBindEnabled() && isClosedEnabled() && isGetInputStreamEnabled()
				&& isGetOutputStreamEnabled() && isShutdownInputEnabled() && isShutdownOutputEnabled();
	}

	@EpaState(name = "S4")
	private boolean isS4() {
		return !isConnectEnabled() && !isBindEnabled() && isClosedEnabled() && !isGetInputStreamEnabled()
				&& isGetOutputStreamEnabled() && !isShutdownInputEnabled() && isShutdownOutputEnabled();
	}

	@EpaState(name = "S5")
	private boolean isS5() {
		return !isConnectEnabled() && !isBindEnabled() && isClosedEnabled() && isGetInputStreamEnabled()
				&& !isGetOutputStreamEnabled() && isShutdownInputEnabled() && !isShutdownOutputEnabled();
	}

	@EpaState(name = "S6")
	private boolean isS6() {
		return !isConnectEnabled() && !isBindEnabled() && isClosedEnabled() && !isGetInputStreamEnabled()
				&& !isGetOutputStreamEnabled() && !isShutdownInputEnabled() && !isShutdownOutputEnabled();
	}*/

	//private boolean isShutdownInputEnabled() {
	private boolean shutdownInput_pre() {
		return !isClosed() && isConnected() && !isInputShutdown();
	}

	//private boolean isShutdownOutputEnabled() {
	private boolean shutdownOutput_pre() {
		return !isClosed() && isConnected() && !isOutputShutdown();
	}

	//private boolean isConnectEnabled() {
	private boolean connect_pre() {
		return !isClosed() && (oldImpl || !isConnected());
	}

	//private boolean isBindEnabled() {
	private boolean bind_pre() {
		return !isClosed() && (oldImpl || !isBound());
	}

	//private boolean isGetInputStreamEnabled() {
	private boolean getInputStream_pre() {
		return !isClosed() && isConnected() && !isInputShutdown() && !isClosedOrPending() && !shutIn;
	}

	//private boolean isGetOutputStreamEnabled() {
	private boolean getOutputStream_pre() {
		return !isClosed() && isConnected() && !isOutputShutdown() && !isClosedOrPending() && !shutOut;
	}

	//private boolean isClosedEnabled() {
	private boolean close_pre() {
		return true;
	}

	private boolean isClosedOrPending() {
		// TODO: PlainSocketImpl has the isClosedOrPending method declared, but
		// the EvoSuite's mock SocketImpl has no isClosedOrPending
		// We need to extend EvoSuite's mock to support this
		return false;
	}

	private boolean inv(){

		if (!isConnected() && (isInputShutdown() || isOutputShutdown()))
			return false;

		return true;
	}
}
