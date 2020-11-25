package http.client;

import javax.xml.transform.stream.StreamResult;
import java.net.InetAddress;
import java.net.Socket;

public class WebPing {
    /**
     * the main function for a web ping using the socket to connect to the server.
     * by default, the host name is localhost, and the server port is 3000,
     * they can be changed by passing the parameters using args[]
     * @param args arguments for host and port
     */
    public static void main(String[] args) {
  

        String host = "localhost";
        int port = 3000;

        if (args.length > 0 && args.length == 2) {
            host = args[0];
            port = Integer.parseInt(args[1]);
        }


        String httpServerHost = host;
        int httpServerPort = port;


        try {
            InetAddress addr;
            Socket sock = new Socket(httpServerHost, httpServerPort);
            addr = sock.getInetAddress();
            System.out.println("Connected to " + addr);
            sock.close();
        } catch (java.io.IOException e) {
            System.out.println("Can't connect to " + httpServerHost + ":" + httpServerPort);
            System.out.println(e);
        }
    }
}