package br.edu.ucsal.colabmeiapp.model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import br.edu.ucsal.colabmeiapp.config.FirebaseConfig;
import br.edu.ucsal.colabmeiapp.helper.UsuarioFirebase;

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

    public boolean salvar(DataSnapshot seguidoresSnapshot){
        Map objeto = new HashMap();
        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuariologado();

        DatabaseReference firebaseRef =  FirebaseConfig.getFirebaseDatabase();

        //Referencia para publicacao
        String combinacaoId = "/" + getIdUsuario() + "/" + getId();
        objeto.put("/publicacoes" + combinacaoId, this );

        //Referencia para feed
        for (DataSnapshot seguidores: seguidoresSnapshot.getChildren()){

            /*
            Estrutura do feed
                +feed
                    +id_seguidor<fulano>
                        +id_postagem<XX>
                            postagem<por ciclano>
             */

            String idSeguidor =  seguidores.getKey();

            //montando o objeto
            HashMap<String, Object> dadosSeguidor = new HashMap<>();
            dadosSeguidor.put("fotoPublicacao", getCaminhoFoto());
            dadosSeguidor.put("descricao", getDescricao());
            dadosSeguidor.put("idPublicacao", getId());
            dadosSeguidor.put("nomeUsuario", usuarioLogado.getNomeXrazao());
            dadosSeguidor.put("fotoUsuario", usuarioLogado.getCaminhoFoto());

            String idsAtualizacao = "/" + idSeguidor + "/" + getId();
            objeto.put("/feed" + idsAtualizacao, dadosSeguidor );

        }

        firebaseRef.updateChildren( objeto );
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
