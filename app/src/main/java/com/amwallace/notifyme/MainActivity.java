package com.amwallace.notifyme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button notifyBtn;
    private Button updateBtn;
    private Button cancelBtn;
    private NotificationManager notificationManager;
    private NotificationReceiver notificationReceiver = new NotificationReceiver();
    //constant for notification channel ID,
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    //constant for notification ID
    private static final int NOTIFICATION_ID = 0;
    //constant for update notification action for broadcast
    private static final String ACTION_UPDATE_NOTIFICATION =
            "com.amwallace.notifyme.ACTION_UPDATE_NOTIFICATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set up buttons
        notifyBtn = (Button) findViewById(R.id.notifyMeBtn);
        updateBtn = (Button) findViewById(R.id.updateMeBtn);
        cancelBtn = (Button) findViewById(R.id.cancelMeBtn);

        //set on click listener for notify button
        notifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call send notification method on click
                sendNotification();
            }
        });
        //set on click listener for update button
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update notification
                updateNotification();
            }
        });
        //set on click listener for cancel button
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cancel notification
                cancelNotification();
            }
        });
        //call create notification channel method
        createNotificationChannel();
        //set button states
        setNotificationButtonState(true, false, false);
        //register broadcast receiver
        registerReceiver(notificationReceiver, new IntentFilter(ACTION_UPDATE_NOTIFICATION));
    }

    //override onDestroy
    @Override
    protected void onDestroy() {
        //unregister broadcast receiver
        unregisterReceiver(notificationReceiver);
        super.onDestroy();
    }

    //send notification method
    public void sendNotification() {
        //new intent w/ update action
        Intent updateAction = new Intent(ACTION_UPDATE_NOTIFICATION);
        //get pendingIntent, w/ one use flag
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(this,
                NOTIFICATION_ID, updateAction, PendingIntent.FLAG_ONE_SHOT);
        //get notification builder
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        //add update action to builder
        notifyBuilder.addAction(R.drawable.ic_update,
                "Update Notification", updatePendingIntent);
        //perform notification w/ notification ID and builder
        notificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());
        //set button states
        setNotificationButtonState(false, true, true);
    }

    //update notification method
    public void updateNotification() {
        //convert drawable to bitmap
        Bitmap macImage = BitmapFactory.decodeResource(getResources(), R.drawable.mac_mascot);
        //get notification builder
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        //update style of notification, set image/title
        notifyBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                .bigPicture(macImage).setBigContentTitle("Notification Update"));
        //build notification/perform notification w/ notification ID
        notificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());
        //set button states
        setNotificationButtonState(false, false, true);
    }

    //cancel notification method
    public void cancelNotification() {
        //cancel notification w/ notification manager
        notificationManager.cancel(NOTIFICATION_ID);
        //set button states
        setNotificationButtonState(true, false, false);
    }

    //toggle button states method
    void setNotificationButtonState(Boolean isNotifyEnabled, Boolean isUpdateEnabled,
                                    Boolean isCancelEnabled) {
        //set notify button state
        notifyBtn.setEnabled(isNotifyEnabled);
        //set update button state
        updateBtn.setEnabled(isUpdateEnabled);
        //set cancel button state
        cancelBtn.setEnabled(isCancelEnabled);
    }

    //create notification channel method
    public void createNotificationChannel() {
        //instantiate notification manager
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //check device API version, create notification channel if >= 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //create notification channel w/ primary channel id constant, high importance
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    "Mascot Notification", NotificationManager.IMPORTANCE_HIGH);
            //configure notification channel settings - light, vibrate, description
            notificationChannel.enableLights(true);
            //red light
            notificationChannel.setLightColor(Color.RED);
            //vibration
            notificationChannel.enableVibration(true);
            //description
            notificationChannel.setDescription("Notification from Mascot");
            //create channel with manager
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    //get notification builder method
    private NotificationCompat.Builder getNotificationBuilder() {
        //create intent to launch main activity
        Intent notificationIntent = new Intent(this, MainActivity.class);
        //get pending intent with notification ID as request code
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //instantiate builder with primary channel id, set title/text/icon/pendingIntent
        //set priority/notification config for devices w/ Android 7.1 and lower
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(
                this, PRIMARY_CHANNEL_ID).setContentTitle("You've been notified!")
                .setContentText("A nu cheeki breeki iv damke!")
                .setSmallIcon(R.drawable.ic_android)
                .setContentIntent(notifyPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);
        return notifyBuilder;
    }

    //broadcast receiver inner class
    public class NotificationReceiver extends BroadcastReceiver {
        public NotificationReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            //update notification on receive
            updateNotification();
        }
    }
}
