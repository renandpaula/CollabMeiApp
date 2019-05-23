package br.edu.ucsal.colabmeiapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.edu.ucsal.colabmeiapp.R;
import br.edu.ucsal.colabmeiapp.model.Anuncio;

public class AdapterAnuncios  extends RecyclerView.Adapter<AdapterAnuncios.MyViewHolder> {

    private List<Anuncio> anuncios;
    private Context context;


    public AdapterAnuncios(List<Anuncio> anuncios, Context context) {
        this.anuncios = anuncios;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_anuncio, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {

        Anuncio anuncio = anuncios.get(position);
        myViewHolder.titulo.setText(anuncio.getTitulo());
        myViewHolder.cidade.setText(anuncio.getCidade());
        myViewHolder.valor.setText(anuncio.getValor());

        //Pega primeira imagem da lista
        List<String> urlFotos = anuncio.getFotos();
        String urlCapa = urlFotos.get(0);

        Picasso.get().load(urlCapa).into(myViewHolder.foto);

    }

    @Override
    public int getItemCount() {
        return anuncios.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView titulo;
        TextView cidade;
        TextView valor;
        ImageView foto;

        public MyViewHolder(View itemView){
            super((itemView));

            titulo =  itemView.findViewById(R.id.textView_anuncio_titulo);
            cidade =  itemView.findViewById(R.id.textView_anuncio_cidade );
            valor =  itemView.findViewById(R.id.textView_anuncio_valor);
            foto =  itemView.findViewById(R.id.imageView_anuncio);
        }
    }
}
