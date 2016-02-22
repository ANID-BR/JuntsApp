package com.example.ricardo.junts_2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class CadastroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadstro);

        final Spinner spinner = (Spinner) findViewById(R.id.paises);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.paises_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        final Intent cadastroIntent = new Intent(getBaseContext(), cadastroParteDoisActivity.class);
        Button mCadastreButton = (Button) findViewById(R.id.passo_um);
        mCadastreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Guarda em variavel do Cadastro
                SharedPreferences dadosCadastroJunts = getSharedPreferences("Cadastro",MODE_PRIVATE);
                SharedPreferences.Editor editor = dadosCadastroJunts.edit();

                TextView telefone = (TextView) findViewById(R.id.telefone);
                editor.putString("pais", spinner.getSelectedItem().toString());
                editor.putString("telefone", telefone.getText().toString());
                editor.commit();

                startActivity(cadastroIntent);
            }
        });

    }


}
