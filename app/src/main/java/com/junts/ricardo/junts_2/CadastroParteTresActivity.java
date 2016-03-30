package com.junts.ricardo.junts_2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

public class CadastroParteTresActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setTitle("Cadastro");

        setContentView(R.layout.activity_cadastro_parte_tres);

        final NumberPicker dia = (NumberPicker) findViewById(R.id.dia);
        dia.setMinValue(1);
        dia.setMaxValue(31);

        final NumberPicker mes = (NumberPicker) findViewById(R.id.mes);
        mes.setMinValue(1);
        mes.setMaxValue(12);

        final NumberPicker ano = (NumberPicker) findViewById(R.id.ano);
        ano.setMinValue(1910);
        ano.setMaxValue(2001);

        final Intent cadastroIntent = new Intent(getBaseContext(), CadastroGeneroActivity.class);
        Button mCadastreButton = (Button) findViewById(R.id.passo_tres);
        mCadastreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            String valorAno = String.valueOf(ano.getValue());
            String valorMes = String.valueOf(mes.getValue());
            String valorDia = String.valueOf(dia.getValue());

            //Guarda em variavel do Cadastro
            SharedPreferences dadosCadastroJunts = getSharedPreferences("Cadastro", MODE_PRIVATE);
            SharedPreferences.Editor editor = dadosCadastroJunts.edit();

            Log.e("JUNTS ANIVERSARIO",valorAno);

            editor.putString("aniversario", valorAno+'-'+valorMes+'-'+valorDia);
            editor.commit();

            startActivity(cadastroIntent);
            }

        });
    }
}
