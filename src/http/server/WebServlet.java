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
        return "GET";
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
        }
        stringBuffer.append("Content-Length: " + length + "\r\n");
        stringBuffer.append("Server: Bot\r\n");
        stringBuffer.append("\r\n");
        return stringBuffer.toString();

    }

    public void httpGET(String resourceFile) throws IOException {
        System.out.println(resourceFile);
        if (resourceFile.equals("/ ") || resourceFile.equals("/")) {
            resourceFile = "/index.html";
        }
        resourceFile = "./resources" + resourceFile;
        File resourcePath = new File(resourceFile);
        System.out.println(resourceFile);
        if (resourcePath.exists() && resourcePath.isFile()) {
            out.write(header(resourceFile, resourcePath.length(), "200 OK").getBytes());
        } else {
            resourceFile = "./resources/customized404.html";
            resourcePath = new File(resourceFile);
            out.write(header(resourceFile, resourcePath.length(), "404 not found").getBytes());
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

    public void httpDELETE(String resourceFile) {

    }
}
