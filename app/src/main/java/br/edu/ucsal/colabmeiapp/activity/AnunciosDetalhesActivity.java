package br.edu.ucsal.colabmeiapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import br.edu.ucsal.colabmeiapp.R;
import br.edu.ucsal.colabmeiapp.config.FirebaseConfig;
import br.edu.ucsal.colabmeiapp.model.Anuncio;
import br.edu.ucsal.colabmeiapp.model.Usuario;

public class AnunciosDetalhesActivity extends AppCompatActivity {

    private TextView campoTitulo, campoCidade, campoBairro, campoDescricao, campoPreco, campoCategoria, campoRegiao;
    private CarouselView carouselView;
    private String idUsuario;
    private Anuncio anuncioSelecionado;
    private DatabaseReference usuarioRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios_detalhes);

        //configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Detalhes do anúncio:");
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_azul);


        //inicializar componentes da interface
        inicializarComponentes();
        usuarioRef =  FirebaseConfig.getFirebaseDatabase()
                .child("usuarios");

        //Recupera anuncio para exibicao
        anuncioSelecionado =  (Anuncio) getIntent().getSerializableExtra("anuncioSelecionado");

        if (anuncioSelecionado != null){
            campoCategoria.setText(anuncioSelecionado.getCategoria());
            campoTitulo.setText(anuncioSelecionado.getTitulo());
            campoPreco.setText(anuncioSelecionado.getValor());
            campoRegiao.setText(anuncioSelecionado.getRegiao());
            campoCidade.setText(anuncioSelecionado.getCidade());
            campoBairro.setText(anuncioSelecionado.getBairro());
            campoDescricao.setText(anuncioSelecionado.getDescricao());
            idUsuario = anuncioSelecionado.getIdUsuario();

            ImageListener imageListener = new ImageListener() {
                @Override
                public void setImageForPosition(int position, ImageView imageView) {
                    String urlString = anuncioSelecionado.getFotos().get(position);
                    Picasso.get().load(urlString).into(imageView);
                }
            };

            carouselView.setPageCount(anuncioSelecionado.getFotos().size());
            carouselView.setImageListener(imageListener);
            Log.i("INFO", "ID do Usuario: "+ idUsuario);
        }


    }

    public void visualizarTelefone(View view){
        Intent i = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", anuncioSelecionado.getTelefone(), null));
        startActivity(i);
    }

    public void abrePerfilId(View view){
        recuperaPorId(idUsuario);
    }

    private void inicializarComponentes(){
        carouselView = findViewById(R.id.detalhes_carouselView_Fotos);
        campoTitulo = findViewById(R.id.detalhes_textView_Titulo);
        campoRegiao = findViewById(R.id.detalhes_textView_Regiao);
        campoCidade = findViewById(R.id.detalhes_textView_Cidade);
        campoBairro = findViewById(R.id.detalhes_textView_Bairro);
        campoDescricao = findViewById(R.id.detalhes_textView_Descricao);
        campoPreco = findViewById(R.id.detalhes_textView_Preco);
        campoCategoria = findViewById(R.id.detalhes_textView_Categoria);

    }

    private void recuperaPorId(final String idUsuarioAnuncio) {

        Query query = usuarioRef.orderByChild("id")
                .startAt(idUsuarioAnuncio);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("id").getValue().toString().equals(idUsuarioAnuncio)) {
                        Usuario recuperado = ds.getValue(Usuario.class);
                        onUsuarioRecuperado(recuperado);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onUsuarioRecuperado(Usuario recuperado){
        Usuario usuario = recuperado;
        Intent i = new Intent(AnunciosDetalhesActivity.this, PerfilAmigoActivity.class);
        i.putExtra("usuarioSelecionado", usuario);
        startActivity(i);
    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return false;
    }
}
