package net.awesomeapps.httpserverapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import net.awesomeapps.httpserverapp.microhttp.MicroHttpExchange;
import net.awesomeapps.httpserverapp.microhttp.MicroHttpHandler;
import net.awesomeapps.httpserverapp.microhttp.MicroHttpServer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MicroHttpServer httpServer = MicroHttpServer.create(new InetSocketAddress(12345));

        httpServer.createContext("/", new MicroHttpHandler() {
            @Override
            public void handle(MicroHttpExchange exchange) {
                Log.d(TAG, "Handler for root uri");
                Log.d(TAG, "request method: " + exchange.getRequestMethod());
                Log.d(TAG, "request uri: " + exchange.getRequestUri());
                Log.d(TAG, "request headers: " + exchange.getHeaders());

                if (exchange.getRequestMethod().equals("POST")) {
                    Log.d(TAG, "post body: " + new String(exchange.getRequestBody()));
                }

                PrintStream output = null;

                try {
                    output = new PrintStream(exchange.getResponseBody());
                    output.println("HTTP/1.0 200 OK");
                    output.println();
                    output.write("<h1>Hello World!</h1>".getBytes());
                    output.flush();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    if (output != null) {
                        output.close();
                    }
                }
            }
        });

        httpServer.start();
    }

    public void onClickSendPost(View view) {
        Log.d(TAG, "onClickSendPost");

        new Thread(new Runnable() {
            @Override
            public void run() {

                String url = "http://127.0.0.1:12345";
                URL obj = null;
                try {
                    obj = new URL(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpURLConnection con = null;
                try {
                    con = (HttpURLConnection) obj.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //add reuqest header
                try {
                    con.setRequestMethod("POST");
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
                con.setRequestProperty("User-Agent", "Mozilla/5.0");
                con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

                String urlParameters = "This was the post body!";

                // Send post request
                con.setDoOutput(true);
                DataOutputStream wr = null;
                try {
                    wr = new DataOutputStream(con.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    wr.writeBytes(urlParameters);
                    wr.flush();
                    wr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                int responseCode = 0;
                try {
                    responseCode = con.getResponseCode();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("\nSending 'POST' request to URL : " + url);
                System.out.println("Post parameters : " + urlParameters);
                System.out.println("Response Code : " + responseCode);

            }
        }).start();

    }
}
