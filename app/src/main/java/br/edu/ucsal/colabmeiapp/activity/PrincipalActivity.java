package br.edu.ucsal.colabmeiapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import br.edu.ucsal.colabmeiapp.R;
import br.edu.ucsal.colabmeiapp.config.FirebaseConfig;

public class PrincipalActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        autenticacao = FirebaseConfig.getFirebaseAutenticacao();

        //configura toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("CollabMEI APP");
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId() ) {
            case R.id.meus_anuncios :
                startActivity(new Intent(getApplicationContext(), MeusAnunciosActivity.class));
                break;

            case R.id.menu_perfil :
//                startActivity(new Intent(getApplicationContext(), MeuPerfilActivity.class));
                break;

            case R.id.menu_sair :
                autenticacao.signOut();
                finish();
                Toast.makeText(PrincipalActivity.this,
                        "Usuario deslogado com sucesso!",
                        Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
