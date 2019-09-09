package br.edu.ucsal.colabmeiapp.activity;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import br.edu.ucsal.colabmeiapp.R;
import br.edu.ucsal.colabmeiapp.adapter.AdapterThumbnails;
import br.edu.ucsal.colabmeiapp.config.FirebaseConfig;
import br.edu.ucsal.colabmeiapp.helper.RecyclerItemClickListener;
import br.edu.ucsal.colabmeiapp.model.Publicacao;
import br.edu.ucsal.colabmeiapp.model.Usuario;
import dmax.dialog.SpotsDialog;

public class FiltroActivity extends AppCompatActivity {

    static {
        System.loadLibrary("NativeImageProcessor");
    }

    private ImageView imageFotoEscolhida;
    private Bitmap imagem;
    private Bitmap imagemFiltro;
    private List<ThumbnailItem> listaFiltros;
    private RecyclerView recyclerFiltros;
    private AdapterThumbnails adapterThumbnails;
    private String idUsuarioLogado;
    private TextInputEditText textDescricaoFiltro;
    private AlertDialog dialog;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference firebaseRef;
    private Usuario usuarioLogado;
    private boolean estaCarregado;
    private ProgressBar progressBarFiltro;
    private DataSnapshot seguidoresSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);

        //configs iniciais
        listaFiltros = new ArrayList<>();
        firebaseRef = FirebaseConfig.getFirebaseDatabase();
        usuariosRef = firebaseRef.child("usuarios");
        idUsuarioLogado = FirebaseConfig.getIdUsuario();


        //inicializar componentes
        imageFotoEscolhida = findViewById(R.id.imageFotoEscolhida);
        recyclerFiltros = findViewById(R.id.recyclerFiltros);
        textDescricaoFiltro = findViewById(R.id.textDescricaoFiltro);
        progressBarFiltro = findViewById(R.id.progressBarFiltro);

        //recupera dados para uma nova postagem
        recuperarDadosPostagem();

        //configura toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Filtros");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_azul);

        //recupera imagem escolhida
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            byte[] dadosImagem = bundle.getByteArray("fotoEscolhida");
            imagem = BitmapFactory.decodeByteArray(dadosImagem, 0, dadosImagem.length);
            imageFotoEscolhida.setImageBitmap(imagem);
            imagemFiltro = imagem.copy(imagem.getConfig(), true);

            //configura recycler view
            adapterThumbnails = new AdapterThumbnails(listaFiltros, getApplicationContext());
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerFiltros.setLayoutManager(layoutManager);
            recyclerFiltros.setAdapter(adapterThumbnails);

            //add evento de clique no recyclerview
            recyclerFiltros.addOnItemTouchListener(
                    new RecyclerItemClickListener(
                            getApplicationContext(),
                            recyclerFiltros,
                            new RecyclerItemClickListener.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    ThumbnailItem item = listaFiltros.get(position);

                                    imagemFiltro = imagem.copy(imagem.getConfig(), true);
                                    Filter filtro = item.filter;
                                    imageFotoEscolhida.setImageBitmap(filtro.processFilter(imagemFiltro));
                                }

                                @Override
                                public void onLongItemClick(View view, int position) {

                                }

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                }
                            }
                    )
            );

            //recupera filtros
            recuperarFiltros();

        }
    }

    private void recuperarDadosPostagem() {
        carregando(true);
        usuarioLogadoRef = usuariosRef.child(idUsuarioLogado);
        usuarioLogadoRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //Recupera os dados do usuario logado
                        usuarioLogado = dataSnapshot.getValue(Usuario.class);

                        //Recupera seguidores
                        DatabaseReference seguidoresRef = firebaseRef
                                .child("seguidores")
                                .child( idUsuarioLogado );
                        seguidoresRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                seguidoresSnapshot = dataSnapshot;
                                carregando(false);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        carregando(false);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
    }

    private void recuperarFiltros() {

        //limpar itens
        ThumbnailsManager.clearThumbs();
        listaFiltros.clear();

        //configurar filtro normal
        ThumbnailItem item = new ThumbnailItem();
        item.image = imagem;
        item.filterName = "Normal";
        ThumbnailsManager.addThumb(item);

        //listar todos os filtros
        List<Filter> filtros = FilterPack.getFilterPack(getApplicationContext());
        for (Filter filtro : filtros) {
            ThumbnailItem itemFiltro = new ThumbnailItem();
            itemFiltro.image = imagem;
            itemFiltro.filter = filtro;
            itemFiltro.filterName = filtro.getName();

            ThumbnailsManager.addThumb(itemFiltro);
        }

        listaFiltros.addAll(ThumbnailsManager.processThumbs(getApplicationContext()));
        adapterThumbnails.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filtro, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_salvar_postagem:
                postarPublicacao();
        }

        return super.onOptionsItemSelected(item);
    }

    private void carregando(boolean estado) {
        if (estado) {
            estaCarregado = true;
            progressBarFiltro.setVisibility(View.VISIBLE);
        } else {
            estaCarregado = false;
            progressBarFiltro.setVisibility(View.GONE);
        }
    }

    private void postarPublicacao() {

        if (estaCarregado) {
            Toast.makeText(getApplicationContext(),
                    "Carregando dados, aguarde!",
                    Toast.LENGTH_SHORT).show();
        } else {
            dialog = new SpotsDialog.Builder()
                    .setContext(this)
                    .setMessage("Publicando...")
                    .setCancelable(false)
                    .build();
            dialog.show();

            final Publicacao publicacao = new Publicacao();
            publicacao.setIdUsuario(idUsuarioLogado);
            publicacao.setDescricao(textDescricaoFiltro.getText().toString());

            //recupera dados da imagem
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imagemFiltro.compress((Bitmap.CompressFormat.JPEG), 70, baos);
            byte[] dadosImagem = baos.toByteArray();

            //salvar no firebase
            StorageReference storageRef = FirebaseConfig.getFirebaseStorage();
            StorageReference imagemRef = storageRef
                    .child("imagens")
                    .child("publicacoes")
                    .child(publicacao.getId() + ".jpeg");

            UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(FiltroActivity.this,
                            "Erro ao publicar, tente novamente!",
                            Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    //Recupera local da foto
                    Uri url = taskSnapshot.getDownloadUrl();
                    publicacao.setCaminhoFoto(url.toString());

                    //atualizar qtd de postagens
                    int qtdPostagem = usuarioLogado.getPostagens() + 1;
                    usuarioLogado.setPostagens(qtdPostagem);
                    usuarioLogado.atualizarQtdPostagens();

                    //salvar postagem
                    if (publicacao.salvar(seguidoresSnapshot)) {

                        Toast.makeText(FiltroActivity.this,
                                "Publicação realizada!",
                                Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                        finish();
                    }
                }
            });

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
