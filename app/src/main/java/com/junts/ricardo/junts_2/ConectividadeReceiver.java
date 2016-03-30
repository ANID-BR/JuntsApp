package com.junts.ricardo.junts_2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

/**
 * Created by ricardo on 25/02/16.
 */
public class ConectividadeReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        try {

            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();

            String nomeWifi = wifiInfo.getSSID().replace("\"","").trim();

            Log.e("Junts SSID", nomeWifi);
            if(nomeWifi.startsWith("JUNTS")) {
                Log.e("Junts SSID 2", nomeWifi);

                NotificationManager mNM = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

                Intent intentPrincipal = new Intent(context, PropagandaActivity.class);
                PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intentPrincipal, PendingIntent.FLAG_CANCEL_CURRENT);
                NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.junts_logo)
                        .setCategory(Notification.CATEGORY_PROGRESS)
                        .setContentTitle("Junts")
                        .setContentText("Ola, seus próximos 30mim de internet são patrocinados por:  ")
                        .setContentIntent(contentIntent);
                Notification notification = notificationBuilder.build();
                notification.flags = Notification.FLAG_INSISTENT;
                mNM.notify(R.string.controle_tempo_acesso, notification);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
