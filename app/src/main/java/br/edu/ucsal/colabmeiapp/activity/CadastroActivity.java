package br.edu.ucsal.colabmeiapp.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.santalu.maskedittext.MaskEditText;

import br.edu.ucsal.colabmeiapp.R;
import br.edu.ucsal.colabmeiapp.config.FirebaseConfig;
import br.edu.ucsal.colabmeiapp.helper.CpfCnpjUtils;
import br.edu.ucsal.colabmeiapp.helper.UsuarioFirebase;
import br.edu.ucsal.colabmeiapp.model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText campoNome, campoRazao, campoEndereco, campoEmail, campoSenha, campoConfirmaSenha;
    private MaskEditText campoCPF, campoCNPJ, campoTelefone;
    private Switch switchTipoUsuario;
    private ProgressBar loading;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        //Inicializa componentes
        campoNome = findViewById(R.id.edit_TextNome);
        campoRazao = findViewById(R.id.edit_TextRazaoSocial);
        campoCPF = findViewById(R.id.edit_TextCPF);
        campoCNPJ = findViewById(R.id.edit_TextCNPJ);
        campoTelefone = findViewById(R.id.edit_TextTelefone);
        campoEndereco = findViewById(R.id.edit_TextEndereco);
        campoEmail = findViewById(R.id.edit_TextEmail);
        campoSenha = findViewById(R.id.cadastro_TextSenha);
        campoConfirmaSenha = findViewById(R.id.cadastro_TextConfirmaSenha);
        switchTipoUsuario = findViewById(R.id.cadastro_switch);
        loading = findViewById(R.id.cadastro_ProgressBar);

        final TextInputLayout layoutNome = findViewById(R.id.editLayout_campoNome);
        final TextInputLayout layoutRazao = findViewById(R.id.editLayout_campoRazao);
        final TextInputLayout layoutCPF = findViewById(R.id.editLayout_campoCPF);
        final TextInputLayout layoutCNPJ = findViewById(R.id.editLayout_campoCNPJ);

        loading.setVisibility(View.GONE);

        switchTipoUsuario.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                // Verificando se o switch de pessoa juridica está marcado, caso esteja, exibir os elementos 'CNPJ' e 'Razao Social' e ocultar 'Nome" e 'CPF'
                if (checked) {
                    layoutNome.setVisibility(View.GONE);
                    layoutRazao.setVisibility(View.VISIBLE);
                    layoutCPF.setVisibility(View.GONE);
                    layoutCNPJ.setVisibility(View.VISIBLE);
                    limpaCampos();
                    campoRazao.requestFocus();

                } else {
                    layoutNome.setVisibility(View.VISIBLE);
                    layoutRazao.setVisibility(View.GONE);
                    layoutCPF.setVisibility(View.VISIBLE);
                    layoutCNPJ.setVisibility(View.GONE);
                    limpaCampos();
                    campoNome.requestFocus();
                }
            }
        });


    }

    private void limpaCampos() {
        campoNome.getText().clear();
        campoRazao.getText().clear();
        campoCPF.getText().clear();
        campoCNPJ.getText().clear();
        campoTelefone.getText().clear();
        campoEndereco.getText().clear();
        campoEmail.getText().clear();
        campoSenha.getText().clear();
        campoConfirmaSenha.getText().clear();
    }


    public void validarCadastroUsuario(View view){

            //Recuperar textos digitados pelo usuario
            String textoNome = campoNome.getText().toString();
            String textoRazao = campoRazao.getText().toString();
            String textoCPF = campoCPF.getRawText();
            String textoCNPJ = campoCNPJ.getRawText();
            String textoEndereco = campoEndereco.getText().toString();
            String textoTelefone = campoTelefone.getRawText();
            String textoEmail = campoEmail.getText().toString();
            String textoSenha = campoSenha.getText().toString();
            String textoConfirmaSenha = campoConfirmaSenha.getText().toString();
            String pessoaJuridica = "PJ";
            String pessoaFisica = "PF";



            //Verifica se o cadastro é de pessoa juridica
            if (switchTipoUsuario.isChecked()) {
                if( !textoRazao.isEmpty() ) { //verifica se a Razao Social foi preenchida
                    if( textoCNPJ != null ) { //verifica se o CNPJ foi preenchido

                    } else {
                        Toast.makeText(CadastroActivity.this,
                                "Preencha o CNPJ!",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CadastroActivity.this,
                            "Preencha a Razão Social!",
                            Toast.LENGTH_SHORT).show();
                }
            } else {   //Valida as informações de pessoa física
                if( !textoNome.isEmpty() ) { //verifica se o nome foi preenchido
                    if( textoCPF != null ) { //verifica se o CPF foi preenchido

                    } else {
                        Toast.makeText(CadastroActivity.this,
                                "Preencha o CPF!",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CadastroActivity.this,
                            "Preencha o nome!",
                            Toast.LENGTH_SHORT).show();
                }
            }
//verifica se a senha confirmada é igual a digitada.
            //Volta ao fluxo normal de validação de cadastro
            if( textoTelefone != null ) { //verifica se o Telefone foi preenchido
                if( !textoEndereco.isEmpty() ) { //verifica se o Endereco foi preenchido
                    if( !textoEmail.isEmpty() ) { //verifica se o Email foi preenchido
                        if( !textoSenha.isEmpty() ) { //verifica se a senha foi preenchida
                            if( !textoConfirmaSenha.isEmpty() ) { //verifica se a senha confirmada foi preenchida.
                                if(textoConfirmaSenha.equals(textoSenha)){
                                    Usuario usuario = new Usuario();
                                    usuario.setEndereco(textoEndereco);
                                    usuario.setEmail(textoEmail);
                                    usuario.setTelefone(textoTelefone);
                                    usuario.setSenha(textoSenha);
                                    if(switchTipoUsuario.isChecked()){ //Usuario tipo PJ
                                        usuario.setTipo(pessoaJuridica);
                                        usuario.setNomeXrazao(textoRazao);
                                        boolean cnpjValido = CpfCnpjUtils.isValid(textoCNPJ); //verifica se o cnpj digitado é valido

                                        if (cnpjValido){
                                            usuario.setCpfXcnpj(textoCNPJ);
                                            loading.setVisibility(View.VISIBLE);
                                            cadastrarUsuario(usuario);
                                        } else {
                                            campoCNPJ.requestFocus();
                                            Toast.makeText(CadastroActivity.this,
                                                    "CNPJ Inválido!",
                                                    Toast.LENGTH_SHORT).show();
                                        }

                                    } else { //Usuario tipo PF
                                        usuario.setTipo(pessoaFisica);
                                        usuario.setNomeXrazao(textoNome);
                                        boolean cpfValido = CpfCnpjUtils.isValid(textoCPF);  //verifica se o cpf digitado é valido

                                        if (cpfValido){
                                            usuario.setCpfXcnpj(textoCPF);
                                            loading.setVisibility(View.VISIBLE);
                                            cadastrarUsuario(usuario);
                                        } else {
                                            campoCPF.requestFocus();
                                            Toast.makeText(CadastroActivity.this,
                                                    "CPF Inválido!",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }else{
                                    Toast.makeText(CadastroActivity.this,
                                            "As senhas não coincidem!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(CadastroActivity.this,
                                        "Preencha a confirmação de senha!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(CadastroActivity.this,
                                    "Preencha a Senha!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CadastroActivity.this,
                                "Preencha o E-mail!",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CadastroActivity.this,
                            "Preencha o Endereço!",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(CadastroActivity.this,
                        "Preencha o Telefone!",
                        Toast.LENGTH_SHORT).show();
            }


        }

    public void cadastrarUsuario ( final Usuario usuario){

        autenticacao = FirebaseConfig.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()

        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    loading.setVisibility(View.GONE);
                    //Salva os dados do usario no banco de dados
                    String idUsuario = task.getResult().getUser().getUid();
                    usuario.setId( idUsuario );
                    usuario.salvarNoBanco();


                    //Salvar dados no profile
                    UsuarioFirebase.atualizarNomeUsuario(usuario.getNomeXrazao());

                    //Redireciona o usuario a pagina principal
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    Toast.makeText(CadastroActivity.this,
                           "Usuário cadastrado com sucesso!",
                           Toast.LENGTH_SHORT).show();
                    finish();

                } else {

                    String erroExcecao = "";

                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e){
                        erroExcecao = "Digite uma senha mais forte!";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        erroExcecao = "Por favor, digite um e-mail válido.";
                    } catch (FirebaseAuthUserCollisionException e){
                        erroExcecao = "Esta conta já foi cadastrada.";
                    } catch (Exception e) {
                        erroExcecao = "ao cadastrar usuário: " + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroActivity.this,
                            "Erro: " + erroExcecao,
                            Toast.LENGTH_SHORT).show();
                    loading.setVisibility(View.GONE);

                }

            }
        });

    }



}
