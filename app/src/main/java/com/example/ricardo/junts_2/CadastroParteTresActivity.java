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

public class CadastroParteTresActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_parte_tres);

        final Intent cadastroIntent = new Intent(getBaseContext(), CadastroParteQuatroActivity.class);
        Button mCadastreButton = (Button) findViewById(R.id.passo_tres);
        mCadastreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Guarda em variavel do Cadastro
                SharedPreferences dadosCadastroJunts = getSharedPreferences("Cadastro", MODE_PRIVATE);
                SharedPreferences.Editor editor = dadosCadastroJunts.edit();

                TextView aniversario = (TextView) findViewById(R.id.aniversario);
                editor.putString("aniversario", aniversario.getText().toString());
                editor.commit();

                startActivity(cadastroIntent);

            }

        });
    }
}
