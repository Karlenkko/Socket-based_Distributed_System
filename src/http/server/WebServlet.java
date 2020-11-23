package http.server;

import java.io.*;

public class WebServlet {
    private BufferedOutputStream out;

    WebServlet(BufferedOutputStream out) {
        this.out = out;
    }

    public String getResourceFileName(StringBuffer stringBuffer) {
        String request = stringBuffer.toString();
        String[] all = request.split(" ", 3);
        if (all[1].equals("/favicon.ico")) {
            return "";
        }
        return all[1];
    }

    public String deleteResourceFileName(StringBuffer stringBuffer) {
        String request = stringBuffer.toString();
        String[] all = request.split(" ", 3);
        if (all[1].contains("=")) {
            String[] query = all[1].split("=");
            return query[1];
        } else {
            return "";
        }
    }

    public String getRequestType(StringBuffer stringBuffer) {
        String request = stringBuffer.toString();
        if (request.contains("GET")) {
            return "GET";
        } else if (request.contains("POST")) {
            return "POST";
        } else if (request.contains("PUT")) {
            return "PUT";
        } else if (request.contains("DELETE")) {
            return "DELETE";
        }
        return "";
    }

    private String header(String resourceFile, long length, String status) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("HTTP/1.0 " + status + "\r\n");
        if (resourceFile.endsWith(".html") || resourceFile.endsWith(".htm")) {
            stringBuffer.append("Content-Type: text/html\r\n");
        } else if (resourceFile.endsWith(".mp4")) {
            stringBuffer.append("Content-Type: video/mp4\r\n");
        } else if (resourceFile.endsWith(".png")) {
            stringBuffer.append("Content-Type: image/png\r\n");
        } else if (resourceFile.endsWith(".jpeg") || resourceFile.endsWith(".jpg")) {
            stringBuffer.append("Content-Type: image/jpg\r\n");
        } else if (resourceFile.endsWith(".mp3")) {
            stringBuffer.append("Content-Type: audio/mp3\r\n");
        } else if (resourceFile.endsWith(".avi")) {
            stringBuffer.append("Content-Type: video/avi\r\n");
        } else if (resourceFile.endsWith(".pdf")) {
            stringBuffer.append("Content-Type: application/pdf\r\n");
        } else {
            // default
            stringBuffer.append("Content-Type: text/html\r\n");
            stringBuffer.append("Server: Bot\r\n");
            stringBuffer.append("\r\n");
            return stringBuffer.toString();
        }
        stringBuffer.append("Content-Length: " + length + "\r\n");
        stringBuffer.append("Server: Bot\r\n");
        stringBuffer.append("\r\n");
        return stringBuffer.toString();

    }

    private String internalErrorMsg() {
        StringBuffer msg = new StringBuffer();
        msg.append("<html>");
        msg.append("<head><title>500 Internal Error</title></head>");
        msg.append("<body><h1>500 Internal Error</h1>");
        msg.append("<p>There is an internal error in the server, please contact the administrator.</p></body>");
        msg.append("</html>");
        return msg.toString();
    }

    private String forbiddenMsg() {
        StringBuffer msg = new StringBuffer();
        msg.append("<html>");
        msg.append("<head><title>403 Forbidden</title></head>");
        msg.append("<body><h1>403 Forbidden</h1>");
        msg.append("<p>Sorry, you have no right to do this operation, please contact the administrator.</p></body>");
        msg.append("</html>");
        return msg.toString();
    }

    private void response404() throws IOException {
        String resourceFile = "./resources/customized404.html";
        File resourcePath = new File(resourceFile);
        out.write(header(resourceFile, resourcePath.length(), "404 not found").getBytes());
        BufferedInputStream fileIn = new BufferedInputStream(new FileInputStream(resourcePath));
        byte[] buffer = new byte[256];
        int nbRead;
        while((nbRead = fileIn.read(buffer)) != -1) {
            out.write(buffer, 0, nbRead);
        }
        fileIn.close();
        out.flush();
    }

    public void httpGET(String resourceFile) throws IOException {
        if (resourceFile.equals("/ ") || resourceFile.equals("/")) {
            resourceFile = "/index.html";
        }
        resourceFile = "./resources" + resourceFile;
        File resourcePath = new File(resourceFile);
        if (resourcePath.exists() && resourcePath.isFile()) {
            out.write(header(resourceFile, resourcePath.length(), "200 OK").getBytes());
        } else if (!resourcePath.exists()){
            response404();
            return;
        } else {
            // 500
            String error500 = internalErrorMsg();
            out.write(header("", error500.getBytes().length, "500 internal error").getBytes());
            out.write(error500.getBytes());
            out.flush();
            return;
        }
        BufferedInputStream fileIn = new BufferedInputStream(new FileInputStream(resourcePath));
        byte[] buffer = new byte[256];
        int nbRead;
        while((nbRead = fileIn.read(buffer)) != -1) {
            out.write(buffer, 0, nbRead);
        }
        fileIn.close();
        out.flush();
    }

    public void httpDELETE(String resourceFile) throws IOException {
        resourceFile = "./resources/" + resourceFile;
        File resourcePath = new File(resourceFile);
        System.out.println(resourceFile);
        if (resourcePath.exists() && resourcePath.isFile()) {
            if (resourceFile.contains("index") || resourceFile.contains("404") || resourceFile.contains("demo")) {
                // 403
                String error403 = forbiddenMsg();
                out.write(header("", error403.getBytes().length, "403 Forbidden").getBytes());
                out.write(error403.getBytes());
                out.flush();
                return;
            }
            if (resourcePath.delete()) {
                // 200
                System.out.println(resourceFile + " deleted");
                httpGET("/");
                return;
            } else {
                // 202
            }
        } else if (!resourcePath.exists()){
            // 404
            response404();
            return;
        } else {
            // 500
            String error500 = internalErrorMsg();
            out.write(header("", error500.getBytes().length, "500 internal error").getBytes());
            out.write(error500.getBytes());
            out.flush();
            return;
        }
    }
}
