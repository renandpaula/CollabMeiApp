package br.edu.ucsal.colabmeiapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

import br.edu.ucsal.colabmeiapp.R;
import br.edu.ucsal.colabmeiapp.model.Usuario;

public class PerfilAmigoActivity extends AppCompatActivity {
    private Usuario usuarioSelecionado;
    private Button buttonAcaoPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_amigo);

        //inicializa componentes
        buttonAcaoPerfil = findViewById(R.id.buttonAcaoPerfil);
        buttonAcaoPerfil.setText("Seguir");

        //configura toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Perfil");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_azul);

        //Recuperar usuario selecionado
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            usuarioSelecionado = (Usuario) bundle.getSerializable("usuarioSelecionado");

            //Seta o nome do perfil selecionado na toolbar
            getSupportActionBar().setTitle(usuarioSelecionado.getNomeXrazao());
        }
    }







    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
