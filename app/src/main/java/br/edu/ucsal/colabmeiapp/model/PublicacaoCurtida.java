package br.edu.ucsal.colabmeiapp.model;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

import br.edu.ucsal.colabmeiapp.config.FirebaseConfig;

public class PublicacaoCurtida {

    public int qtdCurtidas = 0;
    public Feed feed;
    public Usuario usuario;

    public PublicacaoCurtida() {
    }

    public void salvar(){

        DatabaseReference firebaseRef = FirebaseConfig.getFirebaseDatabase();

        //Objeto usuario
        HashMap<String, Object> dadosUsuario = new HashMap<>();
        dadosUsuario.put("nomeUsuario", usuario.getNomeXrazao());
        dadosUsuario.put("caminhoFoto", usuario.getCaminhoFoto());

        DatabaseReference pCurtidasRef = firebaseRef
                .child("postagens-curtidas")
                .child(feed.getId()) //id_postagem
                .child(usuario.getId()); //id_usuario_logado
        pCurtidasRef.setValue( dadosUsuario );

        //atualiza quantidade de curtidas
        atualizarQtd(1);

    }

    public void atualizarQtd(int valor){
        DatabaseReference firebaseRef = FirebaseConfig.getFirebaseDatabase();

        DatabaseReference pCurtidasRef = firebaseRef
                .child("postagens-curtidas")
                .child(feed.getId()) //id_postagem
                .child("qtdCurtidas");
        setQtdCurtidas(getQtdCurtidas() + valor);
        pCurtidasRef.setValue(getQtdCurtidas());
    }

    public void removerCurtida(){
        DatabaseReference firebaseRef = FirebaseConfig.getFirebaseDatabase();

        DatabaseReference pCurtidasRef = firebaseRef
                .child("postagens-curtidas")
                .child(feed.getId()) //id_postagem
                .child(usuario.getId()); //id_usuario_logado
        pCurtidasRef.removeValue();

        //atualiza quantidade de curtidas
        atualizarQtd(-1);
    }

    public int getQtdCurtidas() {
        return qtdCurtidas;
    }

    public void setQtdCurtidas(int qtdCurtidas) {
        this.qtdCurtidas = qtdCurtidas;
    }

    public Feed getFeed() {
        return feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
