package com.junts.ricardo.junts_2;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.junts.ricardo.junts_2.dummy.LocalConteudo;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class PrincipalActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int CONTENT_VIEW_ID = 10101010;
    private String nome;
    private String email;
    private String foto;
    private String endereco;
    private Intent intentService;
    protected Bitmap bitmap;
    private static GoogleMap mapaPrincipal;
    private static TextView textoLocalizacao;
    private static ProgressBar progressBarLocalizacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Boolean deuErro = getIntent().getBooleanExtra("deuErro", false);

        //Voltar para o estado inicial
        /*Button button = (Button) findViewById(R.id.continuarInternet);
        ProgressBar pbContinuarInternet = (ProgressBar) findViewById(R.id.progressBarContinuarInternet);

        pbContinuarInternet.setAlpha(100);
        button.setAlpha(0);*/

        //TODO Verificar se esta em uma rede JUNTS
        WifiManager wifiManager = (WifiManager) getBaseContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        String nomeWifi = wifiInfo.getSSID().replace("\"","").trim();
        String nomeWifiTratado = nomeWifi.replace(" ","_");
        Log.e("Junts SSID", nomeWifiTratado);
        if(!nomeWifi.startsWith("JUNTS")) {
            Toast.makeText(getApplication().getBaseContext(), "Atenção! Você não esta conectado em uma rede JUNTS!", Toast.LENGTH_LONG).show();
        }

        if(deuErro == true) {
            Toast.makeText(getApplicationContext(), "Erro no login.", Toast.LENGTH_LONG).show();
            //Apagar dados de Login
            SharedPreferences dadosCadastroJunts = getSharedPreferences("DadosLogin", MODE_PRIVATE);
            SharedPreferences.Editor editor = dadosCadastroJunts.edit();
            editor.clear();
            editor.commit();
        } else {

            SharedPreferences dadosCliente = getSharedPreferences("DadosCliente", MODE_PRIVATE);
            Map<String, ?> itensDadosClientes = dadosCliente.getAll();

            nome = (String) itensDadosClientes.get("nome");
            email = (String) itensDadosClientes.get("email");
            foto = (String) itensDadosClientes.get("foto");
            endereco = (String) itensDadosClientes.get("endereco");
            String login = (String) itensDadosClientes.get("login");

            Log.e("JUNTS Email", email);

            if (email == null) {
                email = "";
            }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);

            toggle.syncState();

            if (ContextCompat.checkSelfPermission(PrincipalActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(PrincipalActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
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

            //TODO Chamada do mapa
            MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragmentMap);
            mapFragment.getMapAsync(this);

            textoLocalizacao = (TextView) findViewById(R.id.textoLocalizacao);
            progressBarLocalizacao = (ProgressBar) findViewById(R.id.progressBaraLocalizacao);

            //TODO Propaganda 2
            new DownloadPropagandaTask((ImageView) findViewById(R.id.propagandaPrincipal))
                    .execute("http://junts.com.br/propaganda.php?tipo=i&usuario=" + login + "&local=" + nomeWifiTratado);

            /*ImageView img = (ImageView) findViewById(R.id.propagandaPrincipal);
            img.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse("http://junts.com.br/redirecionaPropaganda/"));
                    startActivity(intent);
                }
            });*/

        }
    }

    private class DownloadPropagandaTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadPropagandaTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();

                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            try {
                bmImage.setImageBitmap(result);
            }  catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void mapaPronto(Location location) {

        GoogleMap mMap = mapaPrincipal;
        int totalDePontos = 0;

        HashMap localizacaoAtual = MyLocationListener.localizacaoAtual;
        Log.e("JUNTS MAPA","MAPAPAPAPA");
        if(localizacaoAtual != null) {

            Log.e("JUNTS MAPA", String.valueOf(localizacaoAtual.get("longitude")));

            LatLng latitudeLontitudeAtual = new LatLng((Double) localizacaoAtual.get("latitude"),
                    (Double) localizacaoAtual.get("longitude"));

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latitudeLontitudeAtual, 16));

            mMap.clear();

            //TODO Colcoar os outros pontos
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            mMap.addMarker(new MarkerOptions()
                    .position(latitudeLontitudeAtual)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            );
            builder.include(latitudeLontitudeAtual);

            Map<String, LocalConteudo.LocalItem> locais = LocalConteudo.ITEM_MAP;
            for (Map.Entry<String, LocalConteudo.LocalItem> local : locais.entrySet()) {
                Location localizacaoPonto = new Location("Localizacao Ponto");
                localizacaoPonto.setLatitude(local.getValue().latitude);
                localizacaoPonto.setLongitude(local.getValue().longitude);

                LatLng latitudeLontitudePonto = new LatLng((Double) local.getValue().latitude,
                        (Double) local.getValue().longitude);

                if(location.distanceTo(localizacaoPonto) < 3000) {
                    totalDePontos++;
                    builder.include(latitudeLontitudePonto);

                    mMap.addMarker(new MarkerOptions()
                            .position(latitudeLontitudePonto)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                            .title(local.getValue().nome)
                    );
                }
            }

            LatLngBounds bounds = builder.build();

            //mMap.moveCamera();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            textoLocalizacao.setText("Existem "+totalDePontos+" JUNTS perto de você.");
            progressBarLocalizacao.setAlpha(0);

        }
    }

    public void onMapReady(GoogleMap googleMap) {

        mapaPrincipal = googleMap;

    }

    public static class AddFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            EditText v = new EditText(getActivity());
            v.setText("Bem vindo ao JUNTS");
            return v;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*final Button button = (Button) findViewById(R.id.continuarInternet);
        final ProgressBar pbContinuarInternet = (ProgressBar) findViewById(R.id.progressBarContinuarInternet);
        new android.os.Handler().postDelayed(
            new Runnable() {
                public void run() {
                    button.setAlpha(100);
                    pbContinuarInternet.setAlpha(0);
                }
            },
            9000); // 15s
            */



        //Log.e("JUNTS Ping", "opa!");
        TesteInternet netTeste = new TesteInternet();
        netTeste.execute();



    }

    public class TesteInternet extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            URL url;
            //boolean resposta = false;
            try {

                url = new URL("http://ricardocavalcante.com");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);

                int responseCode = conn.getResponseCode();
                //TODO no MotoX esta dando 302 quando conectado e no LG ta dando 200
                int ok  = 200;
                int ok2 = 303;
                Log.e("JUNTS Ping",String.valueOf(responseCode));
                Log.e("JUNTS Pong",String.valueOf(HttpsURLConnection.HTTP_OK));
                if (responseCode > ok2) {
                    Intent i = new Intent(getBaseContext(), PropagandaActivity.class);
                    startActivity(i);
                } else {
                    /*
                    LocalConteudo.LocalItem item;
                    item = LocalConteudo.ITEM_MAP.get(getIntent().getStringExtra(LocalDetailFragment.ARG_ITEM_ID));

                    if(MyLocationListener.localizacaoAtual == null) {
                        Toast.makeText(getApplicationContext(), "Aguarde, estamos verificando sua localizaçao", Toast.LENGTH_LONG).show();
                    } else {
                    }*/

                    //Intent i = new Intent(getBaseContext(), PrincipalActivity.class);
                    //startActivity(i);

                }
                //return resposta;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finishAffinity();
        /*
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);

        try {

            TextView nomeCliente = (TextView) findViewById(R.id.nomeCliente);
            nomeCliente.setText(nome);

            if(email == null) {
                email = " ";
            }
            TextView emailCliente = (TextView) findViewById(R.id.emailCliente);
            emailCliente.setText(email);

            //TODO Menu lateral troca de imagem
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            //View header = navigationView.inflateHeaderView(R.layout.nav_header_principal);
            View header = navigationView.getHeaderView(0);

            if(foto != null) {
                try {
                //    new DownloadImageTask((ImageView) header.findViewById(R.id.fotoCliente))
                //            .execute("http://junts.com.br/fotosPerfis/" + foto);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //TODO Adicionar imagem do menu
            new DownloadImageTaskMenu((ImageView) header.findViewById(R.id.imagemdefundo))
                    .execute("http://junts.com.br/fotosPerfis/topo_junts_2.png");

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
        if (id == R.id.action_sobre) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("http://junts.com.br"));
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        /*if (id == R.id.nav_pontos) {
            Intent i = new Intent(getBaseContext(), LocalListActivity.class);
            startActivity(i);
        } else*/
        if (id == R.id.nav_logout) {
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

    private class DownloadImageTaskMenu extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTaskMenu(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            try {
                bmImage.setImageBitmap(result);
            }  catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            try {
                bmImage.setImageBitmap(result);
            }  catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // show The Image in a ImageView
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
