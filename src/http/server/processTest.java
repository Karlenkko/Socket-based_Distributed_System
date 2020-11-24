package http.server;

import java.io.IOException;

public class processTest {
    public static void main(String[] args) throws IOException {
        try {
            TCPServerThread tcpServerThread = new TCPServerThread();
            tcpServerThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
