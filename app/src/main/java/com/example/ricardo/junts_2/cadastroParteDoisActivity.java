package com.example.ricardo.junts_2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class cadastroParteDoisActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_parte_dois);

        final Intent cadastroIntent = new Intent(getBaseContext(), CadastroParteTresActivity.class);
        Button mCadastreButton = (Button) findViewById(R.id.passo_dois);
        mCadastreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Guarda em variavel do Cadastro
                SharedPreferences dadosCadastroJunts = getSharedPreferences("Cadastro", MODE_PRIVATE);
                SharedPreferences.Editor editor = dadosCadastroJunts.edit();

                TextView nome = (TextView) findViewById(R.id.nome);
                TextView sobrenome = (TextView) findViewById(R.id.sobrenome);
                editor.putString("nome", nome.getText().toString());
                editor.putString("sobrenome", sobrenome.getText().toString());
                editor.commit();

                startActivity(cadastroIntent);
            }
        });

    }

}
