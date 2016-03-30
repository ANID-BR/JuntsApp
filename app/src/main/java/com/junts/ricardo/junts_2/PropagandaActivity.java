package com.junts.ricardo.junts_2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class PropagandaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_propaganda);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Propaganda");
        setSupportActionBar(toolbar);

        //TODO Verificar se esta em uma rede JUNTS
        WifiManager wifiManager = (WifiManager) getBaseContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        String nomeWifi = wifiInfo.getSSID().replace("\"","").trim();
        String nomeWifiTratado = nomeWifi.replace(" ","_");
        Log.e("Junts SSID", nomeWifiTratado);
        if(!nomeWifi.startsWith("JUNTS")) {
            Toast.makeText(getApplication().getBaseContext(), "Antenção, você não esta conectado em uma rede JUNTS!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        }

        //TODO Verificar se o login é o mesmo que esta guardado
        SharedPreferences dadosLogin = getSharedPreferences("DadosLogin", MODE_PRIVATE);
        Map<String, ?> itensCadastroJunts = dadosLogin.getAll();
        final String login = (String) itensCadastroJunts.get("login");
        final String senha = (String) itensCadastroJunts.get("senha");

        //Botão para logar no JUNTS
        final Button button = (Button) findViewById(R.id.continuarInternet);
        final ProgressBar pbContinuarInternet = (ProgressBar) findViewById(R.id.progressBarContinuarInternet);

        //TODO Chamar o login do Radius
        pbContinuarInternet.setAlpha(100);
        button.setAlpha(0);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Logar direto aqui
                try {

                    button.setAlpha(0);
                    pbContinuarInternet.setAlpha(100);

                    UserLoginTask mAuthTask = new UserLoginTask(login, senha);
                    mAuthTask.execute();

                    //MostrarBotaoLogar();
//TODO Isso deve(pode?) ficar depois do login do Radius
                    //Intent i = new Intent(getBaseContext(), PrincipalActivity.class);
                    //startActivity(i);

                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
        });
        //TODO contar 15s e mostar o botão
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        button.setAlpha(100);
                        pbContinuarInternet.setAlpha(0);
                    }
                },
                9000); // 15s

        WifiManager wifiMan = (WifiManager) getBaseContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        String ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));

        new DownloadImageTask((ImageView) findViewById(R.id.propaganda))
                .execute("http://junts.com.br/propaganda.php?tipo=e&usuario="+login+"&local="+ip);

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

    public String md5(String s) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++) {
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }


    public class UserLoginTask extends AsyncTask<Void, Boolean, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail    = email;
            mPassword = password;
        }

        protected Boolean doInBackground(Void... params) {
            URL url;
            String response = "";
            Boolean respostaLoginRadius = false;
            try {
                //TODO COLOCAR ISSO EM OUTRA CLASSE
                url = new URL("http://junts.com.br/loginApp.html");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                String login = mEmail;
                String senha = mPassword;

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                writer.write("login=" + login + "&senha=" + senha);

                writer.flush();
                writer.close();
                os.close();
                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }

                    if (!response.equals("Nada")) {
                        Log.e("JUNTS JSON RESP 1", response);

                        //TODO Guarda dados do Cliente
                        JSONObject dadosJson = new JSONObject(response);

                        String nome     = dadosJson.getString("nome");
                        String email    = dadosJson.getString("email");
                        String foto     = dadosJson.getString("foto");
                        String endereco = dadosJson.getString("endereco");

                        SharedPreferences dadosCliente = getSharedPreferences("DadosCliente",MODE_PRIVATE);
                        SharedPreferences.Editor editor = dadosCliente.edit();
                        editor.putString("nome", nome);
                        editor.putString("email", email);
                        editor.putString("foto", foto);
                        editor.putString("endereco", endereco);
                        editor.commit();

                        //TODO Colocar dados no menu
                        /*TextView nomeCliente = (TextView) findViewById(R.id.nomeCliente);
                        nomeCliente.setText(nome);

                        if(email != null) {
                            TextView emailCliente = (TextView) findViewById(R.id.emailCliente);
                            emailCliente.setText(email);
                        }*/
                        Log.e("JUNTS Chama Radius", "Radius");
                        //TODO Verificar se o cliente realmente logou no JUNTS
                        respostaLoginRadius = SendPostToRadius(login, senha);
                    } else {
                        Log.e("JUNTS ERRO LOGIN 2", response);
                        return false;
                    }
                } else {
                    return false;
                }
                return true;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        protected void onPostExecute(Boolean resposta) {

            if(resposta) {

            } else {

                Toast.makeText(getApplication().getBaseContext(), "Verifique o Login ou senha!", Toast.LENGTH_LONG).show();

                //Apagar dados de Login
                SharedPreferences dadosCadastroJunts = getSharedPreferences("DadosLogin", MODE_PRIVATE);
                SharedPreferences.Editor editor = dadosCadastroJunts.edit();
                editor.clear();
                editor.commit();

                Intent i = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(i);
            }
        }

        protected boolean SendPostToRadius(String login, String senha) {
            URL url;
            String response = "Olá !";
            Log.e("Resposta Login", response);
            try {
                URL urlFake = new URL("http://www.bing.com");
                HttpURLConnection ucon = (HttpURLConnection) urlFake.openConnection();
                ucon.setInstanceFollowRedirects(false);

                //TODO teste para pegar dados da URL
                String URLServer = ucon.getHeaderField("Location");
                if(URLServer != null) {
                    String[] serverSliced = URLServer.split("/");
                    String serverJunts = serverSliced[2];
                    if (serverJunts.indexOf("bing") == -1) {
                        //TODO Guarda em variavel informaçao do servidor
                        SharedPreferences dadosServerJunts = getSharedPreferences("Dados", MODE_PRIVATE);
                        SharedPreferences.Editor editor = dadosServerJunts.edit();
                        editor.putString("URLJunts", serverJunts);
                        editor.commit();
                        //Log.e("LoginRadius", serverJunts);
                    } else {
                        //Ja esta logado
                        Intent i = new Intent(getBaseContext(), PrincipalActivity.class);
                        startActivity(i);
                    }
                } else {
                    //Ja esta logado
                    Intent i = new Intent(getBaseContext(), PrincipalActivity.class);
                    startActivity(i);
                }

                String servidor = ucon.getHeaderField("Location");

                Log.e("JUNTS SERVIDOR", servidor);

                url = new URL(servidor);
                //url = new URL("https://"+ucon.getHeaderField("Location"));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                //conn.setInstanceFollowRedirects(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                writer.write("username=" + login + "&password=" + senha);

                writer.flush();
                writer.close();
                os.close();

                Log.e("JUNTS Radius", login);
                Log.e("JUNTS Radius", senha);

                String line = "Texto de resposta:";
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
                //Log.e("JUNTS JSON RESP 1", response);

                switch(conn.getResponseCode()) {
                    case HttpURLConnection.HTTP_OK:
                        Log.e("JUNTS HTTP RESP", String.valueOf(HttpURLConnection.HTTP_OK));

                        testarLogin();
                        break;
                    case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
                        Log.e("TesteJUNTS", "ERRO Timeout");
                        break;
                    case HttpURLConnection.HTTP_UNAVAILABLE:
                        Log.e("TesteJUNTS", "ERRO UNAVAILABLE");
                        break;
                    default:

                        Toast.makeText(getApplicationContext(), "Erro no login.", Toast.LENGTH_LONG).show();
                        //Apagar dados de Login
                        SharedPreferences dadosCadastroJunts = getSharedPreferences("DadosLogin", MODE_PRIVATE);
                        SharedPreferences.Editor editor = dadosCadastroJunts.edit();
                        editor.clear();
                        editor.commit();

                        Intent principalIntent = new Intent(getBaseContext(), PrincipalActivity.class);
                        startActivity(principalIntent);
                        Log.e("TesteJUNTS", "ERRO Não logou");
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    protected void testarLogin() {
        try {
            URL urlFake = new URL("http://www.bing.com");
            HttpURLConnection ucon = (HttpURLConnection) urlFake.openConnection();
            ucon.setInstanceFollowRedirects(false);

            //TODO teste para pegar dados da URL
            String URLServer = ucon.getHeaderField("Location");
            if(URLServer != null) {
                String[] serverSliced = URLServer.split("/");
                String serverJunts = serverSliced[2];
                if (serverJunts.indexOf("bing") == -1) {
                    Log.e("JUNTS NOLOGIN", "Não Logou");
                    //TODO Joga apara a Propaganda
                    MostrarBotaoLogar();
                } else {
                    //TODO Joga apara a Principal
                    Intent i = new Intent(getBaseContext(), PrincipalActivity.class);
                    startActivity(i);
                }
            } else {
                Intent i = new Intent(getBaseContext(), PrincipalActivity.class);
                startActivity(i);
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    protected void MostrarBotaoLogar() {

        Log.e("JUNTS NOLOGIN", "Não Logou");
        final Button button = (Button) findViewById(R.id.continuarInternet);
        final ProgressBar pbContinuarInternet = (ProgressBar) findViewById(R.id.progressBarContinuarInternet);

        button.setAlpha(100);
        pbContinuarInternet.setAlpha(0);

    }

    @Override
    protected void onResume() {
        super.onResume();

        //Botão para logar no JUNTS
        final Button button = (Button) findViewById(R.id.continuarInternet);
        final ProgressBar pbContinuarInternet = (ProgressBar) findViewById(R.id.progressBarContinuarInternet);

        //TODO Chamar o login do Radius
        pbContinuarInternet.setAlpha(100);
        button.setAlpha(0);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        button.setAlpha(100);
                        pbContinuarInternet.setAlpha(0);
                    }
                },
                9000); // 15s
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finishAffinity();
    }
}
