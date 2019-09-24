package br.edu.ucsal.colabmeiapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;

import br.edu.ucsal.colabmeiapp.R;
import br.edu.ucsal.colabmeiapp.config.FirebaseConfig;
import br.edu.ucsal.colabmeiapp.helper.UsuarioFirebase;
import br.edu.ucsal.colabmeiapp.model.Feed;
import br.edu.ucsal.colabmeiapp.model.Publicacao;
import br.edu.ucsal.colabmeiapp.model.PublicacaoCurtida;
import br.edu.ucsal.colabmeiapp.model.Usuario;
import de.hdodenhof.circleimageview.CircleImageView;

public class VisualizarPublicacaoActivity extends AppCompatActivity {
    private TextView textNomePerfilPublicacao, textDescricaoPerfilPublicacao, qtdCurtidas;
    private ImageView fotoPublicadaPerfilPublicacao, visualizarComentario;;
    private CircleImageView imagePerfilPublicacao;
    private LikeButton likeButton;
    private Feed feedVariavel = new Feed();

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

        //Recupera usuario logado
        final Usuario usuarioLogado = UsuarioFirebase.getDadosUsuariologado();

        //recupera dados da activity
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){

            Publicacao publicacao = (Publicacao) bundle.getSerializable("postagem");
            Usuario usuario = (Usuario) bundle.getSerializable("usuario");

            if (usuario.getId().equals(usuarioLogado.getId())){
                likeButton.setEnabled(false);
            }


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

            feedVariavel.setDescricao(publicacao.getDescricao());
            feedVariavel.setFotoPostagem(publicacao.getCaminhoFoto());
            feedVariavel.setIdPostagem(publicacao.getId());


            //Add evento de clique no comentario
            visualizarComentario.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(VisualizarPublicacaoActivity.this, ComentariosActivity.class);
                    i.putExtra("idPostagem", feedVariavel.getIdPostagem() );
                    startActivity( i );

                }
            });

            //recuperar dados da publicacao curtida
            DatabaseReference curtidasRef = FirebaseConfig.getFirebaseDatabase()
                    .child("postagens-curtidas")
                    .child( feedVariavel.getIdPostagem() );
            curtidasRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    int curtidas = 0;
                    if( dataSnapshot.hasChild("qtdCurtidas") ){
                        PublicacaoCurtida publicacaoCurtida = dataSnapshot.getValue(PublicacaoCurtida.class);
                        curtidas = publicacaoCurtida.getQtdCurtidas();
                    }

                    //Verifica se ja foi clicado
                    if ( dataSnapshot.hasChild(usuarioLogado.getId()) ){
                        likeButton.setLiked(true);
                    } else {
                        likeButton.setLiked(false);
                    }

                    //Monta objeto publicacao curtida
                    final PublicacaoCurtida curtida = new PublicacaoCurtida();
                    curtida.setFeed( feedVariavel );
                    curtida.setUsuario( usuarioLogado );
                    curtida.setQtdCurtidas( curtidas );

                    //Add evento de curtir publicacao
                    likeButton.setOnLikeListener(new OnLikeListener() {
                        @Override
                        public void liked(LikeButton likeButton) {
                            curtida.salvar();
                            qtdCurtidas.setText(curtida.getQtdCurtidas() + " curtidas");

                        }

                        @Override
                        public void unLiked(LikeButton likeButton) {
                            curtida.removerCurtida();
                            qtdCurtidas.setText(curtida.getQtdCurtidas() + " curtidas");

                        }
                    });

                    qtdCurtidas.setText(curtida.getQtdCurtidas() + " curtidas");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }



    private void inicializarComponentes() {
        textNomePerfilPublicacao = findViewById(R.id.textNomePerfilPublicacao);
        textDescricaoPerfilPublicacao = findViewById(R.id.textDescricaoPerfilPublicacao);
        fotoPublicadaPerfilPublicacao = findViewById(R.id.imagePublicacaoSelecionada);
        imagePerfilPublicacao = findViewById(R.id.imagePerfilPublicacao);
        likeButton =  findViewById(R.id.likeButtonFeed);
        visualizarComentario = findViewById(R.id.imageChatAzul);
        qtdCurtidas = findViewById(R.id.textCurtidasPerfilPublicacao);

    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
