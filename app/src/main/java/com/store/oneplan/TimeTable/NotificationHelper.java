package com.store.oneplan.TimeTable;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.store.oneplan.R;

public class NotificationHelper extends ContextWrapper {

    public static final String channelID = "OnePlan";
    public static final String channelName = "Channel";
    public static final String title = "title";
    public static final String description = "description";

    private NotificationManager mManager;


    public NotificationHelper(Context base) {
        super(base);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels();

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createChannels()
    {
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription(description);
        channel.setName(title);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLightColor(Color.BLUE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager ()
    {
        if (mManager == null)
        {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return mManager;
    }

    public NotificationCompat.Builder getChannelNotification (String title, String description)
    {
        Intent intent = new Intent(NotificationHelper.this, AlertReceiver.class);
        Intent intentActivity = new Intent(NotificationHelper.this, time_table.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pIntent = PendingIntent.getActivity(this, 1, intentActivity, 0);

        return new NotificationCompat.Builder(getApplicationContext() ,channelID)
                .setContentTitle(title)
                .setContentText(description)
                .setSmallIcon(R.drawable.oneplanlogo)
                .setColor(Color.BLUE)
                .setVibrate(new long[]{1000, 10000, 500, 500})
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pIntent)
                .setAutoCancel(true);
    }
}
