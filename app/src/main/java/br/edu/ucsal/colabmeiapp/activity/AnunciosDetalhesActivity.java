package br.edu.ucsal.colabmeiapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import br.edu.ucsal.colabmeiapp.R;
import br.edu.ucsal.colabmeiapp.model.Anuncio;

public class AnunciosDetalhesActivity extends AppCompatActivity {

    private TextView campoTitulo, campoCidade, campoBairro, campoDescricao, campoPreco, campoCategoria, campoRegiao;
    private CarouselView carouselView;
    private String idUsuario;
    private Anuncio anuncioSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios_detalhes);

        //configurar toobar
        getSupportActionBar().setTitle("Detalhes do Anúncio");


        //inicializar componentes da interface
        inicializarComponentes();

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
}
