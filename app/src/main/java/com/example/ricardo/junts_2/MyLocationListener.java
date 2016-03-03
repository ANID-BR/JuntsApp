package com.example.ricardo.junts_2;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.ricardo.junts_2.dummy.LocalConteudo;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by joao on 28/01/16.
 */
public class MyLocationListener implements LocationListener {

    private Context context;

    public static HashMap localizacaoAtual;

    public MyLocationListener(Context context) {
        this.context = context;
    }


    @Override
    public void onLocationChanged(Location location) {
        localizacaoAtual = new HashMap(2);
        localizacaoAtual.put("latitude", location.getLatitude());
        localizacaoAtual.put("longitude", location.getLongitude());

        Map<String, LocalConteudo.LocalItem> locais = LocalConteudo.ITEM_MAP;
        Log.d("RERSE", String.valueOf(locais.size()));
        for (Map.Entry<String, LocalConteudo.LocalItem> local : locais.entrySet()) {
            Location localizacaoPonto = new Location("Localizacao Ponto");
            localizacaoPonto.setLatitude(local.getValue().latitude);
            localizacaoPonto.setLongitude(local.getValue().longitude);

            if(location.distanceTo(localizacaoPonto) < 3000) {
                //BackgroundJuntsService.alertaPontoProximo(this.context, new LatLng(local.getValue().latitude, local.getValue().longitude));
            }
        }

        //Toast.makeText(this.context, "Location changed: Lat: " + location.getLatitude() + " Lng: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
