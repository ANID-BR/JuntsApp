package com.junts.ricardo.junts_2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Map;

public class ConfirmacaoCadastroActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacao_cadastro);

        SharedPreferences dadosCadastro = getSharedPreferences("Cadastro", MODE_PRIVATE);
        Map<String, ?> itensCadastroJunts = dadosCadastro.getAll();

        final String telefone = (String) itensCadastroJunts.get("telefone");
        final String idUsuario = (String) itensCadastroJunts.get("idUsuario");

        TextView texto = (TextView) findViewById(R.id.textoConfirmacao);
        texto.setText("Para confirmar o seu cadastro coloque abaixo o código de ativação que foi enviado para o telefone: " + telefone);

        TextView textoTermo = (TextView) findViewById(R.id.textoConcordoTermo);
        textoTermo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://junts.com.br"));
                startActivity(intent);
            }
        });

        final CheckBox ckTermo = (CheckBox) findViewById(R.id.checkBoxConfirmacao);

        //TODO Apos confirmar SMS colocar "confirmado" no SharedPreferences de DadosLogin
        Button mCadastreButton = (Button) findViewById(R.id.confirmar);
        mCadastreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ckTermo.isChecked()==true) {

                    //View confirmacaoLayout = findViewById(R.id.contentSMS);
                    EditText e = (EditText) findViewById(R.id.respostaSMS);
                    String codigo = e.getText().toString();

                    Log.e("JUNTS IdUsuario", idUsuario);
                    Log.e("JUNTS Codigo", "teste" + codigo);

                    if (codigo.equals(idUsuario)) {
                        SharedPreferences dadosCadastroJunts = getSharedPreferences("DadosLogin", MODE_PRIVATE);
                        SharedPreferences.Editor editor = dadosCadastroJunts.edit();

                        editor.putString("confirmado", "confimado");
                        editor.putString("login", telefone);
                        editor.putString("senha", telefone);
                        editor.commit();

                        //Joga para o login e loga
                        Intent intentLogin = new Intent(ConfirmacaoCadastroActivity.this, LoginActivity.class);
                        startActivity(intentLogin);
                    } else {
                        TextView texto = (TextView) findViewById(R.id.textoConfirmacao);
                        texto.setText("Verifique novamente o código.");
                    }
                } else {
                    Toast.makeText(getApplication().getBaseContext(), "Você precisa confirmar o termo.", Toast.LENGTH_LONG).show();
                }


            }

        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


}
