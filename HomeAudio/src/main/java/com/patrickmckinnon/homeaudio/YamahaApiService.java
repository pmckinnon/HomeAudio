package com.patrickmckinnon.homeaudio;

import android.app.IntentService;
import android.content.Intent;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by prm on 1/21/14.
 */
public class YamahaApiService extends IntentService {
    private static final Logger LOG = new Logger(YamahaApiService.class);
    private static final int TIMEOUT_MS_CONNECTION = 10000;
    private static final int TIMEOUT_MS_SOCKET = 100000;
    public static final String EXTRA_IP = "ip";
    public static final String EXTRA_XML = "xml";

    public YamahaApiService() {
        super("YamahaApiService");
        /*
        java.util.logging.Logger.getLogger("org.apache.http.wire").setLevel(java.util.logging.Level.FINEST);
        java.util.logging.Logger.getLogger("org.apache.http.headers").setLevel(java.util.logging.Level.FINEST);

        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "debug");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "debug");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.headers", "debug");
        */
    }

    @Override
    protected synchronized void onHandleIntent(Intent intent) {
        String ip = intent.getStringExtra(EXTRA_IP);
        String xml = intent.getStringExtra(EXTRA_XML);
        String uri = "http://" + ip + "/YamahaRemoteControl/ctrl";
        LOG.v("IP: " + ip);
        LOG.v("XML: " + xml);

        try {
            HttpParams httpParameters = new BasicHttpParams();
            // Set the timeout in milliseconds until a connection is established.
            // The default value is zero, that means the timeout is not used.
            HttpConnectionParams.setConnectionTimeout(httpParameters, TIMEOUT_MS_CONNECTION);
            // Set the default socket timeout (SO_TIMEOUT)
            // in milliseconds which is the timeout for waiting for data.
            HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT_MS_SOCKET);

            HttpClient client = new DefaultHttpClient(httpParameters);

            HttpPost request = new HttpPost(uri);
            request.addHeader("Content-Type", "text/xml");
            request.addHeader("Accept", "*/*");
            request.setEntity(new StringEntity(xml));

            HttpResponse response = client.execute(request);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            HttpEntity entity = response.getEntity();
            InputStream content = entity.getContent();
            InputStreamReader reader = new InputStreamReader(content);

            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null)
                builder.append(line);

            String responseString = builder.toString();
            LOG.d("HTTP Response(" + statusCode + "): " + responseString);

            if(statusCode == HttpStatus.SC_OK) {
            }
            else {
            }
        } catch(IOException e) {
            LOG.e("IOException: " + e);
        }
    }
}
