package br.edu.ucsal.colabmeiapp.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santalu.maskedittext.MaskEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.edu.ucsal.colabmeiapp.R;
import br.edu.ucsal.colabmeiapp.config.FirebaseConfig;
import br.edu.ucsal.colabmeiapp.helper.Permissoes;
import br.edu.ucsal.colabmeiapp.model.Anuncio;
import dmax.dialog.SpotsDialog;

public class CadastrarAnuncioActivity extends AppCompatActivity
            implements View.OnClickListener {

    private EditText campoTitulo, campoCidade, campoBairro, campoDescricao;
    private CurrencyEditText campoValor;
    private MaskEditText campoTelefone;
    private ImageView fotoAnuncio1, fotoAnuncio2, fotoAnuncio3;
    private Spinner spinnerRegiao, spinnerCategoria;
    private Anuncio anuncio;
    private StorageReference storage;
    private AlertDialog dialog;

    private String[] permissoes =  new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    private List<String> listaFotosRecuperadas = new ArrayList<>();
    private List<String> listaURLFotos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncio);

        getSupportActionBar().setTitle("Novo Anúncio");

        //Configurações iniciais
        storage = FirebaseConfig.getFirebaseStorage();

        //Validar permissões
        Permissoes.validarPermissoes(permissoes, this, 1);

        inicializarComponentes();
        carregarDadosSpinner();


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.cadastroAnuncio_imageView1 :
                escolherImagem(1);
                break;

            case R.id.cadastroAnuncio_imageView2 :
                escolherImagem(2);
                break;

            case R.id.cadastroAnuncio_imageView3 :
                escolherImagem(3);
                break;
        }

    }

    public void escolherImagem(int requestCode){
        Intent i =  new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, requestCode);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


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

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Salvando Anúncio")
                .setCancelable(false)
                .build();
        dialog.show();

        /**
         * Salvar imagens no Storage
         */
        for (int i = 0; i < listaFotosRecuperadas.size(); i++){
            String urlImagem = listaFotosRecuperadas.get(i);
            int tamanhoLista = listaFotosRecuperadas.size();
            salvarFotoStorage(urlImagem, tamanhoLista, i);
        }

    }

    private void salvarFotoStorage(String urlString, final int totalFotos, int contador){

        //Criar nó no Storage
        StorageReference imagemAnuncio = storage.child("imagens")
                .child("anuncios")
                .child(anuncio.getIdAnuncio())
                .child("imagem"+contador);

        //Fazer upload do arquivo
        UploadTask uploadTask = imagemAnuncio.putFile(Uri.parse(urlString));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Uri firebaseUrl = taskSnapshot.getDownloadUrl();
                String urlConvertida = firebaseUrl.toString();

                listaURLFotos.add(urlConvertida);

                if ( totalFotos == listaURLFotos.size()){
                    anuncio.setFotos(listaURLFotos);
                    anuncio.salvar();
                    dialog.dismiss();
                    finish();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                exibirMensagemErro("Falha ao fazer upload da imagem");
                Log.i("INFO", "Falha ao fazer upload: "+ e.getMessage());

            }
        });


    }

    private Anuncio configurarAnuncio(){
        String regiao = spinnerRegiao.getSelectedItem().toString();
        String categoria = spinnerCategoria.getSelectedItem().toString();
        String cidade = campoCidade.getText().toString() ;
        String bairro = campoBairro.getText().toString() ;
        String titulo = campoTitulo.getText().toString();
        String valor = campoValor.getText().toString();
        String telefone = campoTelefone.getText().toString() ;
        String descricao = campoDescricao.getText().toString() ;

        Anuncio anuncio = new Anuncio();
        anuncio.setRegiao(regiao);
        anuncio.setCategoria(categoria);
        anuncio.setCidade(cidade);
        anuncio.setBairro(bairro);
        anuncio.setTitulo(titulo);
        anuncio.setValor(valor);
        anuncio.setTelefone(telefone);
        anuncio.setDescricao(descricao);

        return anuncio;

    }

    public void validarDadosAnuncio(View view){

        anuncio  = configurarAnuncio();
        String valor = String.valueOf(campoValor.getRawValue());
        String fone = "";
        if(campoTelefone.getRawText() != null){
            fone =  campoTelefone.getRawText().toString();
        }

        if(listaFotosRecuperadas.size() != 0){
            if(!anuncio.getRegiao().equals("Região:")){
                if(!anuncio.getCategoria().equals("Categoria:")){
                    if(!anuncio.getCidade().isEmpty()){
                        if(!anuncio.getBairro().isEmpty()){
                            if(!anuncio.getTitulo().isEmpty()){
                                if(!valor.isEmpty() && !valor.equals("0")){
                                    if(!anuncio.getTelefone().isEmpty() && fone.length() >=10){
                                        if(!anuncio.getDescricao().isEmpty()){
                                            salvarAnuncio();
                                        } else {
                                            exibirMensagemErro("Preencha a descrição!");
                                        }
                                    } else {
                                        exibirMensagemErro("O telefone deve conter ao menos 10 números!");
                                    }
                                } else {
                                    exibirMensagemErro("Preencha o valor!");
                                }
                            } else {
                                exibirMensagemErro("Preencha o título!");
                            }
                        } else {
                            exibirMensagemErro("Preencha o bairro!");
                        }
                    } else {
                        exibirMensagemErro("Preencha a cidade!");
                    }
                } else {
                    exibirMensagemErro("Selecione a categoria!");
                }
            } else {
                exibirMensagemErro("Selecione a região!");
            }
        }else {
            exibirMensagemErro("Selecione ao menos uma foto!");
        }
    }
    
    private void exibirMensagemErro(String mensagem){
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }

    private void carregarDadosSpinner(){
        //Spinner de estados
        String[] regiao = getResources().getStringArray(R.array.regiao);
        ArrayAdapter<String> adapterR = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,
                regiao
        );
        adapterR.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRegiao.setAdapter(adapterR);

        //Spinner de categorias
        String[] categoria = getResources().getStringArray(R.array.categorias);
        ArrayAdapter<String> adapterC = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,
                categoria
        );
        adapterC.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapterC);

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
