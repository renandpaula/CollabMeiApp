package br.edu.ucsal.colabmeiapp.fragments;


import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.ByteArrayOutputStream;

import br.edu.ucsal.colabmeiapp.R;
import br.edu.ucsal.colabmeiapp.activity.FiltroActivity;
import br.edu.ucsal.colabmeiapp.helper.Permissoes;

/**
 * A simple {@link Fragment} subclass.
 */
public class PublicarFragment extends Fragment {
    private Button buttonAbrirGaleria, buttonAbrirCamera;
    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;

    private String[] permissoes =  new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA  };



    public PublicarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_publicar, container, false);

        //validar permissoes
        Permissoes.validarPermissoes(permissoes, getActivity(), 1);

        //inicializa componentes
        buttonAbrirCamera = view.findViewById(R.id.button_abrirCamera);
        buttonAbrirGaleria = view.findViewById(R.id.button_abrirGaleria);

        //add evento de click no botao camera;
        buttonAbrirCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (i.resolveActivity(getActivity().getPackageManager()) != null){
                    startActivityForResult(i, SELECAO_CAMERA);
                }
            }
        });

        buttonAbrirGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getActivity().getPackageManager()) != null){
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK){

            Bitmap imagem = null;

            try {
                //valida tipo selecao imagem
                switch (requestCode){
                    case SELECAO_CAMERA :
                        imagem = (Bitmap) data.getExtras().get("data");
                        break;

                    case SELECAO_GALERIA :
                        Uri localImagemSeleciona = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), localImagemSeleciona);
                        break;
                }

                //valida imagem selecionada
                if (imagem != null){

                    //converte imagem em byte array
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress((Bitmap.CompressFormat.JPEG), 70, baos);
                    byte[] dadosImagem =  baos.toByteArray();

                    //envia imagem escolhida para aplicacao
                    Intent i = new Intent(getActivity(), FiltroActivity.class);
                    i.putExtra("fotoEscolhida", dadosImagem);
                    startActivity(i);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
