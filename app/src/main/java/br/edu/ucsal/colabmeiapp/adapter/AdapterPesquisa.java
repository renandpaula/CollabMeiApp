package br.edu.ucsal.colabmeiapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import br.edu.ucsal.colabmeiapp.R;
import br.edu.ucsal.colabmeiapp.model.Usuario;
import de.hdodenhof.circleimageview.CircleImageView;

   public class AdapterPesquisa extends RecyclerView.Adapter<AdapterPesquisa.MyViewHolder>{

       private List<Usuario> listaUsuario;
       private Context context;

       public AdapterPesquisa(List<Usuario> l, Context c) {
           this.listaUsuario = l;
           this.context = c;
       }

       @NonNull
       @Override
       public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
           View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pesquisa_usuario, parent, false);
           return new MyViewHolder((itemLista));
       }

       @Override
       public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
           Usuario usuario = listaUsuario.get(position);

           holder.nome.setText(usuario.getNomeXrazao());

           if (usuario.getCaminhoFoto() != null){
               Uri uri = Uri.parse(usuario.getCaminhoFoto());
               Glide.with(context).load(uri).into(holder.foto);
           }else {
               holder.foto.setImageResource(R.drawable.avatar);
           }

       }

       @Override
       public int getItemCount() {
           return listaUsuario.size();
       }

       public class MyViewHolder extends RecyclerView.ViewHolder{

            CircleImageView foto;
            TextView nome;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                foto = itemView.findViewById(R.id.imageViewFotoPesquisa);
                nome = itemView.findViewById(R.id.textViewNomePesquisa);

            }
        }
    }

