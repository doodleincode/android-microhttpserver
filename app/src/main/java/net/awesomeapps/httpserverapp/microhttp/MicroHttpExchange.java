package net.awesomeapps.httpserverapp.microhttp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * Created by daniel on 2/23/17.
 */

public class MicroHttpExchange {

    private ClientSocket clientSocket;

    public MicroHttpExchange(ClientSocket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public String getRequestMethod() {
        return clientSocket.getRequestMethod();
    }

    public String getRequestUri() {
        return clientSocket.getRequestUri();
    }

    public Map<String, String> getHeaders() {
        return clientSocket.getHeaders();
    }

    public byte[] getRequestBody() {
        return clientSocket.getRequestBody();
    }

    public OutputStream getResponseBody() throws IOException {
        return clientSocket.getSocket().getOutputStream();
    }

}
