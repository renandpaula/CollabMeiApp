package br.edu.ucsal.colabmeiapp.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import br.edu.ucsal.colabmeiapp.R;
import br.edu.ucsal.colabmeiapp.config.FirebaseConfig;

public class InicioActivity extends AppCompatActivity {

    private Button botaoEntrar;
    private EditText campoEmail, campoSenha;
    private ProgressBar loading;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
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
                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                    Toast.makeText(InicioActivity.this,
                                            "Usuario logado com sucesso!",
                                            Toast.LENGTH_SHORT).show();
                                }else {

                                    loading.setVisibility(View.GONE);
                                    Toast.makeText(InicioActivity.this,
                                            "Erro ao fazer login : " + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else {
                        loading.setVisibility(View.GONE);
                        Toast.makeText(InicioActivity.this,
                                "Preencha a Senha!",
                                Toast.LENGTH_SHORT).show();
                    }
                }else{
                    loading.setVisibility(View.GONE);
                    Toast.makeText(InicioActivity.this,
                            "Preencha o E-mail!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void inicializaComponentes(){
        campoEmail = findViewById(R.id.inicioEmailText);
        campoSenha = findViewById(R.id.inicioSenhaText);
        botaoEntrar =  findViewById(R.id.inicio_btnEntrar);
        loading = findViewById(R.id.inicioProgressBar);

    }

    public void abrirTelaCadastro(View view){
        startActivity(new Intent(this, CadastroActivity.class));
    }
}
