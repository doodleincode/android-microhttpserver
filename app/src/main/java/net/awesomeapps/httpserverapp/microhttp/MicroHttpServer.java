package net.awesomeapps.httpserverapp.microhttp;

import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by daniel on 2/23/17.
 */

public class MicroHttpServer implements Runnable {

    private static final String TAG = "MicroHttpServer";
    private static final int DEFAULT_BACKLOG = 100;

    private InetSocketAddress addr;
    private int backlog;
    private boolean isRunning = false;
    private ServerSocket serverSocket;

    private Map<String, MicroHttpHandler> contexts;

    protected MicroHttpServer(InetSocketAddress addr, int backlog) {
        this.addr = addr;
        this.backlog = backlog;

        contexts = new HashMap<>();
    }

    public static MicroHttpServer create(InetSocketAddress addr) {
        return new MicroHttpServer(addr, DEFAULT_BACKLOG);
    }

    public static MicroHttpServer create(InetSocketAddress addr, int backlog) {
        return new MicroHttpServer(addr, backlog);
    }

    public void start() {
        isRunning = true;
        new Thread(this).start();
    }

    public void stop() {
        try {
            isRunning = false;

            if (serverSocket != null) {
                serverSocket.close();
                serverSocket = null;
            }

            Log.e(TAG, "Server shutting down...");
        }
        catch (IOException e) {
            Log.e(TAG, "Error closing the server socket.", e);
        }
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(addr.getPort(), backlog, addr.getAddress());

            while (isRunning) {
                Socket socket = serverSocket.accept();

                ClientSocket client = new ClientSocket(getContexts());
                client.handle(socket);

                socket.close();
            }
        }
        catch (SocketException e) {
            Log.e(TAG, "Server stopped.");
        }
        catch (IOException e) {
            Log.e(TAG, "Server error.", e);
        }
    }

    public void createContext(String context, MicroHttpHandler handler) {
        contexts.put(context, handler);
    }

    public Map<String, MicroHttpHandler> getContexts() {
        return contexts;
    }

}
