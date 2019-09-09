package br.edu.ucsal.colabmeiapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.List;

import br.edu.ucsal.colabmeiapp.R;
import br.edu.ucsal.colabmeiapp.config.FirebaseConfig;
import br.edu.ucsal.colabmeiapp.helper.UsuarioFirebase;
import br.edu.ucsal.colabmeiapp.model.Feed;
import br.edu.ucsal.colabmeiapp.model.Publicacao;
import br.edu.ucsal.colabmeiapp.model.PublicacaoCurtida;
import br.edu.ucsal.colabmeiapp.model.Usuario;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterFeed extends RecyclerView.Adapter<AdapterFeed.MyViewHolder> {

    private List<Feed> listaFeed;
    private Context context;
    private String txtCurtidas;

    public AdapterFeed(List<Feed> listaFeed, Context context) {
        this.listaFeed = listaFeed;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_feed, parent, false);
        return new AdapterFeed.MyViewHolder((itemLista));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int position) {

        final Feed feed = listaFeed.get(position);
        final Usuario usuarioLogado = UsuarioFirebase.getDadosUsuariologado();

        //Carrega dados do feed

        Uri uriFotoUsuario = Uri.parse( feed.getFotoUsuario());
        Uri uriFotoPublicacao = Uri.parse( feed.getFotoPublicacao());

        Glide.with(context).load(uriFotoUsuario).into(myViewHolder.fotoPerfil);
        Glide.with(context).load(uriFotoPublicacao).into(myViewHolder.fotoPublicacao);

        myViewHolder.descricao.setText(feed.getDescricao());
        myViewHolder.nome.setText(feed.getNomeUsuario());

        /*
        Estrutura das publicacoes curtidas no Firebase

            postagens-curtidas
                + id_postagem
                    + qtdCurtidas
                    + id_usuario
                        nome_usuario
                        caminho_foto
         */

        //recuperar dados da publicacao curtida
        DatabaseReference curtidasRef = FirebaseConfig.getFirebaseDatabase()
                .child("postagens-curtidas")
                .child(feed.getId());
        curtidasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int qtdCurtidas = 0;
                if(dataSnapshot.hasChild("qtdCurtidas")){
                    PublicacaoCurtida publicacaoCurtida = dataSnapshot.getValue(PublicacaoCurtida.class);
                    qtdCurtidas = publicacaoCurtida.getQtdCurtidas();
                }

                //Verifica se ja foi clicado
                if (dataSnapshot.hasChild(usuarioLogado.getId())){
                    myViewHolder.likeButton.setLiked(true);
                } else {
                    myViewHolder.likeButton.setLiked(false);
                }

                //Monta objeto publicacao curtida
                final PublicacaoCurtida curtida = new PublicacaoCurtida();
                curtida.setFeed(feed);
                curtida.setUsuario(usuarioLogado);
                curtida.setQtdCurtidas(qtdCurtidas);

                //Add evento de curtir publicacao
                myViewHolder.likeButton.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                        curtida.salvar();
                        txtCurtidas = (curtida.getQtdCurtidas() + " curtidas");
                        myViewHolder.qtdCurtidas.setText(txtCurtidas);

                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        curtida.removerCurtida();
                        txtCurtidas = (curtida.getQtdCurtidas() + " curtidas");
                        myViewHolder.qtdCurtidas.setText(txtCurtidas);

                    }
                });

                txtCurtidas = (curtida.getQtdCurtidas() + " curtidas");
                myViewHolder.qtdCurtidas.setText(txtCurtidas);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return listaFeed.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView fotoPerfil;
        TextView nome, descricao, qtdCurtidas;
        ImageView fotoPublicacao, visualizarComentario;
        LikeButton likeButton;

        public MyViewHolder(View itemView){
            super(itemView);
            fotoPerfil =  itemView.findViewById(R.id.imagePerfilPublicacao);
            fotoPublicacao = itemView.findViewById(R.id.imagePublicacaoSeleciona);
            nome = itemView.findViewById(R.id.textNomePerfilPublicacao);
            qtdCurtidas = itemView.findViewById(R.id.textCurtidasPerfilPublicacao);
            descricao = itemView.findViewById(R.id.textDescricaoPerfilPublicacao);
            visualizarComentario = itemView.findViewById(R.id.imageChatAzul);
            likeButton = itemView.findViewById(R.id.likeButtonFeed);
        }
    }



}

