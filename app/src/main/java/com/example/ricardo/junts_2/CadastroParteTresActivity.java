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
import android.widget.NumberPicker;
import android.widget.TextView;

public class CadastroParteTresActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_parte_tres);

        NumberPicker dia = (NumberPicker) findViewById(R.id.dia);
        dia.setMinValue(1);
        dia.setMaxValue(31);

        NumberPicker mes = (NumberPicker) findViewById(R.id.mes);
        mes.setMinValue(1);
        mes.setMaxValue(12);

        NumberPicker ano = (NumberPicker) findViewById(R.id.ano);
        ano.setMinValue(1910);
        ano.setMaxValue(2001);

        final String valorAno = String.valueOf(ano.getValue());
        final String valorMes = String.valueOf(mes.getValue());
        final String valorDia = String.valueOf(dia.getValue());

        final Intent cadastroIntent = new Intent(getBaseContext(), CadastroParteQuatroActivity.class);
        Button mCadastreButton = (Button) findViewById(R.id.passo_tres);
        mCadastreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Guarda em variavel do Cadastro
                SharedPreferences dadosCadastroJunts = getSharedPreferences("Cadastro", MODE_PRIVATE);
                SharedPreferences.Editor editor = dadosCadastroJunts.edit();
Log.e("JUNTS ANIVERSARIO",valorAno);
                //TextView aniversario = (TextView) findViewById(R.id.aniversario);
                editor.putString("aniversario", valorAno+'-'+valorMes+'-'+valorDia);
                editor.commit();

                startActivity(cadastroIntent);

            }

        });
    }
}
