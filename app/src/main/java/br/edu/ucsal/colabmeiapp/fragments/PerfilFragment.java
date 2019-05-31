package br.edu.ucsal.colabmeiapp.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
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
import java.util.List;

import br.edu.ucsal.colabmeiapp.R;
import br.edu.ucsal.colabmeiapp.activity.EditarPerfilActivity;
import br.edu.ucsal.colabmeiapp.adapter.AdapterGrid;
import br.edu.ucsal.colabmeiapp.config.FirebaseConfig;
import br.edu.ucsal.colabmeiapp.helper.UsuarioFirebase;
import br.edu.ucsal.colabmeiapp.model.Publicacao;
import br.edu.ucsal.colabmeiapp.model.Usuario;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PerfilFragment extends Fragment {

    private ProgressBar progressBar;
    private CircleImageView imagePerfil;
    public GridView gridViewPerfil;
    private TextView textPublicacoes, textSeguidores, textSeguindo;
    private Button buttonAcaoPerfil;
    private Usuario usuarioLogado;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference firebaseRef;
    private DatabaseReference postagensUsuarioRef;
    private ValueEventListener valueEventListenerPerfil;
    private AdapterGrid adapterGrid;


    public PerfilFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        //configs iniciais
        usuarioLogado = UsuarioFirebase.getDadosUsuariologado();
        firebaseRef = FirebaseConfig.getFirebaseDatabase();
        usuariosRef = firebaseRef.child("usuarios");

        //configura ref postagens usuario
        postagensUsuarioRef = FirebaseConfig.getFirebaseDatabase()
                .child("publicacoes")
                .child(usuarioLogado.getId());

        //iniciar componentes
        inicializarComponentes(view);

        //Configurando os componentes
        gridViewPerfil = view.findViewById(R.id.gridview_perfil);
        progressBar = view.findViewById(R.id.progressBarPerfil);
        imagePerfil = view.findViewById(R.id.perfil_image);
        textPublicacoes = view.findViewById(R.id.textView_Publicacoes);
        textSeguidores =  view.findViewById(R.id.textView_Seguidores);
        textSeguindo = view.findViewById(R.id.textView_Seguindo);
        buttonAcaoPerfil = view.findViewById(R.id.buttonAcaoPerfil);

        //recupera usuario logado

        //Abrir edicao do perfil
        buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), EditarPerfilActivity.class);
                startActivity(i);
            }
        });

        //iunicializa image loader
        inicializarImageLoader();

        //carrega fotos postagem
        carregaFotosPostagem();



        return view;
    }

    private void aplicaFotoPerfil(){

        String caminhoFoto = usuarioLogado.getCaminhoFoto();
        if(caminhoFoto != null){
            Uri url = Uri.parse(caminhoFoto);
            Glide.with(getActivity())
                    .load(url)
                    .into(imagePerfil);
        }else{
            imagePerfil.setImageResource(R.drawable.avatar);
        }
    }

    public void carregaFotosPostagem(){

        //recupera fotos postadas pelo user
        postagensUsuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //configura tamanho do grid
                int tamanhoGrid = getResources().getDisplayMetrics().widthPixels;
                int tamanhoImagem = tamanhoGrid / 3;
                gridViewPerfil.setColumnWidth(tamanhoImagem);

                List<String> urlFotos = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Publicacao publicacao = ds.getValue(Publicacao.class);
                    urlFotos.add(publicacao.getCaminhoFoto());
                }

                //configurar adatper
                adapterGrid = new AdapterGrid(getActivity(), R.layout.grid_publicacoes, urlFotos);
                gridViewPerfil.setAdapter(adapterGrid);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void inicializarImageLoader() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(getActivity())
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .build();
        ImageLoader.getInstance().init(config);
    }

    private void inicializarComponentes(View view){
        buttonAcaoPerfil = view.findViewById(R.id.buttonAcaoPerfil);
        buttonAcaoPerfil.setText("Editar Perfil");
        imagePerfil = view.findViewById(R.id.perfil_image);
        textPublicacoes = view.findViewById(R.id.textView_Publicacoes);
        textSeguidores =  view.findViewById(R.id.textView_Seguidores);
        textSeguindo = view.findViewById(R.id.textView_Seguindo);
        gridViewPerfil =  view.findViewById(R.id.gridview_perfil);
    }

    private void recuperarDadosDoUsuarioLogado(){
        usuarioLogadoRef = usuariosRef.child(usuarioLogado.getId());
        valueEventListenerPerfil = usuarioLogadoRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Usuario usuario =  dataSnapshot.getValue(Usuario.class);
                        String postagens = String.valueOf(usuario.getPostagens());
                        String seguindo = String.valueOf(usuario.getSeguindo());
                        String seguidores = String.valueOf(usuario.getSeguidores());

                        //aplica valores recuperados
                        textPublicacoes.setText(postagens);
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
    public void onStart() {
        super.onStart();
        aplicaFotoPerfil();
        recuperarDadosDoUsuarioLogado();
    }

    @Override
    public void onStop() {
        super.onStop();
        usuarioLogadoRef.removeEventListener(valueEventListenerPerfil);
    }
}
