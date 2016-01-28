package com.example.ricardo.junts_2;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class DadosClienteActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dados_cliente);
        try {
            String extrasJson = getIntent().getStringExtra("DadosCliente");

            JSONObject trendLists = new JSONObject(extrasJson);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(trendLists.getString("nome"));
            setSupportActionBar(toolbar);

            TextView dados = (TextView) findViewById(R.id.dadosCliente);
            dados.setText("Nome: "+trendLists.getString("nome"));

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Informações sobre como entrar em contato.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });

            if (ActivityCompat.shouldShowRequestPermissionRationale(DadosClienteActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(getApplicationContext(),"GPS permission allows us to access location data. Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(DadosClienteActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_REQUEST_CODE);
            }

            startService(new Intent(DadosClienteActivity.this,BackgroundJuntsService.class));
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        } catch (JSONException e) {
            Log.e("JUNTS JSON", e.getMessage());
            e.printStackTrace();
        }
    }

}
