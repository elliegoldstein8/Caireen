package com.example.queenelizabethviii.caireen2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

public class MyBroadcastReceiver extends BroadcastReceiver

{
    Uri notif;
    Ringtone r;

    @Override
    public void onReceive(Context context, Intent intent){
         notif = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
         r = RingtoneManager.getRingtone(context,notif);
      // String CHANNEL_ID = "my_channel_01";
        String CHANNEL_ID = "Caireen";// The id of the channel.
        CharSequence name = context.getResources().getString(R.string.app_name);// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
        }


        Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(0b11111010000);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, new Intent(context, AddVitReminder.class), PendingIntent.FLAG_UPDATE_CURRENT);
        //PendingIntent stopAlarm = PendingIntent.getActivity(context,0, r.stop(),PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(context)
                    .setContentTitle("It's time to take baby's vitamin!")
                    .setContentText("Click to see details.")
                    .setChannelId(CHANNEL_ID)
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_HIGH) //depreciated na daw
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launchercaireen_round).build();
        }
        else{
            notification = new Notification.Builder(context)
                    .setContentTitle("It's time to take baby's vitamin!")
                    .setContentText("Click to see details.")
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_HIGH) //depreciated na daw
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launchercaireen_round).build();
        }


        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notification.flags|= Notification.FLAG_AUTO_CANCEL;
        manager.notify(0,notification);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(mChannel);
        }

    }




}
