package net.awesomeapps.httpserverapp.microhttp;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by daniel on 2/23/17.
 */

public class ClientSocket {

    private static final String TAG = "ClientSocket";

    private String requestMethod;
    private String requestUri;
    private Map<String, String> headers;
    private byte[] requestBody;
    private Socket socket;
    private Map<String, MicroHttpHandler> contexts;

    public ClientSocket(Map<String, MicroHttpHandler> contexts) {
        requestMethod = "";
        requestUri = null;
        headers = new HashMap<>();
        this.contexts = contexts;
    }

    public void handle(Socket socket) throws IOException {
        this.socket = socket;

        BufferedReader reader = null;
        PrintStream output = null;

        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Parse out the request line, headers, and anything in the body
            parseRequest(reader);

            // Pass to any attached handler for the given uri
            if (contexts.containsKey(requestUri)) {
                MicroHttpExchange exchange = new MicroHttpExchange(this);

                // TODO pass necessary stuff to the exchange
                // - post body
                // - request headers
                // - request method
                // - request uri
                // - output stream
                // - input stream
                // - remote addr/port

                contexts.get(requestUri).handle(exchange);
            }
            // If no context exist for the uri, we'll show a 404 page
            else {
                // Output stream that we send the response to
                output = new PrintStream(socket.getOutputStream());

                // Send them a 404
                writeError(output);
            }
        }
        finally {
            if (output != null) {
                output.close();
            }

            if (reader != null) {
                reader.close();
            }
        }
    }

    private void parseRequest(BufferedReader reader) throws IOException {
        // Parse the first line, this will be the request line
        // which contains: request method, request uri, http version
        parseRequestLine(reader.readLine());

        String line;

        while (!TextUtils.isEmpty(line = reader.readLine())) {
            // The rest of the lines, they should be the headers
            parseHeaderLine(line);
        }

        // Parse out the request body
        // If the request method was POST, we'll get the body of the request
        // This is really a convenience for the developer so that they don't
        // need to get the post body themselves
        if (requestMethod.equals("POST") && headers.containsKey("Content-Length")) {
            parseRequestBody(reader);
        }

//            Log.d(TAG, "request method: " + requestMethod);
//            Log.d(TAG, "request uri: " + requestUri);
//            Log.d(TAG, "headers" + headers);
    }

    private void parseRequestBody(BufferedReader reader) throws IOException {
        int contentLen = 0;

        try {
            contentLen = Integer.parseInt(headers.get("Content-Length"));
        }
        catch (NumberFormatException e) {
            // Don't care out the exception
            // The content length would have been parsable to int
            // if it was correctly set by the client and not bogus
        }

        if (contentLen > 0) {
            StringBuilder sb = new StringBuilder();

            while (contentLen > 0) {
                contentLen--;

                sb.append((char)reader.read());
            }

            requestBody = sb.toString().getBytes();
        }
    }

    private void parseRequestLine(String str) {
        //Log.d(TAG, "parseRequestLine");

        if (str == null) {
            return;
        }

        // TODO need better way to determine and set the request method
        // maybe use map of known methods and match it up

        // Figure out the request method
        if (str.startsWith("GET")) {
            setRequestMethod("GET");
        }
        else if (str.startsWith("POST")) {
            setRequestMethod("POST");
        }

        // Get the request uri
        // Get the start of the uri string
        int start = str.indexOf('/');

        // Get up to the space after the uri string
        int end = str.indexOf(' ', start);

        setRequestUri(str.substring(start, end));
    }

    private void parseHeaderLine(String str) {
        //Log.d(TAG, "parseHeaderLine");

        if (str == null) {
            return;
        }

        // Header line should be key/value delimited by a colon
        int delimIndex = str.indexOf(':');

        // If a colon exists, we'll assume everything up to the colon is the key
        // and everything after it is the value
        if (delimIndex != -1) {
            headers.put(
                    str.substring(0, delimIndex),
                    str.substring(delimIndex + 1, str.length()).trim()
            );
        }
    }

    private void writeError(PrintStream output) throws IOException {
        output.println("HTTP/1.0 404 Not Found");
        output.println();
        output.write("<h1>404 Not Found</h1>".getBytes());
        output.flush();
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getRequestBody() {
        return requestBody;
    }

    public Socket getSocket() {
        return socket;
    }

    protected void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    protected void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

}
