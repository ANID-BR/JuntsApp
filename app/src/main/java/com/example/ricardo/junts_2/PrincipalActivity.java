package com.example.ricardo.junts_2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ricardo.junts_2.dummy.LocalConteudo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class PrincipalActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private String nome;
    private String email;
    private Intent intentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            ImageView propaganda = (ImageView) findViewById(R.id.propaganda);
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL("http://junts.com.br/teaser-pasta/img/logo.png").getContent());
            propaganda.setImageBitmap(bitmap);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String extrasJson = getIntent().getStringExtra("DadosCliente");
        if( extrasJson == null ) {
            Intent intentLogin = new Intent(PrincipalActivity.this, LoginActivity.class);
            startActivity(intentLogin);
        } else {
            JSONObject trendLists;
            try {
                trendLists = new JSONObject(extrasJson);
                nome = trendLists.getString("nome");
                email = trendLists.getString("email");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Mais Informações.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            if ( ContextCompat.checkSelfPermission(PrincipalActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                if ( ActivityCompat.shouldShowRequestPermissionRationale(PrincipalActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Toast.makeText(getApplicationContext(), "É necessário que você dê permissões para obtermos sua localização para uma melhor experiência.", Toast.LENGTH_LONG).show();
                } else {
                    ActivityCompat.requestPermissions(PrincipalActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
                }
            }

            LocaisJsonTask locaisJsonTask = new LocaisJsonTask();
            locaisJsonTask.execute();

            intentService = new Intent(PrincipalActivity.this, BackgroundJuntsService.class);
            intentService.putExtra("DadosCliente", getIntent().getStringExtra("DadosCliente"));
            startService(intentService);

        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);

        try {

            TextView nomeCliente = (TextView) findViewById(R.id.nomeCliente);
            nomeCliente.setText(nome);

            TextView emailCliente = (TextView) findViewById(R.id.emailCliente);
            emailCliente.setText(email);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dados) {

            // Handle the camera action
        } else if (id == R.id.nav_pontos) {
            Intent i = new Intent(getBaseContext(), LocalListActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_logout) {
            //TODO Call logout
            Intent logoutIntent = new Intent(PrincipalActivity.this, LogoutActivity.class);
            startActivity(logoutIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public class LocaisJsonTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                URL url = new URL("http://www.junts.com.br/opix/getLocais.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                int responseCode = conn.getResponseCode();
                String response = "";

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }

                } else {
                    response = "Nada";
                }
                LocalListActivity.jsonLocais = response;
                LocalConteudo localConteudo = new LocalConteudo(LocalListActivity.jsonLocais);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return true;
        }

    }
}
