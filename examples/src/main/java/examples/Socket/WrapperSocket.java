package examples.Socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.channels.SocketChannel;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

/**
 * Created by Usuario on 14/09/2017.
 */
public class WrapperSocket extends Socket {

    // ****************** CONSTRUCTORES ************************

    public static boolean WrapperSocket_pre() {
        return true;
    }

    /**
     * Creates an unconnected socket, with the
     * system-default type of SocketImpl.
     *
     * @since   JDK1.1
     * @revised 1.4
     */

    public WrapperSocket(){
        super(); // Socket
    }


    public boolean bind_wrapper_pre() {
        if (isClosed())
            return false;

        if (!oldImpl && isBound())
            return false;

        return true;
    }
    public boolean bind_wrapper_pre(SocketAddress bindpoint) {

        // Los chequeos sobre tipos no se traducen correctamente a boogie
        // la funcion instanceof es totalmente no deterministica
        // porque no hay tipos en nuestro modelo de boogie
        // es por eso que hay transiciones con excepciones.

        if (bindpoint != null && (!(bindpoint instanceof InetSocketAddress)))
            return false;

        InetSocketAddress epoint = (InetSocketAddress) bindpoint;

        if (epoint != null && epoint.isUnresolved())
            return false;

        InetAddress addr = epoint.getAddress();

        if (addr == null)
            return false;

        // viene del checkAddress
        if (!(addr instanceof Inet4Address || addr instanceof Inet6Address))
            return false;

        return true;
    }

    /**
     * Binds the socket to a local address.
     * <P>
     * If the address is <code>null</code>, then the system will pick up
     * an ephemeral port and a valid local address to bind the socket.
     *
     * @param   bindpoint the <code>SocketAddress</code> to bind to
     * @throws  IOException if the bind operation fails, or if the socket
     *                     is already bound.
     * @throws  IllegalArgumentException if bindpoint is a
     *          SocketAddress subclass not supported by this socket
     *
     * @since   1.4
     * @see #isBound
     */

    public void bind_wrapper(SocketAddress bindpoint) throws IOException {
        super.bind(bindpoint);
        super.getImpl().localport = 10; // puerto valido mock
    }


    public boolean close_wrapper_pre() throws IOException {
        return true;
    }

    /**
     * Closes this socket.
     * <p>
     * Any thread currently blocked in an I/O operation upon this socket
     * will throw a {@link SocketException}.
     * <p>
     * Once a socket has been closed, it is not available for further networking
     * use (i.e. can't be reconnected or rebound). A new socket needs to be
     * created.
     *
     * <p> Closing this socket will also close the socket's
     * {@link java.io.InputStream InputStream} and
     * {@link java.io.OutputStream OutputStream}.
     *
     * <p> If this socket has an associated channel then the channel is closed
     * as well.
     *
     * @exception  IOException  if an I/O error occurs when closing this socket.
     * @revised 1.4
     * @spec JSR-51
     * @see #isClosed
     */


    public void close_wrapper() throws IOException {
        super.close();
    }


    public boolean connect_wrapper_pre(){
        if (super.isClosed())
            return false;

        if (!super.oldImpl && super.isConnected())
            return false;

        return true;
    }

    public boolean connect_wrapper_pre(SocketAddress socketAddress){
        return !(socketAddress == null) && ! (!(socketAddress instanceof InetSocketAddress));
    }

    /**
     * Connects this socket to the server.
     *
     * @param   endpoint the <code>SocketAddress</code>
     * @throws  IOException if an error occurs during the connection
     * @throws  java.nio.channels.IllegalBlockingModeException
     *          if this socket has an associated channel,
     *          and the channel is in non-blocking mode
     * @throws  IllegalArgumentException if endpoint is null or is a
     *          SocketAddress subclass not supported by this socket
     * @since 1.4
     * @spec JSR-51
     */

    public void connect_wrapper(SocketAddress socketAddress) throws IOException {
        super.connect(socketAddress);
        super.getImpl().localport = 10; // puerto valido mock
        super.getImpl().port = 10; // hack: no hay dynamic dispatch entonces no se ejecuta el AbstractPlainSocketImpl con su connect que asigna el puerto
                                    // si pasa eso, podemos meter en el pre de este metodo que el socketAddress tenga puerto valido.
    }


    /*public boolean  sendUrgentData_wrapper_pre() throws SocketException {
        if (!getImpl().supportsUrgentData ()) {
            return false;
        }

        return true;
    }*/
    /**
     * Send one byte of urgent data on the socket. The byte to be sent is the lowest eight
     * bits of the data parameter. The urgent byte is
     * sent after any preceding writes to the socket OutputStream
     * and before any future writes to the OutputStream.
     * @param data The byte of data to send
     * @exception IOException if there is an error
     *  sending the data.
     * @since 1.4
     */
    //public void sendUrgentData_wrapper (int data) throws IOException  {
    //    super.sendUrgentData(data);
    //}


    /*public boolean getChannel_wrapper_pre(){
        return true;
    }*/

    /**
     * Returns the unique {@link java.nio.channels.SocketChannel SocketChannel}
     * object associated with this socket, if any.
     *
     * <p> A socket will have a channel if, and only if, the channel itself was
     * created via the {@link java.nio.channels.SocketChannel#open
     * SocketChannel.open} or {@link
     * java.nio.channels.ServerSocketChannel#accept ServerSocketChannel.accept}
     * methods.
     *
     * @return  the socket channel associated with this socket,
     *          or <tt>null</tt> if this socket was not created
     *          for a channel
     *
     * @since 1.4
     * @spec JSR-51
     */

    /*public SocketChannel getChannel_wrapper() {
        return super.getChannel();
    }*/

    /**
     * Returns the address to which the socket is connected.
     * <p>
     * If the socket was connected prior to being {@link #close closed},
     * then this method will continue to return the connected address
     * after the socket is closed.
     *
     * @return  the remote IP address to which this socket is connected,
     *          or <code>null</code> if the socket is not connected.
     */

    /*public boolean getInetAddress_wrapper_pre() { return true;}
    public InetAddress getInetAddress_wrapper() { return super.getInetAddress();}*/


    /**
     * Returns an input stream for this socket.
     *
     * <p> If this socket has an associated channel then the resulting input
     * stream delegates all of its operations to the channel.  If the channel
     * is in non-blocking mode then the input stream's <tt>read</tt> operations
     * will throw an {@link java.nio.channels.IllegalBlockingModeException}.
     *
     * <p>Under abnormal conditions the underlying connection may be
     * broken by the remote host or the network software (for example
     * a connection reset in the case of TCP connections). When a
     * broken connection is detected by the network software the
     * following applies to the returned input stream :-
     *
     * <ul>
     *
     *   <li><p>The network software may discard bytes that are buffered
     *   by the socket. Bytes that aren't discarded by the network
     *   software can be read using {@link java.io.InputStream#read read}.
     *
     *   <li><p>If there are no bytes buffered on the socket, or all
     *   buffered bytes have been consumed by
     *   {@link java.io.InputStream#read read}, then all subsequent
     *   calls to {@link java.io.InputStream#read read} will throw an
     *   {@link java.io.IOException IOException}.
     *
     *   <li><p>If there are no bytes buffered on the socket, and the
     *   socket has not been closed using {@link #close close}, then
     *   {@link java.io.InputStream#available available} will
     *   return <code>0</code>.
     *
     * </ul>
     *
     * <p> Closing the returned {@link java.io.InputStream InputStream}
     * will close the associated socket.
     *
     * @return     an input stream for reading bytes from this socket.
     * @exception  IOException  if an I/O error occurs when creating the
     *             input stream, the socket is closed, the socket is
     *             not connected, or the socket input has been shutdown
     *             using {@link #shutdownInput()}
     *
     * @revised 1.4
     * @spec JSR-51
     */

    public boolean getInputStream_wrapper_pre(){
        if (isClosed())
            return false;

        if (!isConnected())
            return false;

        if (isInputShutdown())
            return false;

        return true;
    }

    public InputStream getInputStream_wrapper() throws IOException {

        // workaround for dynamic dispatch
        if (isClosed())
            throw new SocketException("Socket is closed");
        if (!isConnected())
            throw new SocketException("Socket is not connected");
        if (isInputShutdown())
            throw new SocketException("Socket input is shutdown");
        final Socket s = this;
        InputStream is = null;
        try {
            is = AccessController.doPrivileged(
                    new PrivilegedExceptionAction<InputStream>() {
                        public InputStream run() throws IOException {
                            AbstractPlainSocketImpl abstImpl = (AbstractPlainSocketImpl)impl;  // workaround
                            return abstImpl.getInputStream();
                        }
                    });
        } catch (java.security.PrivilegedActionException e) {
            throw (IOException) e.getException();
        }
        return is;

        //return super.getInputStream();
    }


    /**
     * Places the input stream for this socket at "end of stream".
     * Any data sent to the input stream side of the socket is acknowledged
     * and then silently discarded.
     * <p>
     * If you read from a socket input stream after invoking
     * shutdownInput() on the socket, the stream will return EOF.
     *
     * @exception IOException if an I/O error occurs when shutting down this
     * socket.
     *
     * @since 1.3
     * @see java.net.Socket#shutdownOutput()
     * @see java.net.Socket#close()
     * @see java.net.Socket#setSoLinger(boolean, int)
     * @see #isInputShutdown
     */

    public boolean shutdownInput_wrapper_pre() throws IOException
    {
        if (isClosed())
            return false;
        if (!isConnected())
            return false;
        if (isInputShutdown())
            return false;

        return true;
    }

    public void shutdownInput_wrapper() throws IOException
    {
        // workaround for dynamic dispatch

        AbstractPlainSocketImpl abstImpl = (AbstractPlainSocketImpl)getImpl();

        abstImpl.shutdownInput();
        shutIn = true;

        //super.shutdownInput();
    }

    /**
     * Returns an output stream for this socket.
     *
     * <p> If this socket has an associated channel then the resulting output
     * stream delegates all of its operations to the channel.  If the channel
     * is in non-blocking mode then the output stream's <tt>write</tt>
     * operations will throw an {@link
     * java.nio.channels.IllegalBlockingModeException}.
     *
     * <p> Closing the returned {@link java.io.OutputStream OutputStream}
     * will close the associated socket.
     *
     * @return     an output stream for writing bytes to this socket.
     * @exception  IOException  if an I/O error occurs when creating the
     *               output stream or if the socket is not connected.
     * @revised 1.4
     * @spec JSR-51
     */
    public boolean getOutputStream_wrapper_pre() throws IOException {
        if (isClosed())
            return false;
        if (!isConnected())
            return false;
        if (isOutputShutdown())
            return false;

        return true;
    }

    public OutputStream getOutputStream_wrapper() throws IOException {
        if (isClosed())
            throw new SocketException("Socket is closed");
        if (!isConnected())
            throw new SocketException("Socket is not connected");
        if (isOutputShutdown())
            throw new SocketException("Socket output is shutdown");
        final Socket s = this;
        OutputStream os = null;
        try {
            os = AccessController.doPrivileged(
                    new PrivilegedExceptionAction<OutputStream>() {
                        public OutputStream run() throws IOException {
                            AbstractPlainSocketImpl absImpl = (AbstractPlainSocketImpl)impl;
                            return absImpl.getOutputStream();
                        }
                    });
        } catch (java.security.PrivilegedActionException e) {
            throw (IOException) e.getException();
        }
        return os;

    }

    /**
     * Disables the output stream for this socket.
     * For a TCP socket, any previously written data will be sent
     * followed by TCP's normal connection termination sequence.
     *
     * If you write to a socket output stream after invoking
     * shutdownOutput() on the socket, the stream will throw
     * an IOException.
     *
     * @exception IOException if an I/O error occurs when shutting down this
     * socket.
     *
     * @since 1.3
     * @see java.net.Socket#shutdownInput()
     * @see java.net.Socket#close()
     * @see java.net.Socket#setSoLinger(boolean, int)
     * @see #isOutputShutdown
     */

    public boolean shutdownOutput_wrapper_pre()
    {
        if (isClosed())
            return false;
        if (!isConnected())
            return false;
        if (isOutputShutdown())
            return false;

        return true;
    }

    public void shutdownOutput_wrapper() throws IOException {
        if (isClosed())
            throw new SocketException("Socket is closed");
        if (!isConnected())
            throw new SocketException("Socket is not connected");
        if (isOutputShutdown())
            throw new SocketException("Socket output is already shutdown");
        AbstractPlainSocketImpl absImpl = (AbstractPlainSocketImpl)getImpl();
        absImpl.shutdownOutput();
        shutOut = true;
    }


    /**
     * Tests if SO_KEEPALIVE is enabled.
     *
     * @return a <code>boolean</code> indicating whether or not SO_KEEPALIVE is enabled.
     * @exception SocketException if there is an error
     * in the underlying protocol, such as a TCP error.
     * @since   1.3
     * @see #setKeepAlive(boolean)
     */

    /*public boolean getKeepAlive_wrapper_pre() {
        if (isClosed())
            return false;
        return true;
    }

    public boolean getKeepAlive_wrapper() throws SocketException {
        return super.getKeepAlive();
    }*/


    /**
     * Enable/disable SO_KEEPALIVE.
     *
     * @param on     whether or not to have socket keep alive turned on.
     * @exception SocketException if there is an error
     * in the underlying protocol, such as a TCP error.
     * @since 1.3
     * @see #getKeepAlive()
     */

    /*public boolean setKeepAlive_wrapper_pre(){
        if (isClosed())
            return false;
        return true;
    }
    public void setKeepAlive_wrapper(boolean on) throws SocketException {
        super.setKeepAlive(on);
    }*/

    /**
     * Gets the local address to which the socket is bound.
     *
     * @return the local address to which the socket is bound, or
     *         the {@link InetAddress#isAnyLocalAddress wildcard} address
     *         if the socket is closed or not bound yet.
     * @since   JDK1.1
     */

    /*public boolean getLocalAddress_wrapper_pre(){
        return true;
    }
    public InetAddress getLocalAddress_wrapper() {
        return super.getLocalAddress();
    }*/


    /**
     * Returns the local port number to which this socket is bound.
     * <p>
     * If the socket was bound prior to being {@link #close closed},
     * then this method will continue to return the local port number
     * after the socket is closed.
     *
     * @return  the local port number to which this socket is bound or -1
     *          if the socket is not bound yet.
     */

    /*public boolean getLocalPort_wrapper_pre(){
        return true;
    }

    public int getLocalPort_wrapper() {
        return super.getLocalPort();
    }*/


    /**
     * Returns the address of the endpoint this socket is bound to, or
     * <code>null</code> if it is not bound yet.
     * <p>
     * If a socket bound to an endpoint represented by an
     * <code>InetSocketAddress </code> is {@link #close closed},
     * then this method will continue to return an <code>InetSocketAddress</code>
     * after the socket is closed. In that case the returned
     * <code>InetSocketAddress</code>'s address is the
     * {@link InetAddress#isAnyLocalAddress wildcard} address
     * and its port is the local port that it was bound to.
     *
     * @return a <code>SocketAddress</code> representing the local endpoint of this
     *         socket, or <code>null</code> if it is not bound yet.
     * @see #getLocalAddress()
     * @see #getLocalPort()
     * @see #bind(SocketAddress)
     * @since 1.4
     */

    /*public boolean getLocalSocketAddress_wrapper_pre() {
        return true;
    }

    public SocketAddress getLocalSocketAddress_wrapper() {
        return super.getLocalSocketAddress();
    }*/


    // esta aca anda

    /**
     * Tests if OOBINLINE is enabled.
     *
     * @return a <code>boolean</code> indicating whether or not OOBINLINE is enabled.
     * @exception SocketException if there is an error
     * in the underlying protocol, such as a TCP error.
     * @since   1.4
     * @see #setOOBInline(boolean)
     */

    /*public boolean getOOBInline_wrapper_pre() {
        if (isClosed())
            return false;

        return true;
    }

    public boolean getOOBInline_wrapper() throws SocketException {

        return super.getOOBInline();
    }*/


    /**
     * Returns the remote port number to which this socket is connected.
     * <p>
     * If the socket was connected prior to being {@link #close closed},
     * then this method will continue to return the connected port number
     * after the socket is closed.
     *
     * @return  the remote port number to which this socket is connected, or
     *          0 if the socket is not connected yet.
     */

    /*public boolean getPort_wrapper_pre(){
        return true;
    }

    public int getPort_wrapper() {
        return super.getPort();
    }*/

    /**
     * Gets the value of the SO_RCVBUF option for this <tt>Socket</tt>,
     * that is the buffer size used by the platform for
     * input on this <tt>Socket</tt>.
     *
     * @return the value of the SO_RCVBUF option for this <tt>Socket</tt>.
     * @exception SocketException if there is an error
     * in the underlying protocol, such as a TCP error.
     * @see #setReceiveBufferSize(int)
     * @since 1.2
     */

    /*public boolean getReceiveBufferSize_wrapper_pre() {
        if (isClosed())
            return false;

        return true;
    }

    public int getReceiveBufferSize_wrapper() throws SocketException {
        return super.getReceiveBufferSize();
    }*/

    /**
     * Returns the address of the endpoint this socket is connected to, or
     * <code>null</code> if it is unconnected.
     * <p>
     * If the socket was connected prior to being {@link #close closed},
     * then this method will continue to return the connected address
     * after the socket is closed.
     *

     * @return a <code>SocketAddress</code> representing the remote endpoint of this
     *         socket, or <code>null</code> if it is not connected yet.
     * @see #getInetAddress()
     * @see #getPort()
     * @see #connect(SocketAddress, int)
     * @see #connect(SocketAddress)
     * @since 1.4
     */
    /*public SocketAddress getRemoteSocketAddress_wrapper() {
        return super.getRemoteSocketAddress();
    }

    public boolean getRemoteSocketAddress_wrapper_pre() {
        return true;
    }*/


    /**
     * Tests if SO_REUSEADDR is enabled.
     *
     * @return a <code>boolean</code> indicating whether or not SO_REUSEADDR is enabled.
     * @exception SocketException if there is an error
     * in the underlying protocol, such as a TCP error.
     * @since   1.4
     * @see #setReuseAddress(boolean)
     */

    /*public boolean getReuseAddress_wrapper_pre(){
        if (isClosed())
            return false;

        return true;
    }

    public boolean getReuseAddress_wrapper() throws SocketException {
        return super.getReuseAddress();
    }*/


    /**
     * Get value of the SO_SNDBUF option for this <tt>Socket</tt>,
     * that is the buffer size used by the platform
     * for output on this <tt>Socket</tt>.
     * @return the value of the SO_SNDBUF option for this <tt>Socket</tt>.
     *
     * @exception SocketException if there is an error
     * in the underlying protocol, such as a TCP error.
     *
     * @see #setSendBufferSize(int)
     * @since 1.2
     */
    /*public  int getSendBufferSize_wrapper() throws SocketException {
        return super.getSendBufferSize();
    }

    public boolean getSendBufferSize_wrapper_pre(){
        if (isClosed())
            return false;

        return true;
    }*/

    /**
     * Returns setting for SO_LINGER. -1 returns implies that the
     * option is disabled.
     *
     * The setting only affects socket close.
     *
     * @return the setting for SO_LINGER.
     * @exception SocketException if there is an error
     * in the underlying protocol, such as a TCP error.
     * @since   JDK1.1
     * @see #setSoLinger(boolean, int)
     */
    /*public int getSoLinger_wrapper() throws SocketException {
        return super.getSoLinger();
    }

    public boolean getSoLinger_wrapper_pre(){
        if (isClosed())
            return false;
        return true;
    }*/

    /**
     * Returns setting for SO_TIMEOUT.  0 returns implies that the
     * option is disabled (i.e., timeout of infinity).
     * @return the setting for SO_TIMEOUT
     * @exception SocketException if there is an error
     * in the underlying protocol, such as a TCP error.
     * @since   JDK1.1
     * @see #setSoTimeout(int)
     */
    /*public int getSoTimeout_wrapper() throws SocketException {
        return super.getSoTimeout();
    }

    public boolean getSoTimeout_wrapper_pre(){
        if (isClosed())
            return false;

        return true;
    }*/

    /*public boolean setOOBInline_wrapper_pre(){
        if (isClosed())
            return false;
        return true;
    }

    public void setOOBInline_wrapper(boolean on) throws SocketException {
        super.setOOBInline(on);
    }*/

    //public void setPerformancePreferences(int connectionTime,
    //                                      int latency,
    //                                      int bandwidth)
    //{
        /* Not implemented yet */
    //}

    /*public boolean setReceiveBufferSize_wrapper_pre(int size)
    {
        if (size <= 0) {
            return false;
        }
        return true;
    }

    public boolean setReceiveBufferSize_wrapper_pre()
    {
        if (isClosed()) {
            return false;
        }
        return true;
    }
    public synchronized void setReceiveBufferSize_wrapper(int size)
            throws SocketException{
        super.setReceiveBufferSize(size);
    }


    public boolean setReuseAddress_wrapper_pre(){
        if (isClosed())
            return false;

        return true;
    }

    public void setReuseAddress_wrapper(boolean on) throws SocketException {
        super.setReuseAddress(on);
    }

    public boolean setSoLinger_wrapper_pre(boolean on, int linger){
        if (on && linger < 0)
            return false;

        return true;
    }

    public boolean setSoLinger_wrapper_pre(){
        if (isClosed())
            return false;

        return true;
    }

    public void setSoLinger_wrapper(boolean on, int linger) throws SocketException {
        super.setSoLinger(on,linger);
    }


    public boolean setSoTimeout_wrapper_pre(int timeout){
        if (timeout < 0)
            return false;
        return true;
    }

    public boolean setSoTimeout_wrapper_pre(){
        if (isClosed())
            return false;

        return true;
    }

    public synchronized void setSoTimeout_wrapper(int timeout) throws SocketException {
        super.setSoTimeout(timeout);
    }

    public boolean setTcpNoDelay_wrapper_pre(){
        if (isClosed())
            return false;
        return true;
    }
    public void setTcpNoDelay_wrapper(boolean on) throws SocketException {
        super.setTcpNoDelay(on);
    }

    public boolean setTrafficClass_wrapper_pre(int tc){
        if (tc < 0 || tc > 255)
            return false;
        return true;
    }

    public boolean setTrafficClass_wrapper_pre(){
        if (isClosed())
            return false;

        return true;
    }
    public void setTrafficClass_wrapper(int tc) throws SocketException {
        super.setTrafficClass(tc);
    }*/

    // Para evitar la transicion de error
    // mockear el getLocalPort de SocketImpl o al que se llame que ahora esta como no deterministico
    // poner ensures para que este en el rango especifico ademeas tambien poner como pre del bind
    public boolean inv(){
        if (impl == null)
            return false;

        if (oldImpl == true)
            return false;

        if (!isConnected() && (isInputShutdown() || isOutputShutdown()))
            return false;

        if (isConnected() && (getPort() < 0 || getPort() > 0xFFFF))
            return false;

        if (isBound() && (getLocalPort() < 0 || getLocalPort() > 0xFFFF))
            return false;

        return true;
    }

/*
//procedure {:extern} java.util.Enumeration#hasMoreElements($this : Ref) returns (r : bool);
//procedure {:extern} java.util.Enumeration#nextElement($this : Ref) returns (r : Ref);
//procedure {:extern} java.util.Iterator#hasNext($this : Ref) returns (r : bool);
//procedure {:extern} java.util.Iterator#next($this : Ref) returns (r : Ref);
//procedure {:extern} sun.net.spi.nameservice.NameServiceDescriptor#getType($this : Ref) returns (r : Ref);
//procedure {:extern} sun.net.spi.nameservice.NameServiceDescriptor#getProviderName($this : Ref) returns (r : Ref);
//procedure {:extern} sun.net.spi.nameservice.NameServiceDescriptor#createNameService($this : Ref) returns (r : Ref);
//procedure {:extern} java.util.Set#iterator($this : Ref) returns (r : Ref);
//procedure {:extern} java.util.List#iterator($this : Ref) returns (r : Ref);
//procedure {:extern} sun.net.spi.nameservice.NameService#lookupAllHostAddr$java.lang.String($this : Ref, param00 : Ref) returns (r : Ref);
//procedure {:extern} sun.net.spi.nameservice.NameService#getHostByAddr$Ref($this : Ref, param00 : Ref) returns (r : Ref);
//procedure {:extern} java.util.List#add$int$java.lang.Object($this : Ref, param00 : int, param01 : Ref);
//procedure {:extern} java.util.List#get$int($this : Ref, param00 : int) returns (r : Ref);
//procedure {:extern} java.util.List#addAll$java.util.Collection($this : Ref, param00 : Ref) returns (r : bool);
//procedure {:extern} java.net.Inet6Address.?clinit?();
 */

}

