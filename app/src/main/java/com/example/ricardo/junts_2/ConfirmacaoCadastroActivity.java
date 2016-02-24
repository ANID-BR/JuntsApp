package com.example.ricardo.junts_2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Map;

public class ConfirmacaoCadastroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacao_cadastro);

        SharedPreferences dadosCadastro = getSharedPreferences("Cadastro", MODE_PRIVATE);
        Map<String, ?> itensCadastroJunts = dadosCadastro.getAll();

        final String telefone    = (String) itensCadastroJunts.get("telefone");
        final String idUsuario   = (String) itensCadastroJunts.get("idUsuario");

        TextView texto = (TextView) findViewById(R.id.textoConfirmacao);
        texto.setText("Para confirmar o seu cadastro coloque abaixo o código de ativação que foi enviado para o telefone: "+telefone);

        //TODO Apos confirmar SMS colocar "confirmado" no SharedPreferences de DadosLogin
        Button mCadastreButton = (Button) findViewById(R.id.confirmar);
        mCadastreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Guarda em variavel do Cadastro

                //View confirmacaoLayout = findViewById(R.id.contentSMS);
                EditText e = (EditText) findViewById(R.id.respostaSMS);
                String codigo = e.getText().toString();

                Log.e("JUNTS IdUsuario", idUsuario);
                Log.e("JUNTS Codigo", "teste"+codigo);

                if(codigo.equals(idUsuario)) {
                    SharedPreferences dadosCadastroJunts = getSharedPreferences("DadosLogin", MODE_PRIVATE);
                    SharedPreferences.Editor editor = dadosCadastroJunts.edit();

                    editor.putString("confirmado", "confimado");
                    editor.commit();

                    //Joga para o login e loga
                    Intent intentLogin = new Intent(ConfirmacaoCadastroActivity.this, LoginActivity.class);
                    startActivity(intentLogin);
                } else {
                    TextView texto = (TextView) findViewById(R.id.textoConfirmacao);
                    texto.setText("Verifique novamente o código.");
                }


            }

        });

    }

}
