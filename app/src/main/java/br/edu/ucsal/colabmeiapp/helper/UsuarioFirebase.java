package br.edu.ucsal.colabmeiapp.helper;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import br.edu.ucsal.colabmeiapp.config.FirebaseConfig;

public class UsuarioFirebase {

    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = FirebaseConfig.getFirebaseAutenticacao();
        return usuario.getCurrentUser();
    }

    public static void atualizarNomeUsuario(String nome){

        try {

            //usuario logado
            FirebaseUser usuarioLogado = getUsuarioAtual();

            //configura objeto para alterar perfil
            UserProfileChangeRequest profile =  new UserProfileChangeRequest
                    .Builder()
                    .setDisplayName(nome)
                    .build();

            usuarioLogado.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()){
                        Log.d("Perfil", "Erro ao atuyalizar nome de perfil");
                    } else{

                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
