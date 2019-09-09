package br.edu.ucsal.colabmeiapp.helper;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import br.edu.ucsal.colabmeiapp.config.FirebaseConfig;
import br.edu.ucsal.colabmeiapp.model.Usuario;

public class UsuarioFirebase {

    public static  FirebaseUser getUsuarioLogado(){
        FirebaseAuth usuario = FirebaseConfig.getFirebaseAutenticacao();
        return usuario.getCurrentUser();
    }

    public static String getIdentificadorUsuario(){
        return getUsuarioLogado().getUid();
    }



    public static void atualizarNomeUsuario(String nome){


        try {

            //usuario logado
            FirebaseUser usuarioLogado = getUsuarioLogado();

            //configura objeto para alterar perfil
            UserProfileChangeRequest profile =  new UserProfileChangeRequest
                    .Builder()
                    .setDisplayName( nome )
                    .build();
            usuarioLogado.updateProfile( profile ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()){
                        Log.i("INFO", "Erro ao atualizar nome de perfil");
                    } else {
                        Log.i("INFO", "Sucesso ao atualizar nome de perfil");
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void atualizarFotoUsuario(Uri url){

        try {

            //usuario logado
            FirebaseUser usuarioLogado = getUsuarioLogado();

            //configura objeto para alterar perfil
            UserProfileChangeRequest profile =  new UserProfileChangeRequest
                    .Builder()
                    .setPhotoUri( url )
                    .build();

            usuarioLogado.updateProfile( profile ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()){
                        Log.d("Perfil", "Erro ao atualizar foto de perfil");
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Usuario getDadosUsuariologado(){
        FirebaseUser firebaseUser = getUsuarioLogado();

        Usuario usuario = new Usuario();
        usuario.setEmail( firebaseUser.getEmail() );
        usuario.setNomeXrazao( firebaseUser.getDisplayName() );
        usuario.setId( firebaseUser.getUid() );

        if(firebaseUser.getPhotoUrl() == null){
            usuario.setCaminhoFoto("");
        }else{
            usuario.setCaminhoFoto( firebaseUser.getPhotoUrl().toString() );
        }
        return usuario;
    }

}
