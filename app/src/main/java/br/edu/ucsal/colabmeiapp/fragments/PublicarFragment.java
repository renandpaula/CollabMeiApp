package br.edu.ucsal.colabmeiapp.fragments;


import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;

import br.edu.ucsal.colabmeiapp.R;
import br.edu.ucsal.colabmeiapp.activity.CadastrarAnuncioActivity;
import br.edu.ucsal.colabmeiapp.activity.FiltroActivity;
import br.edu.ucsal.colabmeiapp.helper.Permissoes;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class PublicarFragment extends Fragment {
    private Button buttonNovaPostagem, buttonNovoAnuncio;
//    private static final int SELECAO_CAMERA = 100;
//    private static final int SELECAO_GALERIA = 200;
//    private CropImageView cropImageView;

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
        buttonNovoAnuncio = view.findViewById(R.id.button_novaPostagem);
        buttonNovaPostagem = view.findViewById(R.id.button_novoAnuncio);

        //add evento de click nos botoes;
        buttonNovoAnuncio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CadastrarAnuncioActivity.class));
            }
        });

        buttonNovaPostagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.startPickImageActivity(getActivity());
            }
        });

        return view;
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .start(getActivity());

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap imagem = null;

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(getActivity(), data);


            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(getActivity(), imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                Uri mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},   CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                // no permissions required or already granted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                imagem = result.getBitmap();

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


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


    }




}
