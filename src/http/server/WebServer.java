package http.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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
     * WebServer core function that starts a server on the port and listen to requests,
     * responds to different requests.
     * By default, the port number is 3000
     * Warning, some requests may not be supported
     * @param port the port to which the server will listen
     */
    protected void start(int port) {
        ServerSocket s;

        System.out.println("Webserver starting up on port " + port);
        System.out.println("(press ctrl-c to exit)");
        try {
            // create the main server socket
            s = new ServerSocket(port);
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
                WebServlet webServlet = new WebServlet(out);

//                 read the data sent. We basically ignore it,
//                 stop reading once a blank line is hit. This
//                 blank line signals the end of the client HTTP
//                 headers.
                StringBuffer request = new StringBuffer();
                String str = ".";
                while (str != null && !str.equals("")){
                    str = in.readLine();
                    webServlet.fillHeaders(str);
                    request.append(str);
                    System.out.println(str);
                }

                if (request.toString().isEmpty() || request.toString().equals("")) {
                    remote.close();
                    continue;
                }

                byte[] body = null;
                if (request.toString().contains("Content-Length")) {
                    char[] bodyBuffer = new char[webServlet.getContentLength()];
                    in.read(bodyBuffer, 0, webServlet.getContentLength());

                    String postData = new String(bodyBuffer, 0, bodyBuffer.length);
                    System.out.println(postData);

                    Charset cs = StandardCharsets.UTF_8;
                    CharBuffer cb = CharBuffer.allocate(bodyBuffer.length);
                    cb.put(bodyBuffer);
                    cb.flip();
                    ByteBuffer bb = cs.encode(cb);
                    body = bb.array();
                }



                String requestType = WebServlet.getRequestType(request);
                System.out.println("requestType : " + requestType);
                if (requestType.equals("GET")) {
                    String requestFile = webServlet.getResourceFileName(request);
                    webServlet.httpGET(requestFile);
                } else if (requestType.equals("DELETE")) {
                    String deleteFile = webServlet.getLocalResourceFileName(request);
                    webServlet.httpDELETE(deleteFile);
                } else if (requestType.equals("PUT")) {
                    String putFile = webServlet.getLocalResourceFileName(request);
                    webServlet.httpPUT(body, putFile);
                } else if (requestType.equals("POST")) {
                    String putFile = webServlet.getLocalResourceFileName(request);
                    webServlet.httpPOST(body, putFile);
                } else if (requestType.equals("HEAD")) {
                    String requestFile = webServlet.getResourceFileName(request);
                    webServlet.httpHEAD(requestFile);
                }
                remote.close();

            } catch (Exception e) {

                System.out.println("Error: " + e);
                try{
                    Socket remote = s.accept();
                    BufferedOutputStream out = new BufferedOutputStream(remote.getOutputStream());
                    String error500 = WebServlet.internalErrorMsg();
                    out.write(WebServlet.header("", error500.getBytes().length, "500 internal error").getBytes());
                    out.write(error500.getBytes());
                    out.flush();
                } catch (Exception ex) {
                    System.out.println("Fatal Error: " + ex);
                }
            }
        }
    }

    /**
     * Start the application.
     *
     * @param args the first args, if given, should be the port number
     */
    public static void main(String[] args) {
      WebServer ws = new WebServer();
      int port = 3000;
      if (args.length > 0 ) {
          port = Integer.parseInt(args[0]);
      }
      ws.start(port);
    }
}
