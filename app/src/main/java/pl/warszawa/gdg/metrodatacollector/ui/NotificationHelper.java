package pl.warszawa.gdg.metrodatacollector.ui;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import pl.warszawa.gdg.metrodatacollector.R;
import pl.warszawa.gdg.metrodatacollector.subway.Station;

public class NotificationHelper {
    private static NotificationManager notificationManager;


    public static final int NOTIFICATION_UNKNOWN_ID = 23411;
    public static final int NOTIFICATION_STATION_ID = 23412;
    public static final int NOTIFICATION_RUNNING_ID = 23413;

    public static NotificationManager getNotificationManager(Context context) {
        if(notificationManager == null){
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    public static void showStation(Station station, Context context) {
        if(station == null || station.getName() == null || context == null) {
            return;
        }
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_action_directions_subway)
                        .setAutoCancel(true)
                        .setContentTitle(station.getName())
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setContentText("Known location.");
        getNotificationManager(context).notify(NOTIFICATION_STATION_ID, mBuilder.build());
    }

    public static void showNotification(int id, String title, String subtitle, Context context) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_action_directions_subway)
                        .setAutoCancel(true)
                        .setContentTitle(title)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setContentText(subtitle);
        getNotificationManager(context).notify(id, mBuilder.build());
    }

    public static void showNotificationNewPlace(String cellId, Context context) {
        Intent intent = new Intent(context, ActivityAddNewPoint.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        intent.setAction(ActivityAddNewPoint.STOP_LISTENING);
        PendingIntent pendingStopIntent = PendingIntent.getActivity(context, 0, intent, 0);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_action_directions_subway)
                        .setAutoCancel(true)
                        .setContentTitle("Unknown place")
                        .setContentText("New cell id detected: " + cellId)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                .addAction(new NotificationCompat.Action(R.drawable.ic_action_add, "Add", pendingIntent))
                .addAction(new NotificationCompat.Action(R.drawable.ic_action_pause, "Exit", pendingStopIntent));
        getNotificationManager(context).notify(NOTIFICATION_UNKNOWN_ID, mBuilder.build());
    }

    public static void hideNotificationNewPlace(Context context) {
        hideNotification(context, NOTIFICATION_UNKNOWN_ID);
    }

    public static void hideNotification(Context context, int notificationId) {
        getNotificationManager(context).cancel(notificationId);
    }

    public static void showRunningNotification(Context context) {
        if(!isNotificationVisible(context, 4455)) {
            Intent intent = new Intent(context, ActivityAddNewPoint.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setAction(ActivityAddNewPoint.STOP_LISTENING);
            PendingIntent pendingStopIntent = PendingIntent.getActivity(context, 0, intent, 0);


            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_action_directions_subway)
                            .setAutoCancel(false)
                            .setOngoing(true)
                            .setProgress(0, 0, true)
                            .setContentTitle("Gathering data... ")
                            .addAction(new NotificationCompat.Action(R.drawable.ic_action_pause, "Exit", pendingStopIntent));
            ;
            getNotificationManager(context).notify(NOTIFICATION_RUNNING_ID, mBuilder.build());
        }
    }

    public static void hideStationNotification(Context context) {
        getNotificationManager(context).cancel(NOTIFICATION_STATION_ID);
    }

    public static void hideRunningNotification(Context context) {
        getNotificationManager(context).cancel(NOTIFICATION_RUNNING_ID);
    }

    public static boolean isNotificationVisible(Context context, int notificationId) {
        Intent notificationIntent = new Intent(context, ActivityAddNewPoint.class);
        PendingIntent test = PendingIntent.getActivity(context, notificationId, notificationIntent, PendingIntent.FLAG_NO_CREATE);
        return test != null;
    }
}
