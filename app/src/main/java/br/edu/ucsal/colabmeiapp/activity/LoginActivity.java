package br.edu.ucsal.colabmeiapp.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;

import br.edu.ucsal.colabmeiapp.R;
import br.edu.ucsal.colabmeiapp.config.FirebaseConfig;

public class LoginActivity extends AppCompatActivity {

    private Button botaoEntrar;
    private EditText campoEmail, campoSenha;
    private ProgressBar loading;

    private FirebaseAuth autenticacao;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);




        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        inicializaComponentes();
        loading.setVisibility(View.GONE);
        autenticacao = FirebaseConfig.getFirebaseAutenticacao();

        botaoEntrar.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();
                loading.setVisibility(View.VISIBLE);

                if(!email.isEmpty()){ //Verifica se o email esta preenchido
                    if(!senha.isEmpty()) { //Verifica se a senha esta preenchida

                        autenticacao.signInWithEmailAndPassword(
                                email, senha
                        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()){
                                    loading.setVisibility(View.GONE);
                                    abrirTelaPrincipal();
                                    Toast.makeText(LoginActivity.this,
                                            "Usuario logado com sucesso!",
                                            Toast.LENGTH_SHORT).show();
                                }else {

                                    String erroExcecao = "";

                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthInvalidCredentialsException e){
                                        erroExcecao = "Senha ou E-mail não conferem!";
                                    } catch (Exception e) {
                                        erroExcecao = e.getMessage();
                                        e.printStackTrace();
                                    }

                                    loading.setVisibility(View.GONE);
                                    Toast.makeText(LoginActivity.this,
                                            "Erro ao fazer login : " + erroExcecao,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else {
                        loading.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this,
                                "Preencha a Senha!",
                                Toast.LENGTH_SHORT).show();
                    }
                }else{
                    loading.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this,
                            "Preencha o E-mail!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void inicializaComponentes(){
        campoEmail = findViewById(R.id.loginEmailText);
        campoSenha = findViewById(R.id.loginSenhaText);
        botaoEntrar =  findViewById(R.id.login_btnEntrar);
        loading = findViewById(R.id.loginProgressBar);

    }

    public void abrirTelaCadastro(View view){
        startActivity(new Intent(this, CadastroActivity.class));
    }

    public void abrirTelaPrincipal(){
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();

        if (usuarioAtual != null ){
            abrirTelaPrincipal();
            finish();
        }
    }
}
