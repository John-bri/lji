package au.com.letsjoinin.android.utils;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import au.com.letsjoinin.android.MainActivity;
import au.com.letsjoinin.android.R;
import au.com.letsjoinin.android.UI.activity.ChatActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * NOTE: There can only be one service in each app that receives FCM messages. If multiple
 * are declared in the Manifest then the first one will be chosen.
 *
 * In order to make this Java sample functional, you must remove the following from the Kotlin messaging
 * service in the AndroidManifest.xml:
 *
 * <intent-filter>
 *   <action android:name="com.google.firebase.MESSAGING_EVENT" />
 * </intent-filter>
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]


    // [START on_new_token]

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
    private void scheduleJob() {
        // [START dispatch_job]
//        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class)
//                .build();
//        WorkManager.getInstance().beginWith(work).enqueue();
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }
    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }
    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        PreferenceManager mPrefMgr = PreferenceManager.getInstance();
        Intent intent = new Intent(this, ChatActivity.class);
        mPrefMgr.setBoolean(AppConstant.FromPushNotification, true);
        intent.putExtra("isChannel", false);
        intent.putExtra("ProgramDataID", "ad13f232-5d6d-452e-adc9-93b079c1576f");
        intent.putExtra("ContentType", "TOPIC");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        Uri.parse("android.resource://"
//                + getApplicationContext().getPackageName() + "/" + R.raw.siren);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.lji_launcher)
                        .setContentTitle("LetsJoinIn")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.background);
        if (bitmap != null) {
            notificationBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle("LetsJoinIn")
                    .setContentText(messageBody)
                    .setSmallIcon(R.mipmap.lji_launcher)
                    .setDefaults(NotificationCompat.DEFAULT_SOUND)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigPictureStyle()
                            .bigPicture(bitmap).setSummaryText(messageBody));
        }
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",android.app.NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }



    /* public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.




    Map<String, String> stringMap = remoteMessage.getData();
    RemoteMessage.Notification messageNotification = remoteMessage.getNotification();
    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
            R.drawable.list_1);
    bitmap = null;
    String message = stringMap.get(0);
    String title = "Lets Join In.";
    String ReplyForType = "";
    String GroupTitle = "";
    String GroupTitleImageURL = "";
    String GroupID = "";
    long GroupSK = 0;
    long ReplyForTypeSK = 0;
        for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
        String key = entry.getKey();
        String value = entry.getValue();
        if(key.equalsIgnoreCase("ReplyForTypeSK")) {
            ReplyForTypeSK = Long.parseLong(value);
        }
        if(key.equalsIgnoreCase("ReplyForType")) {
            ReplyForType = value;
        }
        if(key.equalsIgnoreCase("body")) {
            message = value;
        }
        if(key.equalsIgnoreCase("GroupTitle")) {
            GroupTitle = value;
        }
        if(key.equalsIgnoreCase("GroupTitleImageURL")) {
            GroupTitleImageURL = value;
        }
        if(key.equalsIgnoreCase("GroupID")) {
            GroupID = value;
        }
        if(key.equalsIgnoreCase("GroupSK")) {
            GroupSK = Long.parseLong(value);
        }
        title = "Lets Join In";
        Log.w(TAG, "key, " + key + " value " + value);
    }

    mPrefMgr = PreferenceManager.getInstance();
        if(ReplyForTypeSK > 0 && !TextUtils.isEmpty(ReplyForType)) {
        int icon = R.mipmap.lji_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager =
                (NotificationManager)
                        getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                    Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/" + R.raw.notification_mp3);
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            NotificationChannel mChannel = new NotificationChannel("default", "my_channel_01", NotificationManager.IMPORTANCE_HIGH);
//                    mChannel.setSound(sound, attributes); // This is IMPORTANT
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(mChannel);
        }
        Intent notificationIntent = new Intent(this, HomePage.class);
        notificationIntent.putExtra(PNUserGroupForSK, ReplyForTypeSK);
        notificationIntent.putExtra(PNUserGroupFor, ReplyForType);
        notificationIntent.putExtra(PNGroupName, GroupTitle);
        notificationIntent.putExtra(PNimagePath, GroupTitleImageURL);
        notificationIntent.putExtra(PNIdleTime, false);
        notificationIntent.putExtra(PNGroupID, GroupID);
        notificationIntent.putExtra(FromPN, true);
        int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, uniqueInt, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setSmallIcon(icon)
                .setWhen(when)
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);
        if (bitmap != null) {
            notification = new NotificationCompat.Builder(this)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(icon)
                    .setWhen(when)
                    .setDefaults(NotificationCompat.DEFAULT_SOUND)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigPictureStyle()
                            .bigPicture(bitmap).setSummaryText(message));
        }
        notificationManager.notify(01, notification.build());
    }
}*/
}