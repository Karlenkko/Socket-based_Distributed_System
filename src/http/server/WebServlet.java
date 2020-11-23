package http.server;

import java.io.*;
import java.util.HashMap;

public class WebServlet {
    private BufferedOutputStream out; // the output stream which the server use to give responses
    private HashMap<String, String> headers; // the HashMap that notes all headers in a request

    /**
     * Constructor of the Servlet, which handles requests in a relatively primitive way,
     * it might be better to extend the HttpServlet class
     * @param out the output stream which the server use to give responses
     */
    WebServlet(BufferedOutputStream out) {
        this.out = out;
        headers = new HashMap<>();
    }

    /**
     * notes all header information in one request into the HashMap, normally only
     * the Content-Length might be used in this server, but for possible extension,
     * all headers are marked
     * @param oneLine one line of the request
     */
    public void fillHeaders(String oneLine) {
        String[] header = oneLine.split(": ");
        if (header.length == 2) {
            headers.put(header[0], header[1]);
        }
    }

    /**
     * indicates the Content-Length of the body of one request,
     * usually used for POST and PUT requests for writing files.
     * If there isn't such a header noted in the HashMap of headers,
     * this function will return 0 as a default value.
     * @return the Content-Length of a request body if there is one
     */
    public int getContentLength(){
        return Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
    }

    /**
     * reads a requested local existing file name from the request String that
     * is in the resources folder.
     * returns empty String as default value, which will later give the index page.
     * @param stringBuffer the request text
     * @return the file name which can be found in the resources folder
     */
    public String getResourceFileName(StringBuffer stringBuffer) {
        String request = stringBuffer.toString();
        String[] all = request.split(" ", 3);
        if (all[1].equals("/favicon.ico")) {
            return "";
        }
        return all[1];
    }

    /**
     * reads a desired file name from a POST, DELETE or PUT request,
     * if the desired file name already exists in the local folder, the new file will replace the
     * local existing one if the user is allowed to do this
     * @param stringBuffer the request text
     * @return the desired file name to be stored in the server
     */
    public String getLocalResourceFileName(StringBuffer stringBuffer) {
        String request = stringBuffer.toString();
        String[] all = request.split(" ", 3);
        if (all[1].contains("=")) {
            String[] query = all[1].split("=");
            return query[1];
        } else {
            return "";
        }
    }

    /**
     * reads the request type from the request text, currently support
     * HRAD, GET, POST, DELETE, PUT,
     * if the request is of a type that is not supported,
     * the default value is an empty request which will later be ignored.
     * @param stringBuffer the request text
     * @return the request type
     */
    public static String getRequestType(StringBuffer stringBuffer) {
        String request = stringBuffer.toString();
        if (request.contains("GET")) {
            return "GET";
        } else if (request.contains("POST")) {
            return "POST";
        } else if (request.contains("PUT")) {
            return "PUT";
        } else if (request.contains("DELETE")) {
            return "DELETE";
        } else if (request.contains("HEAD")) {
            return "HEAD";
        }
        return "";
    }

    /**
     * writes the header text for a response depending on the given parameters,
     * currently support limited file type:
     * text/html, video/mp4, image/png, image/jpg, audio/mp3, video/avi, application/pdf,
     * the default file type is text/html
     * @param resourceURI the resource URI name
     * @param length the size of the file
     * @param status the status code of the response
     * @return a text of the header
     */
    public static String header(String resourceURI, long length, String status) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("HTTP/1.0 ").append(status).append("\r\n");
        if (resourceURI.endsWith(".html") || resourceURI.endsWith(".htm") || resourceURI.endsWith(".txt")) {
            stringBuffer.append("Content-Type: text/html\r\n");
        } else if (resourceURI.endsWith(".mp4")) {
            stringBuffer.append("Content-Type: video/mp4\r\n");
        } else if (resourceURI.endsWith(".png")) {
            stringBuffer.append("Content-Type: image/png\r\n");
        } else if (resourceURI.endsWith(".jpeg") || resourceURI.endsWith(".jpg")) {
            stringBuffer.append("Content-Type: image/jpg\r\n");
        } else if (resourceURI.endsWith(".mp3")) {
            stringBuffer.append("Content-Type: audio/mp3\r\n");
        } else if (resourceURI.endsWith(".avi")) {
            stringBuffer.append("Content-Type: video/avi\r\n");
        } else if (resourceURI.endsWith(".pdf")) {
            stringBuffer.append("Content-Type: application/pdf\r\n");
        } else {
            // default
            stringBuffer.append("Content-Type: text/html\r\n");
            stringBuffer.append("Server: Bot\r\n");
            stringBuffer.append("\r\n");
            return stringBuffer.toString();
        }
        stringBuffer.append("Content-Length: ").append(length).append("\r\n");
        stringBuffer.append("Server: Bot\r\n");
        stringBuffer.append("\r\n");
        return stringBuffer.toString();

    }

    /**
     * writes the body text for a 500 response
     * @return the 500 body text
     */
    public static String internalErrorMsg() {
        return "<html>" +
                "<head><title>500 Internal Error</title></head>" +
                "<body><h1>500 Internal Error</h1>" +
                "<p>There is an internal error in the server, please contact the administrator.</p></body>" +
                "</html>";
    }

    /**
     * writes the body text for a 403 response
     * @return the 403 body text
     */
    private String forbiddenMsg() {
        return "<html>" +
                "<head><title>403 Forbidden</title></head>" +
                "<body><h1>403 Forbidden</h1>" +
                "<p>Sorry, you have no right to do this operation, please contact the administrator.</p></body>" +
                "</html>";
    }

    /**
     * returns a 404 not found response using a customized 404 page
     * @throws IOException exception for IO errors when writing output stream
     */
    private void response404() throws IOException {
        String resourceFile = "./resources/customized404.html";
        File resourcePath = new File(resourceFile);
        out.write(header(resourceFile, resourcePath.length(), "404 not found").getBytes());
        outputFile(resourcePath);
    }

    /**
     * return a 403 forbidden response if the request operation concerns a forbidden modification
     * of an existing file,
     * normally it is depends on whether the local file is writable,
     * but critical resources like index.html are double protected by URI indentification
     * @param resourceURI the resource URI file name
     * @return true if the operation is forbidden,
     *         false if the operation is allowed
     * @throws IOException exception for IO errors when writing output stream
     */
    private boolean response403(String resourceURI) throws IOException {
        File resourcePath = new File("./resource/" + resourceURI);
        if (resourceURI.contains("index.html")
                || resourceURI.contains("404.html")
                || resourceURI.contains("demo")
                || ! resourcePath.canWrite()) {
            // 403
            String error403 = forbiddenMsg();
            out.write(header("", error403.getBytes().length, "403 Forbidden").getBytes());
            out.write(error403.getBytes());
            out.flush();
            return true;
        }
        return false;
    }

    /**
     * return a 500 internal error response if there is an unexpected error
     * @throws IOException exception for IO errors when writing output stream
     */
    private void response500() throws IOException {
        String error500 = internalErrorMsg();
        out.write(header("", error500.getBytes().length, "500 internal error").getBytes());
        out.write(error500.getBytes());
        out.flush();
    }

    /**
     * writes the binary code of the resource file into the output stream
     * @param resourceFile the resource file
     * @throws IOException exception for IO errors when writing output stream
     */
    private void outputFile(File resourceFile) throws IOException {
        BufferedInputStream fileIn = new BufferedInputStream(new FileInputStream(resourceFile));
        byte[] buffer = new byte[256];
        int nbRead;
        while((nbRead = fileIn.read(buffer)) != -1) {
            out.write(buffer, 0, nbRead);
        }
        fileIn.close();
        out.flush();
    }

    /**
     * the method that handles the GET requests, which can have a lot of functionalities,
     * currently have 200, 404, 500 responses.
     * @param resourceURI the desired resource file name
     * @throws IOException exception for IO errors when writing output stream
     */
    public void httpGET(String resourceURI) throws IOException {
        if (resourceURI.equals("/ ") || resourceURI.equals("/")) {
            resourceURI = "/index.html";
        }
        resourceURI = "./resources" + resourceURI;
        File resourceFile = new File(resourceURI);
        if (resourceFile.exists() && resourceFile.isFile()) {
            out.write(header(resourceURI, resourceFile.length(), "200 OK").getBytes());
        } else if (!resourceFile.exists()){
            response404();
            return;
        } else {
            // 500
            response500();
            return;
        }
        outputFile(resourceFile);
    }

    /**
     * the method that handles the DELETE requests, which deletes a local resource file
     * deleting critical files is forbidden.
     * currently have 200, 403, 404, 500 responses.
     * the 200 response will return the index page, which means that the delete was successful
     * @param resourceURI the desired resource file name
     * @throws IOException exception for IO errors when writing output stream
     */
    public void httpDELETE(String resourceURI) throws IOException {
        resourceURI = "./resources/" + resourceURI;
        File resourceFile = new File(resourceURI);
        if (resourceFile.exists() && resourceFile.isFile()) {
            if (response403(resourceURI)) return;
            if (resourceFile.delete()) {
                // 200
                httpGET("/");
            } else {
                // 500
                String error500 = internalErrorMsg();
                out.write(header("", error500.getBytes().length, "500 internal error").getBytes());
                out.write(error500.getBytes());
                out.flush();
            }
        } else if (!resourceFile.exists()){
            // 404
            response404();
        } else {
            // 500
            response500();
        }
    }

    /**
     * the method that handles the PUT requests, which replaces or creates a file.
     * replacing critical files is forbidden.
     * the put requests are mostly dedicated for uploading resources,
     * currently have 200, 201, 403, 500 responses.
     * the 200, 201 responses will return the result of the post request
     * @param body the binary body code of a file from the request
     * @param resourceURI the desired resource file name
     * @throws IOException exception for IO errors when writing output stream
     */
    public void httpPUT(byte[] body, String resourceURI) throws IOException {
        File resourceFile = new File("./resources/" + resourceURI);
        boolean existed = resourceFile.exists();
        // 403
        if (response403(resourceURI)) return;

        FileOutputStream fos = new FileOutputStream(resourceFile, false);
        fos.write(body);
        fos.close();

        if (existed) {
            // 200
            httpGET("/" + resourceURI);
        } else if (resourceFile.isFile()) {
            // 201
            out.write(header(resourceURI, resourceFile.length(), "201 Created").getBytes());
            outputFile(resourceFile);
        } else {
            // 500
            response500();
        }
    }

    /**
     * the method that handles the POST requests, which appends or creates a file.
     * appending to critical files is forbidden.
     * though post requests can be really variable, here due to a lack of dynamic resources,
     * only post files are possible.
     * currently have 200, 201, 403, 500 responses.
     * the 200, 201 responses will return the result of the post request
     * @param body the binary body code of a file from the request
     * @param resourceURI the desired file name
     * @throws IOException
     */
    public void httpPOST(byte[] body, String resourceURI) throws IOException {
        File resourceFile = new File("./resources/" + resourceURI);
        boolean existed = resourceFile.exists();
        // 403
        if (response403(resourceURI)) return;

        FileOutputStream fos = new FileOutputStream(resourceFile, true);
        fos.write(body);
        fos.close();

        if (existed) {
            // 200
            httpGET("/" + resourceURI);
        } else if (resourceFile.isFile()) {
            // 201
            out.write(header(resourceURI, resourceFile.length(), "201 Created").getBytes());
            outputFile(resourceFile);
        } else {
            // 500
            response500();
        }
    }

    /**
     * the method that handles HEAD requests,
     * currently have 200, 404 responses.
     * @param resourceURI the requested file URI
     * @throws IOException exception of IO when writing the output stream
     */
    public void httpHEAD(String resourceURI) throws IOException {
        File resourceFile = new File(resourceURI);
        if(resourceFile.exists() && resourceFile.isFile()) {
            out.write(header(resourceURI, resourceFile.length(), "200 OK").getBytes());
            out.flush();
        } else {
            // 404
            response404();
        }
    }


}
