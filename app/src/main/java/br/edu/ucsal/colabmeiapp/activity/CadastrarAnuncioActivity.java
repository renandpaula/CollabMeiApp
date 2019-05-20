package br.edu.ucsal.colabmeiapp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.mvc.imagepicker.ImagePicker;
import com.santalu.maskedittext.MaskEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.edu.ucsal.colabmeiapp.R;
import br.edu.ucsal.colabmeiapp.helper.Permissoes;

public class CadastrarAnuncioActivity extends AppCompatActivity
            implements View.OnClickListener {

    private EditText campoTitulo, campoCidade, campoBairro, campoDescricao;
    private CurrencyEditText campoValor;
    private MaskEditText campoTelefone;
    private ImageView fotoAnuncio1, fotoAnuncio2, fotoAnuncio3;
    private Spinner spinnerRegiao, spinnerCategoria;

    private String[] permissoes =  new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private List<String> listaFotosRecuperadas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncio);

        //Validar permissões
        Permissoes.validarPermissoes(permissoes, this, 1);
        ImagePicker.setMinQuality(600,600);

        inicializarComponentes();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.cadastroAnuncio_imageView1 :
                ImagePicker.pickImage(this, "Selecione a imagem", 1, false);
                break;

            case R.id.cadastroAnuncio_imageView2 :
                ImagePicker.pickImage(this, "Selecione a imagem", 2, false);
                break;

            case R.id.cadastroAnuncio_imageView3 :
                ImagePicker.pickImage(this, "Selecione a imagem", 3, false);
                break;
        }

    }

//    public void escolherImagem(int requestCode, int tipoRecurso){
//        switch (tipoRecurso) {
//
//            case 1 : //Uso recurso da galeria
//                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(i, requestCode);
//                break;
////
//            case 2 : //Uso recurso da camera
//                Intent e = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(e, requestCode);
//                break;
//        }
//
//
//
//    }

    public void onPickImage(View v) {
        // Click on image button

        switch (v.getId()) {

            case R.id.cadastroAnuncio_imageView1 :
                ImagePicker.pickImage(this, "Selecione a imagem", 1, false);
                break;

            case R.id.cadastroAnuncio_imageView2 :
                ImagePicker.pickImage(this, "Selecione a imagem", 2, false);
                break;

            case R.id.cadastroAnuncio_imageView3 :
                ImagePicker.pickImage(this, "Selecione a imagem", 3, false);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, data);

        if ( resultCode == Activity.RESULT_OK){

            //Recuperar imagem
            Uri imagemSelecionada = data.getData();
            String caminhoImagem = imagemSelecionada.toString();

            //Configura imagem no ImageView
            if (requestCode == 1){
                fotoAnuncio1.setImageURI(imagemSelecionada);
            } else if (requestCode == 2) {
                fotoAnuncio2.setImageURI(imagemSelecionada);
            } else if (requestCode == 3){
                fotoAnuncio3.setImageURI(imagemSelecionada);
            }

            listaFotosRecuperadas.add(caminhoImagem);
        }
    }

    public void salvarAnuncio(){

    }

    private void inicializarComponentes(){

        campoTitulo = findViewById(R.id.cadastroAnuncio_editText_titulo);
        campoValor = findViewById(R.id.cadastroAnuncio_editText_valor);
        campoTelefone = findViewById(R.id.cadastroAnuncio_editText_telefone);
        campoCidade = findViewById(R.id.cadastroAnuncio_editText_cidade);
        campoBairro = findViewById(R.id.cadastroAnuncio_editText_bairro);
        campoDescricao = findViewById(R.id.cadastroAnuncio_editText_descricao);
        spinnerCategoria = findViewById(R.id.cadastroAnuncio_spinner_categoria);
        spinnerRegiao = findViewById(R.id.cadastroAnuncio_spinner_regiao);
        fotoAnuncio1 = findViewById(R.id.cadastroAnuncio_imageView1);
        fotoAnuncio2 = findViewById(R.id.cadastroAnuncio_imageView2);
        fotoAnuncio3 = findViewById(R.id.cadastroAnuncio_imageView3);
        fotoAnuncio1.setOnClickListener(this);
        fotoAnuncio2.setOnClickListener(this);
        fotoAnuncio3.setOnClickListener(this);


        Locale locale = new Locale("pt", "BR");
        campoValor.setLocale(locale);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado : grantResults){
            if (permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }
        }
    }

    private void alertaValidacaoPermissao(){
        AlertDialog.Builder builder =  new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões.");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
