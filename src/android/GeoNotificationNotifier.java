package com.cowbell.cordova.geofence;

import android.app.NotificationManager;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class GeoNotificationNotifier {
    private NotificationManager notificationManager;
    private Context context;
    private BeepHelper beepHelper;
    private Logger logger;

    public GeoNotificationNotifier(NotificationManager notificationManager, Context context) {
        this.notificationManager = notificationManager;
        this.context = context;
        this.beepHelper = new BeepHelper();
        this.logger = Logger.getLogger();
    }

    public void notify(Notification notification) {
        logger.log(Log.DEBUG, "ON est bien dans la notification");
        notification.setContext(context);
        final int NOTIFY_ID = 1002;

        // There are hardcoding only for show it's just strings
        String name = "SmartRH_channel";
        String id = "SmartRH_channel_1"; // The user-visible name of the channel.
        String description = "SmartRH_first_channel"; // The user-visible description of the channel.

        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notificationManager.getNotificationChannel(id);
            if (mChannel == null) {
               mChannel = new NotificationChannel(id, name, importance);
               mChannel.setDescription(description);
               mChannel.enableVibration(true);
               mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
               notificationManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(context, id);

            builder.setContentTitle(notification.getTitle())  // required
                   .setSmallIcon(notification.getSmallIcon()) // required
                   .setContentText(notification.getText())  // required         
                   .setAutoCancel(true)
                   .setTicker(notification.getTitle())
                   .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
      } else {

           builder = new NotificationCompat.Builder(context);

           builder.setContentTitle(notification.getTitle())                           // required
                  .setSmallIcon(notification.getSmallIcon()) // required
                  .setContentText(notification.getText())  // required
                  .setAutoCancel(true)
                  //.setContentIntent(pendingIntent)
                  .setTicker(notification.getTitle())
                  .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        } 

        if (notification.openAppOnClick) {
            String packageName = context.getPackageName();
            Intent resultIntent = context.getPackageManager()
                .getLaunchIntentForPackage(packageName);

            if (notification.data != null) {
                resultIntent.putExtra("geofence.notification.data", notification.getDataJson());
                logger.log(Log.DEBUG, "Notif = " + notification.getDataJson());

            }

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                notification.id, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);
        }
        try {
            Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context, notificationSound);
            r.play();
        } catch (Exception e) {
        	beepHelper.startTone("beep_beep_beep");
            e.printStackTrace();
        }
        notificationManager.notify(NOTIFY_ID, builder.build());

        logger.log(Log.DEBUG, notification.toString());
    }
}
