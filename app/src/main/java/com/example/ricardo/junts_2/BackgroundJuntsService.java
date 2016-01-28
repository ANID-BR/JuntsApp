<<<<<<< HEAD

package com.example.ricardo.junts_2;

=======
package com.example.ricardo.junts_2;

import android.Manifest;
>>>>>>> b75d5e458316f423ccfd05575e5cbd37427fdcbf
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
<<<<<<< HEAD
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
=======
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;
>>>>>>> b75d5e458316f423ccfd05575e5cbd37427fdcbf

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
<<<<<<< HEAD
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        newtext = "BackGroundApp Service Running";
        PendingIntent contentIntent = PendingIntent.getActivity(BackgroundJuntsService.this, 0, new Intent(BackgroundJuntsService.this, DadosClienteActivity.class), 0);
=======
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        newtext = "BackGroundApp Service Running";
        PendingIntent contentIntent = PendingIntent.getActivity(BackgroundJuntsService.this, 0, new Intent(BackgroundJuntsService.this,   DadosClienteActivity.class), 0);
>>>>>>> b75d5e458316f423ccfd05575e5cbd37427fdcbf

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
<<<<<<< HEAD
}
=======
}
>>>>>>> b75d5e458316f423ccfd05575e5cbd37427fdcbf
