package com.example.ricardo.junts_2;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class CadastroParteQuatroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_parte_quatro);

        CadastroJunts logout = new CadastroJunts();
        logout.execute();

    }
    public class CadastroJunts extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground (Void...params){
            SharedPreferences dadosCadastro = getSharedPreferences("Cadastro", MODE_PRIVATE);
            Map<String, ?> itensCadastroJunts = dadosCadastro.getAll();

            String nome        = (String) itensCadastroJunts.get("nome")+' '+(String) itensCadastroJunts.get("sobrenome");
            String telefone    = (String) itensCadastroJunts.get("telefone");
            String aniversario = (String) itensCadastroJunts.get("aniversario");

            URL url;
            //Log.e("Cadastro JUNTS", nome+telefone+aniversario);
            try {
                url = new URL("http://junts.com.br/cadastroApp.html");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                writer.write("nome=" + nome + "&telefone=" + telefone + "&nascimento=" + aniversario);

                writer.flush();
                writer.close();
                os.close();
                //int responseCode=conn.getResponseCode();

                SharedPreferences dadosCadastroJunts = getSharedPreferences("Cadastro", MODE_PRIVATE);
                SharedPreferences.Editor editor = dadosCadastroJunts.edit();

                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    Log.e("Cadasto JUNTS", line);

                    editor.putString("idUsuario", line);
                    editor.commit();
                }

                String login    = (String) itensCadastroJunts.get("telefone");
                String senha    = (String) itensCadastroJunts.get("telefone");
                String response = "logar";

                //TODO finalizar e logar o usuário
               // boolean s = LoginActivity.UserLoginTask.SendPostToRadius(login, senha, response);


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            finishAffinity();
        }
    }

}
