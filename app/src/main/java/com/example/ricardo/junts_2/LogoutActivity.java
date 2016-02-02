package com.example.ricardo.junts_2;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Created by ricardo on 02/02/16.
 */
public class LogoutActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserLogout logout = new UserLogout();
        logout.execute();
    }

    public class UserLogout extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground (Void...params){

            SharedPreferences dadosServerJunts = getSharedPreferences("Dados", MODE_PRIVATE);
            Map<String, ?> URLJunts = dadosServerJunts.getAll();

            Log.e("DeslogarRadius", "http://" + (String) URLJunts.get("URLJunts") + "/logout");
            try {
                URL urlLogout = null;
                urlLogout = new URL("http://" + (String) URLJunts.get("URLJunts") + "/logout");
                HttpURLConnection ucon = (HttpURLConnection) urlLogout.openConnection();

                ucon.setReadTimeout(15000);
                ucon.setConnectTimeout(15000);
                ucon.setRequestMethod("GET");
                ucon.setDoInput(true);
                ucon.setDoOutput(true);

                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(ucon.getInputStream()));
                while ((line=br.readLine()) != null) {
                    Log.e("JUNTS LOGOUT RESP", line);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    }


}
