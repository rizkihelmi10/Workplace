package com.smartlab.wpslidingmenu;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

//import me.leolin.shortcutbadger.ShortcutBadger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static android.content.ContentValues.TAG;

/**
 * Created by Syuk on 12/8/2018.
 */

//class extending FirebaseMessagingService
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    int countMsg = 0;
    JSONObject json = null;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        if (remoteMessage.getData().size() > 0) {
            //handle the data message here
            Log.d("FCM Message data payload: ", "" + remoteMessage.getData());

        }


        //getting the title and the body
        //String title = remoteMessage.getNotification().getTitle();
       // String body = remoteMessage.getNotification().getBody();

        //firebase badge
        //countMsg = countMsg++;
        //ShortcutBadger.applyCount(MyFirebaseMessagingService.this, 1);
       // Log.d("FCM Message title payload: ", "" + title);
        //Log.d("FCM Message body payload: ", "" + body);*/
    }


    public void sendNotification(){
        int NOTIFICATIONID = 1234;
        // Uri sound =  RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.iphone);
        Intent intent = new Intent(this, MainActivity.class);
       // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            Intent buttonIntent = new Intent(getBaseContext(), MainActivity.class);
            PendingIntent dismissIntent = PendingIntent.getBroadcast(getBaseContext(), 0, buttonIntent, 0);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            String CHANNEL_ID = BuildConfig.APPLICATION_ID.concat("_notification_id");
            String CHANNEL_NAME = BuildConfig.APPLICATION_ID.concat("_notification_name");
            assert notificationManager != null;

            NotificationChannel mChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
            if (mChannel == null) {
                mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                mChannel.setSound(sound, audioAttributes);
                notificationManager.createNotificationChannel(mChannel);
            }

            // The PendingIntent to launch activity.
            PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
                    intent, 0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);

            builder.setSmallIcon(R.drawable.wpicon)
                    .setContentTitle(("Incoming Call..."))
                    .setContentText("Najmi is calling")
                    .setDefaults(0)
                    .addAction(R.drawable.answer, "Answer",
                            activityPendingIntent)
                    .addAction(R.drawable.reject, "Reject",
                            dismissIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_CALL)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setSound(sound)
                    .setOngoing(true);


            android.app.Notification notification = builder.build();
            notificationManager.notify(NOTIFICATIONID, notification);
            callRing(1);
        }
    }

    public void sendNotification2(){
        int NOTIFICATIONID = 1234;
        // Uri sound =  RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.iphone);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            Intent buttonIntent = new Intent(getBaseContext(), MainActivity.class);
            PendingIntent dismissIntent = PendingIntent.getBroadcast(getBaseContext(), 0, buttonIntent, 0);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            String CHANNEL_ID = BuildConfig.APPLICATION_ID.concat("_notification_id");
            String CHANNEL_NAME = BuildConfig.APPLICATION_ID.concat("_notification_name");
            assert notificationManager != null;

            NotificationChannel mChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
            if (mChannel == null) {
                mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                mChannel.setSound(sound, audioAttributes);
                notificationManager.createNotificationChannel(mChannel);
            }

            // The PendingIntent to launch activity.
            PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
                    intent, 0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);

            builder.setSmallIcon(R.drawable.wpicon)
                    .setContentTitle(("Incoming Call..."))
                    .setContentText("Najmi is calling")
                    .setDefaults(0)
                    .addAction(R.drawable.answer, "Answer",
                            activityPendingIntent)
                    .addAction(R.drawable.reject, "Reject",
                            dismissIntent)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setCategory(NotificationCompat.CATEGORY_CALL)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setSound(sound)
                    .setOngoing(true);
            android.app.Notification notification = builder.build();
            notificationManager.notify(NOTIFICATIONID, notification);

        }
    }

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        try {
            JSONObject data = json.getJSONObject("data");

            String notiID = data.getString("ID");
            String type = data.getString("TYPE");
            String isRing= data.getString("RINGON");
            String messagedetail = data.getString("MESSAGEDETAIL");
            String delay = data.getString("DELAY");
            String urlPath = data.getString("URL");
            String initiator = data.getString("INITIATOR");
            String company = data.getString("COMPANY");
            Log.i("FCM Type", type);
           /* JSONObject payload = data.getJSONObject("payload");
            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "isBackground: " + isBackground);
            Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);*/



           if (type.equalsIgnoreCase("CALL")){
               Log.i("FCM Call", type);
           }



        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    public void callRing(int ring){

        String ringtoneuri = Environment.getExternalStorageDirectory().getAbsolutePath() + "/media/ringtone";
        File file1 = new File(ringtoneuri);
        file1.mkdirs();
        File newSoundFile = new File(android.os.Environment.getExternalStorageDirectory(), "/media/ringtone/ring.mp3");

        Uri path = Uri.parse("android.resource://"+ "com.smartlab.slidingmenu/" + R.raw.iphone);
        MediaPlayer mediaPlayer = MediaPlayer.create(this,R.raw.iphone);

        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        switch (am.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                Log.i("MyApp","Silent mode");

                am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                mediaPlayer.setVolume(0,0);
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                Log.i("MyApp","Vibrate mode");

                am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                mediaPlayer.setVolume(0,0);
                break;
            case AudioManager.RINGER_MODE_NORMAL:
                Log.i("MyApp","Normal mode");

                am.setStreamVolume(AudioManager.STREAM_MUSIC, 90, 0);
                mediaPlayer.setVolume(90,90);
                break;
        }


        RingtoneManager.setActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_RINGTONE,path);
        //   Uri defaultRintoneUri = RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_RINGTONE);
        Ringtone defaultRingtone = RingtoneManager.getRingtone(getApplicationContext() ,path);

        // Get instance of Vibrator from current Context
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 100, 1000};
        if(ring == 1){
            //defaultRingtone.play();
            mediaPlayer.start();
            if (Build.VERSION.SDK_INT >= 26) {
                //v.vibrate(VibrationEffect.createOneShot(350, VibrationEffect.DEFAULT_AMPLITUDE);
                v.vibrate(pattern,0);
            } else {
                v.vibrate(350);
            }
        }
        else{
            //defaultRingtone.stop();
            mediaPlayer.stop();
            v.cancel();
        }


    }




}
