
package com.example.ricardo.junts_2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;

public class BackgroundJuntsService extends Service {

    private NotificationManager mNM;
    Bundle b;
    Intent notificationIntent;
    private final IBinder mBinder = new LocalBinder();
    private String newtext;

    public class LocalBinder extends Binder {
        BackgroundJuntsService getService() {
            return BackgroundJuntsService.this;
        }
    }

    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        newtext = "BackGroundApp Service Running";
        PendingIntent contentIntent = PendingIntent.getActivity(BackgroundJuntsService.this, 0, new Intent(BackgroundJuntsService.this, DadosClienteActivity.class), 0);

        NotificationCompat.Builder notification = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("My notification")
                .setContentText("Hello World!")
                .setContentIntent(contentIntent);
        mNM.notify(R.string.local_service_started, notification.build());
        notificationIntent = new Intent(this, DadosClienteActivity.class);
        showNotification();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
    public void onDestroy() {
        mNM.cancel(R.string.local_service_started);
        stopSelf();
    }
    private void showNotification() {
        CharSequence text = getText(R.string.local_service_started);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,new Intent(this, DadosClienteActivity.class), 0);

        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("My notification")
                .setContentText("Notificação")
                .setContentIntent(contentIntent);
        Notification notification = notificationBuilder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        mNM.notify(R.string.local_service_started, notification);
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}