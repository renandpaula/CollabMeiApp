package br.edu.ucsal.colabmeiapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santalu.maskedittext.MaskEditText;

import java.io.ByteArrayOutputStream;

import br.edu.ucsal.colabmeiapp.R;
import br.edu.ucsal.colabmeiapp.config.FirebaseConfig;
import br.edu.ucsal.colabmeiapp.helper.CpfCnpjUtils;
import br.edu.ucsal.colabmeiapp.helper.UsuarioFirebase;
import br.edu.ucsal.colabmeiapp.model.Usuario;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditarPerfilActivity extends AppCompatActivity {

    private TextInputEditText campoNome, campoRazao, campoEndereco, campoEmail;
    private MaskEditText campoCPF, campoCNPJ, campoTelefone;
    private TextView alterarFoto;
    private CircleImageView imageEditarPerfil;
    private Button buttonAtualizarPerfil;
    private DatabaseReference usuarioRef;
    private Usuario usuarioLogado;
    private Usuario usuarioParaEditar;
    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageRef;
    private String identificadorUsuario;
    private TextInputLayout editLayout_campoNome, editLayout_campoRazao, editLayout_campoCPF, editLayout_campoCNPJ;
    String pessoaJuridica = "PJ";
    String pessoaFisica = "PF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        usuarioLogado = UsuarioFirebase.getDadosUsuariologado();
        storageRef = FirebaseConfig.getFirebaseStorage();
        identificadorUsuario = FirebaseConfig.getIdUsuario();

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Editar Perfil:");
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_azul);

        //inicia componentes
        inicializarComponentes();
        usuarioRef =  FirebaseConfig.getFirebaseDatabase()
                .child("usuarios");

        //recuperar dados do usuario
        recuperaPorId(identificadorUsuario);
        final FirebaseUser usuarioPerfil = FirebaseConfig.getUsuarioAtual();

        Uri url = usuarioPerfil.getPhotoUrl();
        if(url != null){
            Glide.with(EditarPerfilActivity.this)
            .load(url)
            .into(imageEditarPerfil);
        }else{
            imageEditarPerfil.setImageResource(R.drawable.avatar);
        }

        //Salvar alteracoes do usuario
        buttonAtualizarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //recupera os textos editados
                String nomeAtual = campoNome.getText().toString();
                String razaoAtual = campoRazao.getText().toString();
                String cpfAtual = campoCPF.getRawText();
                String cnpjAtual = campoCNPJ.getRawText();
                String telefoneAtual = campoTelefone.getRawText();
                String enderecoAtual = campoEndereco.getText().toString();

                //seta valores comuns
                usuarioLogado.setTelefone(telefoneAtual);
                usuarioLogado.setEndereco(enderecoAtual);

                //verifica se eh pj ou pf e seta valores de acordo
                if(usuarioParaEditar.getTipo().equals("PJ")){ //Usuario tipo PJ
                    usuarioLogado.setTipo(pessoaJuridica);
                    usuarioLogado.setNomeXrazao(razaoAtual);
                    boolean cnpjValido = CpfCnpjUtils.isValid(cnpjAtual); //verifica se o cnpj digitado é valido

                    if (cnpjValido){
                        usuarioLogado.setCpfXcnpj(cnpjAtual);

                        //Atualizar nome no perfil do firebase
                        UsuarioFirebase.atualizarNomeUsuario(razaoAtual);

                        //Atualizar nome no banco
                        usuarioLogado.atualizar();
                        finish();
                        Toast.makeText(EditarPerfilActivity.this,
                                "Dados atualizados com sucesso!",
                                Toast.LENGTH_SHORT).show();
//                        loading.setVisibility(View.VISIBLE);
                    } else {
                        campoCNPJ.requestFocus();
                        Toast.makeText(EditarPerfilActivity.this,
                                "CNPJ Inválido!",
                                Toast.LENGTH_SHORT).show();
                    }

                } else { //Usuario tipo PF
                    usuarioLogado.setTipo(pessoaFisica);
                    usuarioLogado.setNomeXrazao(nomeAtual);
                    boolean cpfValido = CpfCnpjUtils.isValid(cpfAtual);  //verifica se o cpf digitado é valido

                    if (cpfValido){
                        usuarioLogado.setCpfXcnpj(cpfAtual);

                        //Atualizar nome no perfil do firebase
                        UsuarioFirebase.atualizarNomeUsuario(nomeAtual);

                        //Atualizar nome no banco
                        usuarioLogado.atualizar();
                        finish();
                        Toast.makeText(EditarPerfilActivity.this,
                                "Dados atualizados com sucesso!",
                                Toast.LENGTH_SHORT).show();
//                        loading.setVisibility(View.VISIBLE);
                    } else {
                        campoCPF.requestFocus();
                        Toast.makeText(EditarPerfilActivity.this,
                                "CPF Inválido!",
                                Toast.LENGTH_SHORT).show();
                    }
                }




            }
        });

        //Altera foto do usuario
        alterarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i, SELECAO_GALERIA);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Bitmap imagem = null;
            try {

                //Selecao apenas da galeria
                switch (requestCode){
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                        break;
                }

                //Caso tenha sido escolhido uma imagem
                if (imagem != null){

                    //configura imagem na tela
                    imageEditarPerfil.setImageBitmap(imagem);

                    //recupera dados da imagem
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress((Bitmap.CompressFormat.JPEG), 70, baos);
                    byte[] dadosImagem =  baos.toByteArray();

                    //salvar no firebase
                    StorageReference imagemRef =  storageRef
                            .child("imagens")
                            .child("perfil")
                            .child(identificadorUsuario+".jpeg");
                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditarPerfilActivity.this,
                                    "Erro ao fazer upload da imagem!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            //Recupera local da foto
                            Uri url = taskSnapshot.getDownloadUrl();
                            atualizarFotoUsuario(url);
                            Toast.makeText(EditarPerfilActivity.this,
                                    "Sucesso ao fazer upload da imagem!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    private void inicializarComponentes(){
        editLayout_campoNome = findViewById(R.id.editLayout_campoNome);
        editLayout_campoRazao = findViewById(R.id.editLayout_campoRazao);
        editLayout_campoCPF = findViewById(R.id.editLayout_campoCPF);
        editLayout_campoCNPJ = findViewById(R.id.editLayout_campoCNPJ);
        campoNome = findViewById(R.id.edit_TextNome);
        campoRazao = findViewById(R.id.edit_TextRazaoSocial);
        campoEndereco = findViewById(R.id.edit_TextEndereco);
        campoEmail = findViewById(R.id.edit_TextEmail);
        campoCPF = findViewById(R.id.edit_TextCPF);
        campoCNPJ = findViewById(R.id.edit_TextCNPJ);
        campoTelefone = findViewById(R.id.edit_TextTelefone);
        alterarFoto = findViewById(R.id.edit_textView_alterarFoto);
        imageEditarPerfil = findViewById(R.id.edit_imageView_editarPerfil);
        buttonAtualizarPerfil = findViewById(R.id.edit_btnEditar);
        campoEmail.setFocusable(false);

    }

    private void atualizarFotoUsuario(Uri url){
        //atualizar foto no perfil
        UsuarioFirebase.atualizarFotoUsuario(url);

        //atualizar foto no firebase
        usuarioLogado.setCaminhoFoto(url.toString());
        usuarioLogado.atualizar();
        Toast.makeText(EditarPerfilActivity.this,
                "Foto de perfil atualizada!",
                Toast.LENGTH_SHORT).show();


    }

    private void recuperaPorId(final String idUsuario){
        Query query = usuarioRef.orderByChild("id")
                .startAt(idUsuario);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    if (ds.child("id").getValue().toString().equals(idUsuario)){
                        Usuario recuperado = ds.getValue(Usuario.class);
                        onUsuarioRecuperado(recuperado);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void onUsuarioRecuperado(Usuario usuarioRecuperado){
        Usuario usuarioPerfil = usuarioRecuperado;
        usuarioParaEditar = usuarioRecuperado;


        if (usuarioPerfil != null ){

            if(usuarioPerfil.getTipo().equals("PJ")){ //Usuario PJ

                //Altera a visibilidade dos elementos
                editLayout_campoNome.setVisibility(View.GONE);
                editLayout_campoRazao.setVisibility(View.VISIBLE);
                editLayout_campoCPF.setVisibility(View.GONE);
                editLayout_campoCNPJ.setVisibility(View.VISIBLE);
                campoRazao.requestFocus();

                //Aplica os valores recuperados
                campoRazao.setText(usuarioPerfil.getNomeXrazao());
                campoEndereco .setText(usuarioPerfil.getEndereco());
                campoEmail.setText(usuarioPerfil.getEmail());
                campoCNPJ.setText(usuarioPerfil.getCpfXcnpj());
                campoTelefone.setText(usuarioPerfil.getTelefone());

            } else { //Usuario PF

                //Altera a visibilidade dos elementos
                editLayout_campoNome.setVisibility(View.VISIBLE);
                editLayout_campoRazao.setVisibility(View.GONE);
                editLayout_campoCPF.setVisibility(View.VISIBLE);
                editLayout_campoCNPJ.setVisibility(View.GONE);
                campoNome.requestFocus();

                //Aplica os valores recuperados
                campoNome.setText(usuarioPerfil.getNomeXrazao());
                campoEndereco .setText(usuarioPerfil.getEndereco());
                campoEmail.setText(usuarioPerfil.getEmail());
                campoCPF.setText(usuarioPerfil.getCpfXcnpj());
                campoTelefone.setText(usuarioPerfil.getTelefone());
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
