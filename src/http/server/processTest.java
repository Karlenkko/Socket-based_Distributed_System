package http.server;

import java.io.IOException;

public class processTest {
    public static void main(String[] args) throws IOException {
        try {
            Process process = Runtime.getRuntime().exec("cmd /c chdir");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
