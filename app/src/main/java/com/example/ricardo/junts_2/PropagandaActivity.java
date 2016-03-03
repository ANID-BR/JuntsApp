package com.example.ricardo.junts_2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class PropagandaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_propaganda);

        //TODO Verificar se o login é o mesmo que esta guardado
        SharedPreferences dadosLogin = getSharedPreferences("DadosLogin", MODE_PRIVATE);
        Map<String, ?> itensCadastroJunts = dadosLogin.getAll();
        final String login = (String) itensCadastroJunts.get("login");
        final String senha = (String) itensCadastroJunts.get("senha");

        //Botão para logar no JUNTS
        final Button button = (Button) findViewById(R.id.continuarInternet);
        //TODO Chamar o login do Radius
        button.setAlpha(0);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO Logar direto aqui
                try {
                    UserLoginTask mAuthTask = new UserLoginTask(login, senha);
                    mAuthTask.execute();

                    Intent i = new Intent(getBaseContext(), PrincipalActivity.class);
                    //i.putExtra("DadosCliente", responseCode);
                    startActivity(i);

                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
        });
        //TODO contar 30s e mostar o botão
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        button.setAlpha(100);
                    }
                },
                18000); // 30s

        new DownloadImageTask((ImageView) findViewById(R.id.propaganda))
                .execute("http://junts.com.br/propaganda.php");

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

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail    = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            URL url;
            String response = "";
            boolean respostaLoginRadius = false;
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

                        SharedPreferences dadosCliente = getSharedPreferences("dadosCliente",MODE_PRIVATE);
                        SharedPreferences.Editor editor = dadosCliente.edit();
                        editor.putString("nome", nome);
                        editor.putString("email", email);
                        editor.putString("foto", foto);
                        editor.putString("endereco", endereco);
                        editor.commit();

                        //TODO Verificar se o cliente realmente logou no JUNTS
                        respostaLoginRadius = SendPostToRadius(login, senha);
                    } else {
                        Log.e("JUNTS ERRO LOGIN", response);
                    }
                } else {
                    return false;
                }
                return respostaLoginRadius;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        /*@Override
        protected void onPostExecute(boolean resposta) {
            if(resposta) {
                Toast.makeText(getApplication().getBaseContext(), "Bem Vindo ao JUNTS!", Toast.LENGTH_LONG).show();
                Intent i = new Intent(getBaseContext(), PrincipalActivity.class);

                i.putExtra("DadosCliente", resposta);

                startActivity(i);
            } else {
                Toast.makeText(getApplication().getBaseContext(), "Não logou!", Toast.LENGTH_LONG).show();
            }
        }*/

        @Override
        protected void onCancelled() {
            //mAuthTask = null;
            //showProgress(false);
        }

        protected boolean SendPostToRadius(String login, String senha) {
            URL url;
            String response = "Olá !";
            Log.e("Resposta Login", response);
            try {
                URL urlFake = new URL("http://www.google.com");
                HttpURLConnection ucon = (HttpURLConnection) urlFake.openConnection();
                ucon.setInstanceFollowRedirects(false);
                URL secondURL = new URL(ucon.getHeaderField("Location"));

                //Log.e("TesteJUNTS", ucon.getHeaderField("Location"));

                //TODO teste para pegar dados da URL
                String URLServer = ucon.getHeaderField("Location");
                String[] serverSliced = URLServer.split("/");
                String serverJunts = serverSliced[2];
                if(serverJunts.indexOf("google") == -1) {
                    //TODO Guarda em variavel informaçao do servidor
                    SharedPreferences dadosServerJunts = getSharedPreferences("Dados",MODE_PRIVATE);
                    SharedPreferences.Editor editor = dadosServerJunts.edit();
                    editor.putString("URLJunts", serverJunts);
                    editor.commit();
                    //Log.e("LoginRadius", serverJunts);
                }

                url = new URL(ucon.getHeaderField("Location"));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                writer.write("username=" + login + "&password=" + senha);

                writer.flush();
                writer.close();
                os.close();

                //TODO Guarda o login
                //SharedPreferences dadosCadastroJunts = getSharedPreferences("DadosLogin", MODE_PRIVATE);
                //SharedPreferences.Editor editor = dadosCadastroJunts.edit();

                //editor.putString("login", login);
                //editor.putString("senha", senha);

                //TODO Tirar
                //editor.putString("confirmado", "não");
                //editor.commit();

                Log.e("JUNTS Radius", login);
                Log.e("JUNTS Radius", senha);

                int responseCode=conn.getResponseCode();
                Log.e("TesteJUNTS", "Entrou!");

                //Log.e("LoginRadius", responseCode;

                //Toast.makeText(getApplication().getBaseContext(), "Bem Vindo ao JUNTS!", Toast.LENGTH_LONG).show();

                //return true;

            } catch (Exception e) {
                // Log.e("JUNTS Exception", "Deu Erro");
                e.printStackTrace();
            }
            return false;

        }
    }

}
