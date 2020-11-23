///A Simple Web Server (WebServer.java)

package http.server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Example program from Chapter 1 Programming Spiders, Bots and Aggregators in
 * Java Copyright 2001 by Jeff Heaton
 * 
 * WebServer is a very simple web-server. Any request is responded with a very
 * simple web-page.
 * 
 * @author Jeff Heaton
 * @version 1.0
 */
public class WebServer {

    /**
     * WebServer constructor.
     */
    protected void start() {
        ServerSocket s;

        System.out.println("Webserver starting up on port 3000");
        System.out.println("(press ctrl-c to exit)");
        try {
            // create the main server socket
            s = new ServerSocket(3000);
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return;
        }

        System.out.println("Waiting for connection");
        for (;;) {
            try {
                // wait for a connection
                Socket remote = s.accept();
                // remote is now the connected socket
                System.out.println("Connection, sending data.");
                BufferedReader in = new BufferedReader(new InputStreamReader(
                    remote.getInputStream()));
//                PrintWriter out = new PrintWriter(remote.getOutputStream());
                BufferedOutputStream out = new BufferedOutputStream(remote.getOutputStream());
//                 read the data sent. We basically ignore it,
//                 stop reading once a blank line is hit. This
//                 blank line signals the end of the client HTTP
//                 headers.
                StringBuffer request = new StringBuffer();
                String str = ".";
                while (str != null && !str.equals("")){
                    str = in.readLine();
                    request.append(str);
                    System.out.println(str);
                }
                WebServlet webServlet = new WebServlet(out);
                String requestType = webServlet.getRequestType(request);

                if (requestType.equals("GET")) {
                    String requestFile = webServlet.getResourceFileName(request);
                    webServlet.httpGET(requestFile);
                } else if (requestType.equals("DELETE")) {

                }
                remote.close();

            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }
    }

    /**
     * Start the application.
     *
     * @param args
     *            Command line parameters are not used.
     */
    public static void main(String args[]) {
      WebServer ws = new WebServer();
      ws.start();
    }
}
