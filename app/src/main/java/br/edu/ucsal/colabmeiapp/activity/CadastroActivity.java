package br.edu.ucsal.colabmeiapp.activity;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.awt.font.TextAttribute;

import br.edu.ucsal.colabmeiapp.R;

public class CadastroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        getSupportActionBar().hide();

        final TextInputEditText campoCPFeCNPJ = findViewById(R.id.cadastro_TextCPF);
        Switch switchCadastro = findViewById(R.id.cadastro_switch);

        // Guardando o texto original do Hint
        final CharSequence hintPadrao =  campoCPFeCNPJ.getHint();

        switchCadastro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                // Verificando se o switch está marcado, caso esteja, alterar o valor para 'CNPJ', caso não, restaurar para o valor padrão "CPF"
                if (checked) {
                    campoCPFeCNPJ.setHint("CNPJ");
                } else {
                    campoCPFeCNPJ.setHint(hintPadrao);
                }
            }
        });
    }



}
