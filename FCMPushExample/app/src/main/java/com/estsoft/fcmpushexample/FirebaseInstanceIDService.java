package com.estsoft.fcmpushexample;

import android.util.Log;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Added class for FCM Push
 *
 * "FirebaseInstanceIDService" class for <service> of manifest
 *
 * This class is used to make token to each user
 * token is unique key value to send push message to specific user.
 */

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "★★★FirebaseMsgService";

    // START refresh_token
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + token);

        // save token which is made by FirebaseInstanceId in personal app server to manage something if you want
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("token", token)
                .build();

        //request
        Request request = new Request.Builder()
                // input MAC address of Ethernet
                .url("http://192.168.22.73/fcmphp/insertToken.php")
                .post(body)
                .build();

        try {
            client.newCall(request).execute();
         } catch (IOException e) {
         e.printStackTrace();
         } finally {
         }

    }
}
