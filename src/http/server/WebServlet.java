package http.server;

import java.io.BufferedOutputStream;
import java.io.File;

public class WebServlet {
    public static String getRequestType(StringBuffer stringBuffer){
        String request = stringBuffer.toString();
        if (request.contains("GET")) {
            return "GET";
        } else if (request.contains("POST")) {
            return "POST";
        } else if (request.contains("PUT")){
            return "PUT";
        } else if (request.contains("DELETE")) {
            return "DELETE";
        }
        return "GET";
    }

    public static BufferedOutputStream httpGET(BufferedOutputStream out, String resourceFile) {
        File resourcePath = new File(resourceFile);
        if (resourcePath.exists() && resourcePath.isFile()) {

        }
        return out;
    }
}
