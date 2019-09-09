package br.edu.ucsal.colabmeiapp.activity;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import br.edu.ucsal.colabmeiapp.R;
import br.edu.ucsal.colabmeiapp.model.Publicacao;
import br.edu.ucsal.colabmeiapp.model.Usuario;
import de.hdodenhof.circleimageview.CircleImageView;

public class VisualizarPublicacaoActivity extends AppCompatActivity {
    private TextView textNomePerfilPublicacao, textCurtidasPerfilPublicacao, textDescricaoPerfilPublicacao, textVerComentariosPerfilPublicacao;
    private ImageView fotoPublicadaPerfilPublicacao;
    private CircleImageView imagePerfilPublicacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_publicacao);

        //inicializar componentes
        inicializarComponentes();

        //configura toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Visualizar publicação");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_azul);

        //recupera dados da activity
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){

            Publicacao publicacao = (Publicacao) bundle.getSerializable("postagem");
            Usuario usuario = (Usuario) bundle.getSerializable("usuario");

            //exibe dados do usuario
            Uri uri = Uri.parse(usuario.getCaminhoFoto());
            Glide.with(VisualizarPublicacaoActivity.this)
                    .load(uri)
                    .into(imagePerfilPublicacao);
            textNomePerfilPublicacao.setText(usuario.getNomeXrazao());

            //Exibe dados da postagem
            Uri uriPostagem = Uri.parse(publicacao.getCaminhoFoto());
            Glide.with(VisualizarPublicacaoActivity.this)
                    .load(uriPostagem)
                    .into(fotoPublicadaPerfilPublicacao);
            textDescricaoPerfilPublicacao.setText(publicacao.getDescricao());
        }
    }

    private void inicializarComponentes() {
        textNomePerfilPublicacao = findViewById(R.id.textNomePerfilPublicacao);
        textCurtidasPerfilPublicacao = findViewById(R.id.textCurtidasPerfilPublicacao);
        textDescricaoPerfilPublicacao = findViewById(R.id.textDescricaoPerfilPublicacao);
        fotoPublicadaPerfilPublicacao = findViewById(R.id.imagePublicacaoSelecionada);
        imagePerfilPublicacao = findViewById(R.id.imagePerfilPublicacao);

    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
