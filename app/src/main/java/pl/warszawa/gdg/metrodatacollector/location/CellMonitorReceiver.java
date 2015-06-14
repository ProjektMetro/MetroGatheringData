package pl.warszawa.gdg.metrodatacollector.location;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import de.greenrobot.event.EventBus;
import pl.warszawa.gdg.metrodatacollector.data.busEvents.BackgroundServiceStop;
import pl.warszawa.gdg.metrodatacollector.ui.NotificationHelper;

/**
 * Created by Michal Tajchert on 2015-06-14.
 */
public class CellMonitorReceiver extends BroadcastReceiver {
    public static final String ACTION_RUN_SERVICE = "ACTION_RUN_SERVICE";
    public static final String ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE";
    public static final long DELAY_15_SECONDS = 15000;

    @Override
    final public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if (ACTION_RUN_SERVICE.equals(action)) {
            Intent i = new Intent(context, CellMonitorService.class);
            context.startService(i);
        } else if (ACTION_STOP_SERVICE.equals(action)) {
            stopGsmMonitor(context);
            EventBus.getDefault().post(new BackgroundServiceStop());
        }
    }

    public static void scheduleGsmMonitor(Context context) {
        Intent monitoringIntent = new Intent(context, CellMonitorReceiver.class);
        monitoringIntent.setAction(CellMonitorReceiver.ACTION_RUN_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, monitoringIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), CellMonitorReceiver.DELAY_15_SECONDS, pendingIntent);

        //Also run service manually for first time (to avoid waiting 15 seconds)
        Intent serviceIntent = new Intent(context, CellMonitorService.class);
        context.startService(serviceIntent);
    }

    public static void stopGsmMonitor(Context context) {
        NotificationHelper.hideRunningNotification(context);
        NotificationHelper.hideStationNotification(context);
        NotificationHelper.hideNotificationNewPlace(context);

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent monitoringIntent = new Intent(context, CellMonitorReceiver.class);
        monitoringIntent.setAction(CellMonitorReceiver.ACTION_RUN_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, monitoringIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.cancel(pendingIntent);

        CellMonitorService.reset();
    }
}