package com.junts.ricardo.junts_2;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by ricardo on 26/02/16.
 */
public class JuntsLogin  {

    protected JSONObject trendLists;

    public JuntsLogin() {

    }
    public JSONObject logar(String login, String senha) {
        //TODO Verificar se ja esta logado e enviar o login
        try {
            if (login != null) {
                if (!login.isEmpty()) {
                    Log.e("JUNTS Dados Login", login);

                    //showProgress(true);
                    UserLoginTask mAuthTask = new UserLoginTask(login, senha);
                    mAuthTask.execute();

                   // return trendLists;
                }
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
        return trendLists;
    }

    public boolean TesteSharedPrefencesJunst() {
        try {
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, JSONObject> {

        private final String mEmail;
        private final String mPassword;
        String resposta = "Nada Encontrado";

        UserLoginTask(String email, String password) {
            mEmail    = email;
            mPassword = password;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            URL url;
            String response = "";
            JSONObject respostaJson = null;

            try {
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
                        //JSONObject trendLists;
                        respostaJson = new JSONObject(response);

                        //TODO Verificar se o cliente realmente logou no JUNTS
                        boolean b = SendPostToRadius(login, senha);
                        if(b == true) {
                            return respostaJson;
                        }

                        return respostaJson;
                    } else {
                        Log.e("JUNTS ERRO LOGIN", response);
                    }
                } else {
                    response = "Nada";
                    //return false;
                }
                resposta = response;

                if (!resposta.equals("Nada")) {
                    Log.e("JUNTS JSON RESP 2", resposta);
                    //return true;
                }
                return new JSONObject("");

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                return new JSONObject("");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            //mAuthTask = null;
            //showProgress(true);


            trendLists = response;
            //Toast.makeText(getApplication().getBaseContext(), "Bem Vindo ao JUNTS!", Toast.LENGTH_LONG).show();
            //Intent i = new Intent(getBaseContext(), PrincipalActivity.class);

            //i.putExtra("DadosCliente", resposta);

            //startActivity(i);

            //showProgress(false);

            //mPasswordView.setError(getString(R.string.error_incorrect_password));
            //mPasswordView.requestFocus();
            //Toast.makeText(getApplication().getBaseContext(),"Login ou senha incorretos.",Toast.LENGTH_LONG).show();

        }

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
                URL urlFake = new URL("http://www.bing.com");
                HttpURLConnection ucon = (HttpURLConnection) urlFake.openConnection();
                ucon.setInstanceFollowRedirects(false);
                URL secondURL = new URL(ucon.getHeaderField("Location"));

                //Log.e("TesteJUNTS", ucon.getHeaderField("Location"));

                //TODO teste para pegar dados da URL
                String URLServer = ucon.getHeaderField("Location");
                String[] serverSliced = URLServer.split("/");
                String serverJunts = serverSliced[2];
                if(serverJunts.indexOf("bing") == -1) {
                    //TODO Guarda em variavel informaçao do servidor
                    //SharedPreferences dadosServerJunts = getSharedPreferences("Dados",MODE_PRIVATE);
                    //SharedPreferences.Editor editor = dadosServerJunts.edit();
                    //editor.putString("URLJunts", serverJunts);
                    //editor.commit();
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

                //Log.e("LoginRadius", responseCode);
                return true;

            } catch (Exception e) {
               // Log.e("JUNTS Exception", "Deu Erro");
                e.printStackTrace();
            }
            return false;

        }
    }
}
