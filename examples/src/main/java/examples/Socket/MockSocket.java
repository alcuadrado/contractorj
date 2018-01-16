package examples.Socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketImpl;

public class MockSocket {
    // lo uso para MutableSocket


    // lo unico que agregamos es fijar los puertos
    // lo que esta comentado, se hace en boogie - asumimos que se asignan bien los puertos
    public static void connect(SocketImpl impl, String var1, int var2) throws IOException{
        impl.connect(var1, var2);
        //impl.localport = 10; // puerto valido mock
        //impl.port = 10; // hack: no hay dynamic dispatch entonces no se ejecuta el AbstractPlainSocketImpl con su connect que asigna el puerto
    }

    public static void connect(SocketImpl impl, InetAddress var1, int var2) throws IOException{
        impl.connect(var1, var2);
        //impl.localport = 10; // puerto valido mock
        //impl.port = 10; // hack: no hay dynamic dispatch entonces no se ejecuta el AbstractPlainSocketImpl con su connect que asigna el puerto
    }

    public static void connect(SocketImpl impl,SocketAddress var1, int var2) throws IOException{
        impl.connect(var1, var2);
        //impl.localport = 10; // puerto valido mock
        //impl.port = 10; // hack: no hay dynamic dispatch entonces no se ejecuta el AbstractPlainSocketImpl con su connect que asigna el puerto
    }

    public static void bind(SocketImpl impl, InetAddress var1, int var2) throws IOException {
        impl.bind(var1, var2);
        //impl.localport = 10; // puerto valido mock
    }
}
