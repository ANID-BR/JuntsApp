package com.example.ricardo.junts_2;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;

import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class BackgroundJuntsService extends Service {

    private static NotificationManager mNM;
    private String dadosCliente;
    private final IBinder mBinder = new LocalBinder();
    private String newtext;

    public class LocalBinder extends Binder {
        BackgroundJuntsService getService() {
            return BackgroundJuntsService.this;
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean GPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!GPSEnabled) {
            Intent intentGps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intentGps.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentGps);
        }
        LocationListener locationListener = new MyLocationListener(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "GPS Não Permitido", Toast.LENGTH_SHORT).show();
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 0, locationListener);

        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        newtext = "BackGroundApp Service Running";
        Intent intentPrincipal = new Intent(BackgroundJuntsService.this,   PrincipalActivity.class);
        intentPrincipal.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if( dadosCliente == null ) {
            dadosCliente = intent.getStringExtra("DadosCliente");
        }
        intentPrincipal.putExtra("DadosCliente", dadosCliente);
        PendingIntent contentIntent = PendingIntent.getActivity(BackgroundJuntsService.this, 0, intentPrincipal, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Junts")
                .setContentText("Sua Internet em qualquer lugar!")
                .setContentIntent(contentIntent);
        Notification notification = notificationBuilder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
        mNM.notify(R.string.local_service_started, notification);

        return START_REDELIVER_INTENT;
    }
    public void onDestroy() {
        mNM.cancel(R.string.local_service_started);
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public static void alertaPontoProximo(Context context, LatLng latitudeLongitudePonto) {

        Intent intentPrincipal = new Intent(context, MapsActivity.class);
        intentPrincipal.putExtra("latitude", latitudeLongitudePonto.latitude);
        intentPrincipal.putExtra("longitude", latitudeLongitudePonto.longitude);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intentPrincipal, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Junts")
                .setContentText("Há um ponto Junts próximo a você, clique para visualizá-lo")
                .setContentIntent(contentIntent);
        Notification notification = notificationBuilder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
        mNM.notify(R.string.local_service_started, notification);

    }


}
