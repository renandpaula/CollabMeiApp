package br.edu.ucsal.colabmeiapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.edu.ucsal.colabmeiapp.R;
import br.edu.ucsal.colabmeiapp.adapter.AdapterGrid;
import br.edu.ucsal.colabmeiapp.config.FirebaseConfig;
import br.edu.ucsal.colabmeiapp.model.Publicacao;
import br.edu.ucsal.colabmeiapp.model.Usuario;
import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilAmigoActivity extends AppCompatActivity {
    private Usuario usuarioSelecionado;
    private Usuario usuarioLogado;
    private Button buttonAcaoPerfil;
    private CircleImageView imagePerfil;
    private TextView textPostagens, textSeguidores, textSeguindo;
    private DatabaseReference usuarioRef;
    private DatabaseReference usuarioAmigoRef;
    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference seguidoresRef;
    private DatabaseReference firebaseRef;
    private DatabaseReference postagensUsuarioRef;
    private ValueEventListener valueEventListenerPerfilAmigo;
    private GridView gridview_perfil;
    private AdapterGrid adapterGrid;

    private String idUsuarioLogado;
    private List<Publicacao> publicacoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_amigo);

        //configs iniciais
        firebaseRef = FirebaseConfig.getFirebaseDatabase();
        usuarioRef = firebaseRef.child("usuarios");
        seguidoresRef = firebaseRef.child("seguidores");
        idUsuarioLogado = FirebaseConfig.getIdUsuario();


        //inicializa componentes
        inicializarComponentes();

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

            //configura ref postagens usuario
            postagensUsuarioRef = FirebaseConfig.getFirebaseDatabase()
                    .child("publicacoes")
                    .child(usuarioSelecionado.getId());

            //Seta o nome do perfil selecionado na toolbar
            getSupportActionBar().setTitle(usuarioSelecionado.getNomeXrazao());

            //recupera foto do usuario
            String caminhoFoto = usuarioSelecionado.getCaminhoFoto();
            if (caminhoFoto != null){
                Uri url = Uri.parse(caminhoFoto);
                Glide.with(PerfilAmigoActivity.this)
                        .load(url)
                        .into(imagePerfil);
            }
        }

        //inicia image loader
        inicializarImageLoader();

        //carrega fotos postagens
        carregaFotosPostagem();

        //abrir foto clicada
        gridview_perfil.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Publicacao publicacao = publicacoes.get(position);
                Intent i = new Intent(getApplicationContext(), VisualizarPublicacaoActivity.class);
                i.putExtra("publicacao", publicacao);
                i.putExtra("usuario", usuarioSelecionado);

                startActivity(i);

            }
        });

    }

    private void inicializarComponentes(){
        buttonAcaoPerfil = findViewById(R.id.buttonAcaoPerfil);
        buttonAcaoPerfil.setText("Editar Perfil");
        imagePerfil = findViewById(R.id.perfil_image);
        textPostagens = findViewById(R.id.textView_Publicacoes);
        textSeguidores =  findViewById(R.id.textView_Seguidores);
        textSeguindo = findViewById(R.id.textView_Seguindo);
        gridview_perfil =  findViewById(R.id.gridview_perfil);
    }

    public void inicializarImageLoader() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(this)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .build();
        ImageLoader.getInstance().init(config);
    }

    public void carregaFotosPostagem(){

        //recupera fotos postadas pelo user
        publicacoes = new ArrayList<>();
        postagensUsuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //configura tamanho do grid
                int tamanhoGrid = getResources().getDisplayMetrics().widthPixels;
                int tamanhoImagem = tamanhoGrid / 3;
                gridview_perfil.setColumnWidth(tamanhoImagem);

                List<String> urlFotos = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Publicacao publicacao = ds.getValue(Publicacao.class);
                    publicacoes.add(publicacao);
                    urlFotos.add(publicacao.getCaminhoFoto());
                }

                //configurar adatper
                adapterGrid = new AdapterGrid(getApplicationContext(), R.layout.grid_publicacoes, urlFotos);
                gridview_perfil.setAdapter(adapterGrid);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void recuperarDadosUsuarioLogado(){
        usuarioLogadoRef = usuarioRef.child(idUsuarioLogado);
        usuarioLogadoRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        usuarioLogado = dataSnapshot.getValue(Usuario.class);

                        verificaSegueUsuarioAmigo();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }

    private void verificaSegueUsuarioAmigo(){
        DatabaseReference seguidorRef = seguidoresRef
                .child(idUsuarioLogado)
                .child(usuarioSelecionado.getId());

        seguidorRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            //ja esta seguindo
                            habilitarBotaoSeguir(true);
                        }else{
                            //nao esta seguindo
                            habilitarBotaoSeguir(false);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }

    private void habilitarBotaoSeguir(boolean segueUsuario){
        if (segueUsuario){
            buttonAcaoPerfil.setText("Seguindo");
        }else{
            buttonAcaoPerfil.setText("Seguir");

            //adiciona evento para seguir usuario
            buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //salvar seguidor
                    salvarSeguidor(usuarioLogado, usuarioSelecionado);
                }
            });
        }
    }

    private void salvarSeguidor(Usuario userLogado, Usuario userAmigo){
        /* estrutura:
        seguidores
            id_usuariologado
                id_usuarioASeguir
                    dados_seguindo
         */
        HashMap<String,Object> dadosAmigo = new HashMap<>();
        dadosAmigo.put("nomeXrazao", userAmigo.getNomeXrazao());
        dadosAmigo.put("caminhoFoto", userAmigo.getCaminhoFoto());
        DatabaseReference seguidorRef = seguidoresRef
                .child(userLogado.getId())
                .child(userAmigo.getId());
        seguidorRef.setValue(dadosAmigo);

        //alterar botao
        buttonAcaoPerfil.setText("Seguindo");
        buttonAcaoPerfil.setOnClickListener(null);

        //incrementar seguindo usuario logado
        int seguindo = userLogado.getSeguindo() + 1;
        HashMap<String,Object> dadosSeguindo = new HashMap<>();
        dadosSeguindo.put("seguindo", seguindo);
        DatabaseReference usuarioSeguindo = usuarioRef
                .child(userLogado.getId());
        usuarioSeguindo.updateChildren(dadosSeguindo);

        //incrementar seguidores amigo
        int seguidores = userAmigo.getSeguidores() + 1;
        HashMap<String,Object> dadosSeguidores = new HashMap<>();
        dadosSeguidores.put("seguidores", seguidores);
        DatabaseReference amigoSeguidores = usuarioRef
                .child(userAmigo.getId());
        amigoSeguidores.updateChildren(dadosSeguidores);
    }

    @Override
    protected void onStart() {
        super.onStart();

        recuperarDadosPerfilAmigo();

        //recuperar os dados do usuario logado
        recuperarDadosUsuarioLogado();
    }

    @Override
    protected void onStop() {
        super.onStop();
        usuarioAmigoRef.removeEventListener(valueEventListenerPerfilAmigo);
    }

    private void recuperarDadosPerfilAmigo(){
        usuarioAmigoRef = usuarioRef.child(usuarioSelecionado.getId());
        valueEventListenerPerfilAmigo = usuarioAmigoRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Usuario usuario =  dataSnapshot.getValue(Usuario.class);
                        String postagens = String.valueOf(usuario.getPostagens());
                        String seguindo = String.valueOf(usuario.getSeguindo());
                        String seguidores = String.valueOf(usuario.getSeguidores());

                        //aplica valores recuperados
                        textPostagens.setText(postagens);
                        textSeguidores.setText(seguidores);
                        textSeguindo.setText(seguindo);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }




    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
