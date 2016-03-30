package com.junts.ricardo.junts_2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class CadastroGeneroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setTitle("Cadastro");

        setContentView(R.layout.activity_genero);

        final Spinner spinner = (Spinner) findViewById(R.id.sexo);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sexo_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        final Intent cadastroIntent = new Intent(getBaseContext(), CadastroParteQuatroActivity.class);
        Button mCadastreButton = (Button) findViewById(R.id.passo_genero);
        mCadastreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Guarda em variavel do Cadastro
                SharedPreferences dadosCadastroJunts = getSharedPreferences("Cadastro", MODE_PRIVATE);
                SharedPreferences.Editor editor = dadosCadastroJunts.edit();

                editor.putString("sexo", spinner.getSelectedItem().toString());
                editor.commit();

                startActivity(cadastroIntent);
            }
        });

    }

}
