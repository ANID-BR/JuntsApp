package com.junts.ricardo.junts_2;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.junts.ricardo.junts_2.dummy.LocalConteudo;

import java.util.HashMap;
import java.util.List;
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

            //if(location.distanceTo(localizacaoPonto) < 3000) {
            //TODO Colocar para pegar o SSID do JUNTS
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();

            WifiInfo info = wifiManager.getConnectionInfo();
            String SSIDConectado = info.getSSID();
            String SSIDConectadoTratado = SSIDConectado.replace("\"", "").trim();
            //Log.e("JUNTS SSID", String.valueOf(SSIDConectado.startsWith("JUNTS")));
            if(SSIDConectadoTratado.startsWith("JUNTS") == false) {
                Log.e("JUNTS -->", "ENTROU");
                for (WifiConfiguration i : list) {
                    //Log.e("JUNTS PONTO", i.SSID);
                    String SSIDTratado = i.SSID.replace("\"", "").trim();
                    if (SSIDTratado.startsWith("JUNTS") == true) {
                        Log.e("JUNTS -->", "ENTROU 2");
                        BackgroundJuntsService.alertaPontoProximo(this.context, new LatLng(local.getValue().latitude, local.getValue().longitude));
                        //break;
                    }
                }
            }
            //}
        }

        PrincipalActivity.mapaPronto(location);
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
