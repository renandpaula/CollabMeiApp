package br.edu.ucsal.colabmeiapp.model;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

import br.edu.ucsal.colabmeiapp.config.FirebaseConfig;

public class Publicacao implements Serializable {

    /*
    *   Modelo de estrutura da classe no firebase
    *   publicacoes
    *       <id_usuario>
    *           <id_postagem>
    *               descricao
    *               caminhofoto
    *               idusuario
     */

    private String id;
    private String idUsuario;
    private String descricao;
    private String caminhoFoto;

    public Publicacao() {

        DatabaseReference firebaseRef = FirebaseConfig.getFirebaseDatabase();
        DatabaseReference postagenRef = firebaseRef.child("publicacoes");
        String idPostagem = postagenRef.push().getKey(); //gera id
        setId(idPostagem);
    }

    public boolean salvar(){
        DatabaseReference firebaseRef =  FirebaseConfig.getFirebaseDatabase();
        DatabaseReference postagensRef = firebaseRef.child("publicacoes")
                .child(getIdUsuario())
                .child(getId());
        postagensRef.setValue(this);
        return true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }
}
