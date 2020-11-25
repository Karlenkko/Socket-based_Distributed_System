package http.server;

import java.io.IOException;

/**
 * deprecated, DO NOT USE
 */
public class TCPServerThread extends Thread {
    /**
     * If this thread was constructed using a separate
     * <code>Runnable</code> run object, then that
     * <code>Runnable</code> object's <code>run</code> method is called;
     * otherwise, this method does nothing and returns.
     * <p>
     * Subclasses of <code>Thread</code> should override this method.
     *
     * @see #start()
     * @see #stop()
     */
    @Override
    @Deprecated
    public void run() {
        try {
            Process process = Runtime.getRuntime().exec("cmd /c java comTCP.EchoServerMultiThreaded 3499");
            byte[] results = new byte[255];
            int c = 0;
            int i = 0;
            while((c = process.getInputStream().read()) != -1){
                results[i] = (byte) c;
                ++i;
                System.out.println(new String(results));
            }

            System.out.println(new String(results));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
