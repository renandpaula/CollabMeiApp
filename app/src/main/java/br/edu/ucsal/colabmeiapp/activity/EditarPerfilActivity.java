package br.edu.ucsal.colabmeiapp.activity;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.santalu.maskedittext.MaskEditText;

import br.edu.ucsal.colabmeiapp.R;
import br.edu.ucsal.colabmeiapp.config.FirebaseConfig;
import br.edu.ucsal.colabmeiapp.model.Usuario;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditarPerfilActivity extends AppCompatActivity {

    private TextInputEditText campoNome, campoRazao, campoEndereco, campoEmail;
    private MaskEditText campoCPF, campoCNPJ, campoTelefone;
    private TextView alterarFoto;
    private CircleImageView imageEditarPerfil;
    private Button buttonAtualizarPerfil;
    private DatabaseReference usuarioRef;
    private Usuario usuarioRecuperado;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Editar Perfil:");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_azul);

        //inicia componentes
        inicializarComponentes();
        autenticacao = FirebaseConfig.getFirebaseAutenticacao();
        usuarioRef =  FirebaseConfig.getFirebaseDatabase()
                .child("usuarios")
                .child(autenticacao.getUid());

        //recuperar dados do usuario
        recuperaUsuario();

        if (usuarioRecuperado != null ){

            if(usuarioRecuperado.getTipo().equals("PJ")){ //Usuario PJ

                //Altera a visibilidade dos elementos
                campoNome.setVisibility(View.GONE);
                campoRazao.setVisibility(View.VISIBLE);
                campoCPF.setVisibility(View.GONE);
                campoCNPJ.setVisibility(View.VISIBLE);
                campoRazao.requestFocus();

                //Aplica os valores recuperados
                campoRazao.setText(usuarioRecuperado.getNomeXrazao());
                campoEndereco .setText(usuarioRecuperado.getEndereco());
                campoEmail.setText(usuarioRecuperado.getEmail());
                campoCNPJ.setText(usuarioRecuperado.getCpfXcnpj());
                campoTelefone.setText(usuarioRecuperado.getTelefone());

            } else { //Usuario PF

                //Altera a visibilidade dos elementos
                campoNome.setVisibility(View.VISIBLE);
                campoRazao.setVisibility(View.GONE);
                campoCPF.setVisibility(View.VISIBLE);
                campoCNPJ.setVisibility(View.GONE);
                campoNome.requestFocus();

                //Aplica os valores recuperados
                campoNome.setText(usuarioRecuperado.getNomeXrazao());
                campoEndereco .setText(usuarioRecuperado.getEndereco());
                campoEmail.setText(usuarioRecuperado.getEmail());
                campoCPF.setText(usuarioRecuperado.getCpfXcnpj());
                campoTelefone.setText(usuarioRecuperado.getTelefone());
            }

//            imageEditarPerfil;

        }


    }

    private void inicializarComponentes(){
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

    private void recuperaUsuario(){


        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                usuarioRecuperado = usuario;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ERRO:", "Erro ao tentar recuperar usuário");
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return false;
    }
}
