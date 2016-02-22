package com.example.ricardo.junts_2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.Map;

public class ConfirmacaoCadastroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacao_cadastro);

        SharedPreferences dadosCadastro = getSharedPreferences("Cadastro", MODE_PRIVATE);
        Map<String, ?> itensCadastroJunts = dadosCadastro.getAll();

        String telefone    = (String) itensCadastroJunts.get("telefone");
        String idUsuario   = (String) itensCadastroJunts.get("idUsuario");

        

    }

}
