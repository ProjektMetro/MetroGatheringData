package pl.warszawa.gdg.metrodatacollector.ui;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import pl.warszawa.gdg.metrodatacollector.R;

public class NotificationHelper {
    private static NotificationManager notificationManager;

    public static NotificationManager getNotificationManager(Context context) {
        if(notificationManager == null){
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    public static void showNotification(int id, String title, String subtitle, Context context) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true)
                        .setContentTitle(title)
                        .setContentText(subtitle);
        getNotificationManager(context).notify(id, mBuilder.build());
    }

    public static void showNotificationNewPlace(String cellId, Context context) {

        Intent intent = new Intent(context, ActivityAddNewPoint.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true)
                        .setContentTitle("Unknown place")
                        .setContentText("New cell id detected: " + cellId)
                .addAction(new NotificationCompat.Action(R.mipmap.ic_launcher, "Add place", pendingIntent));
        getNotificationManager(context).notify(4362, mBuilder.build());
    }

    public static void hideNotificationNewPlace(Context context) {
        getNotificationManager(context).cancel(4362);
    }

    private void showRunningNotification(Context context) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setOngoing(true)
                        .setProgress(0, 0, true)
                        .setContentTitle("Gathering data... ");
        getNotificationManager(context).notify(4455, mBuilder.build());
    }

    private void hideRunningNotification(Context context) {
        getNotificationManager(context).cancel(4455);
    }
}
