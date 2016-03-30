package com.junts.ricardo.junts_2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CadastroEmailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_email);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Intent cadastroIntent = new Intent(getBaseContext(), cadastroParteDoisActivity.class);
        Button mCadastreButton = (Button) findViewById(R.id.passo_email);
        mCadastreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Guarda em variavel do Cadastro
                SharedPreferences dadosCadastroJunts = getSharedPreferences("Cadastro",MODE_PRIVATE);
                SharedPreferences.Editor editor = dadosCadastroJunts.edit();

                TextView email = (TextView) findViewById(R.id.email);
                editor.putString("email", email.getText().toString());
                editor.commit();

                startActivity(cadastroIntent);
            }
        });


    }

}
