package com.estsoft.fcmpushexample;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Added class for FCM Push
 *
 * "FirebaseMessagingService" class for <service> of manifest
 *
 * This class is used to make PUSH-ALARM to user.
 * Message written through API is include in "message" and sended.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "★★★FirebaseMsgService";

    //★★★★★★★★★★★★★★★★★★★★★★★★★★★★

    // FCM 수신시 진동 ON / OFF 설정하는 변수
    // 디폴트는 on 이다.
    String vibONOFF = "on";

    // 메인액티비티에서 보내는 intent 들어온다.
    @Override
    protected Intent zzae(Intent intent) {

        if ( intent.getStringExtra("vibONOFF") != null ) {

            vibONOFF = intent.getStringExtra("vibONOFF");

        }

        System.out.println("★★★★★★★★★★★★★★★★★★★★zzae : " + intent.getStringExtra("vibONOFF"));
        System.out.println("★★★★★★★★★★★★★★★★★★★★vibONOFF : " + vibONOFF);

        return super.zzae(intent);
    }
    //★★★★★★★★★★★★★★★★★★★★★★★★★★★★


    // START receive_message
    // onMessageReceived Func is callback function for Foreground
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Case 1. JSON type "data"
        sendNotification(remoteMessage);
        // Case 2. JSON type "notification"
        /**
         * remoteMessage.getNotification().getTitle();
         * remoteMessage.getNotification().getBody();
         * remoteMessage.getNotification().getIcon();
         **/

        //삭제할것★★★★★★★★★★★★★★★★★★★★★★★★
        System.out.println("--------------onMessageReceived 들어옴");
        System.out.println(remoteMessage.getData());
        //★★★★★★★★★★★★★★★★★★★★★★★★★★★★
    }

    private NotificationCompat.Builder sendNotification(RemoteMessage remoteMessage) {

        String message = remoteMessage.getData().get("body");
        String imgURL = remoteMessage.getData().get("imgURL");

        // Bitmap image for FCM Push
        Bitmap img = null;

        if(imgURL != "") {
            try {
                URL url = new URL(imgURL);
                URLConnection conn = url.openConnection();
                conn.connect();

                // convert URL image to Bitmap image
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                img = BitmapFactory.decodeStream(bis);

            } catch (Exception e) {

            }
        }

        // Setting activity which is convert when FCM Push touched
        Intent intent = new Intent(this, FCMActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                // FCM Push icon
                .setSmallIcon(R.mipmap.icon)
                // FCM Push Title
                .setContentTitle("mBlockChain")
                // FCM Push Text
                .setContentText("아래로 당겨 주세요▼")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)

                /**
                 *  Select "Big Text Style" or "BIg Image and Text Style"
                 *
                 * // Big Text Style
                 * .setStyle(new NotificationCompat.BigTextStyle()
                 * .setBigContentTitle("mBlockChain 자세히보기")
                 * .bigText(message))
                 */

                // Big Image and Text Style
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .setBigContentTitle("mBlockChain 이미지 보기")
                        .bigPicture(img)
                        .setSummaryText(message))
                .setContentIntent(pendingIntent);
        //vibONOFF 값에 따라 FCM 수신시 진동 ON/OFF 결정
        if (vibONOFF.equals("off"))
        {
            // 메세지 수신시 진동 ON/OFF 설정
            notificationBuilder.setVibrate(new long[] {0}); //진동 OFF 설정
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

        return notificationBuilder;
    }
}
