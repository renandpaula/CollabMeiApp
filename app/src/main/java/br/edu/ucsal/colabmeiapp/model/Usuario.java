package br.edu.ucsal.colabmeiapp.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import br.edu.ucsal.colabmeiapp.config.FirebaseConfig;

public class Usuario implements Serializable {

    private String id;
    private String nomeXrazao;
    private String cpfXcnpj;
    private String endereco;
    private String telefone;
    private String email;
    private String senha; 
    private String tipo;
    private String caminhoFoto;
    private int seguidores = 0;
    private int seguindo = 0;
    private int postagens = 0;


    public Usuario() {
    }

    public Usuario(String id, String nomeXrazao, String cpfXcnpj, String endereco, String telefone, String email, String senha, String tipo) {
        this.id = id;
        this.nomeXrazao = nomeXrazao;
        this.cpfXcnpj = cpfXcnpj;
        this.endereco = endereco;
        this.telefone = telefone;
        this.email = email;
        this.senha = senha;
        this.tipo = tipo;
    }

    public void salvarNoBanco(){
        DatabaseReference firebaseRef = FirebaseConfig.getFirebaseDatabase();
        DatabaseReference usuarios = firebaseRef.child( "usuarios" ).child( getId() );

        usuarios.setValue(this);
    }

    public void atualizar(){
        DatabaseReference firebaseRef =  FirebaseConfig.getFirebaseDatabase();
        DatabaseReference usuarioRef =  firebaseRef
                .child("usuarios")
                .child(getId());

        usuarioRef.updateChildren(converterParaMap());
    }

    public Map<String, Object> converterParaMap(){
        HashMap<String, Object> usuarioMap = new HashMap<>();
//        usuarioMap.put("email", getEmail());
        usuarioMap.put("nomeXrazao", getNomeXrazao());
        usuarioMap.put("nome", getNomeXrazao());
        usuarioMap.put("cpfXcnpj", getCpfXcnpj());
        usuarioMap.put("endereco", getEndereco());
        usuarioMap.put("telefone", getTelefone());
//        usuarioMap.put("tipo", getTipo());
//        usuarioMap.put("id", getIdPostagem());
        usuarioMap.put("caminhoFoto", getCaminhoFoto());
//        usuarioMap.put("seguidores", getSeguidores());
//        usuarioMap.put("seguindo", getSeguindo());
//        usuarioMap.put("postagens", getPostagens());
        return usuarioMap;
    }

    public void atualizarQtdPostagens(){
        DatabaseReference firebaseRef =  FirebaseConfig.getFirebaseDatabase();
        DatabaseReference usuarioRef =  firebaseRef
                .child("usuarios")
                .child( getId() );

        HashMap<String, Object> dados = new HashMap<>();
        dados.put("postagens", getPostagens());

        usuarioRef.updateChildren(dados);
    }

    public int getSeguidores() {
        return seguidores;
    }

    public void setSeguidores(int seguidores) {
        this.seguidores = seguidores;
    }

    public int getSeguindo() {
        return seguindo;
    }

    public void setSeguindo(int seguindo) {
        this.seguindo = seguindo;
    }

    public int getPostagens() {
        return postagens;
    }

    public void setPostagens(int postagens) {
        this.postagens = postagens;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomeXrazao() {
        return nomeXrazao;
    }

    public void setNomeXrazao(String nomeXrazao) {
        this.nomeXrazao = nomeXrazao.toUpperCase();
    }

    public String getCpfXcnpj() {
        return cpfXcnpj;
    }

    public void setCpfXcnpj(String cpfXcnpj) {
        this.cpfXcnpj = cpfXcnpj;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
