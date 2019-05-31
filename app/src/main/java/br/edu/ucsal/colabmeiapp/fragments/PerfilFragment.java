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
import com.google.firebase.auth.FirebaseUser;

import br.edu.ucsal.colabmeiapp.R;
import br.edu.ucsal.colabmeiapp.activity.EditarPerfilActivity;
import br.edu.ucsal.colabmeiapp.config.FirebaseConfig;
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


    public PerfilFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        FirebaseUser usuarioPerfil = FirebaseConfig.getUsuarioAtual();

        //Configurando os componentes
        gridViewPerfil = view.findViewById(R.id.gridview_perfil);
        progressBar = view.findViewById(R.id.progressBarPerfil);
        imagePerfil = view.findViewById(R.id.perfil_image);
        textPublicacoes = view.findViewById(R.id.textView_Publicacoes);
        textSeguidores =  view.findViewById(R.id.textView_Seguidores);
        textSeguindo = view.findViewById(R.id.textView_Seguindo);
        buttonAcaoPerfil = view.findViewById(R.id.buttonAcaoPerfil);

        //Abrir edicao do perfil
        buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), EditarPerfilActivity.class);
                startActivity(i);
            }
        });

        Uri url = usuarioPerfil.getPhotoUrl();
        if(url != null){
            Glide.with(getActivity())
                    .load(url)
                    .into(imagePerfil);
        }else{
            imagePerfil.setImageResource(R.drawable.avatar);
        }

        return view;
    }

}
