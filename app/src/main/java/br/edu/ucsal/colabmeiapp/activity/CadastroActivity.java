package br.edu.ucsal.colabmeiapp.activity;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.awt.font.TextAttribute;

import br.edu.ucsal.colabmeiapp.R;

public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText campoNome, campoRazao, campoCPF, campoCNPJ, campoTelefone, campoEndereco, campoEmail, campoSenha, campoConfirmaSenha;
    private Switch switchTipoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        getSupportActionBar().hide();

        //Inicializa componentes
        campoNome = findViewById(R.id.cadastro_TextNome);
        campoRazao = findViewById(R.id.cadastro_TextRazaoSocial);
        campoCPF = findViewById(R.id.cadastro_TextCPF);
        campoCNPJ = findViewById(R.id.cadastro_TextCNPJ);
        campoTelefone = findViewById(R.id.cadastro_TextTelefone);
        campoEndereco = findViewById(R.id.cadastro_TextEndereco);
        campoEmail = findViewById(R.id.cadastro_TextEmail);
        campoSenha = findViewById(R.id.cadastro_TextSenha);
        campoConfirmaSenha = findViewById(R.id.cadastro_TextConfirmaSenha);
        switchTipoUsuario = findViewById(R.id.cadastro_switch);

        final TextInputLayout layoutNome = findViewById(R.id.textInputLayoutNome);
        final TextInputLayout layoutRazao = findViewById(R.id.textInputLayoutRazao);
        final TextInputLayout layoutCPF = findViewById(R.id.textInputLayoutCPF);
        final TextInputLayout layoutCNPJ = findViewById(R.id.textInputLayoutCNPJ);


        switchTipoUsuario.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                // Verificando se o switch de pessoa juridica está marcado, caso esteja, exibir os elementos 'CNPJ' e 'Razao Social' e ocultar 'Nome" e 'CPF'
                if (checked) {
                    layoutNome.setVisibility(View.GONE);
                    layoutRazao.setVisibility(View.VISIBLE);
                    layoutCPF.setVisibility(View.GONE);
                    layoutCNPJ.setVisibility(View.VISIBLE);
                } else {
                    layoutNome.setVisibility(View.VISIBLE);
                    layoutRazao.setVisibility(View.GONE);
                    layoutCPF.setVisibility(View.VISIBLE);
                    layoutCNPJ.setVisibility(View.GONE);
                }
            }
        });
    }


        public void validarCadastroUsuario(){

            //Recuperar textos digitados pelo usuario
            String textoNome = campoNome.getText().toString();
            String textoRazao = campoRazao.getText().toString();
            String textoCPF = campoCPF.getText().toString();
            String textoCNPJ = campoCNPJ.getText().toString();
            String textoEndereco = campoEndereco.getText().toString();
            String textoTelefone = campoTelefone.getText().toString();
            String textoEmail = campoEmail.getText().toString();
            String textoSenha = campoSenha.getText().toString();
            String textoConfirmaSenha = campoConfirmaSenha.getText().toString();

            if( !textoNome.isEmpty() ) { //verifica se o nome foi preenchido

            } else {
                Toast.makeText(CadastroActivity.this,
                        "Preencha o nome!",
                        Toast.LENGTH_SHORT).show();
            }


        }





}
