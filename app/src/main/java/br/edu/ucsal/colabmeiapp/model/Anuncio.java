package br.edu.ucsal.colabmeiapp.model;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.List;

import br.edu.ucsal.colabmeiapp.config.FirebaseConfig;
import br.edu.ucsal.colabmeiapp.helper.UsuarioFirebase;

public class Anuncio implements Serializable {

    private String idAnuncio;
    private String idUsuario;
    private String regiao;
    private String categoria;
    private String cidade;
    private String bairro;
    private String titulo;
    private String valor;
    private String telefone;
    private String descricao;
    private List<String> fotos;

    public Anuncio() {
        DatabaseReference anuncioRef = FirebaseConfig.getFirebaseDatabase()
                .child("meus anuncios");
        setIdAnuncio( anuncioRef.push().getKey() );
    }

    public void salvar(){

        String idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        setIdUsuario(idUsuarioLogado);

        DatabaseReference anuncioRef = FirebaseConfig.getFirebaseDatabase()
                .child("meus anuncios");
        anuncioRef.child(getIdUsuario())
                .child(getIdAnuncio())
                .setValue(this);
        salvarAnuncioPublico();
    }

    public void salvarAnuncioPublico(){
        DatabaseReference anuncioRef = FirebaseConfig.getFirebaseDatabase()
                .child("anuncios");
        anuncioRef.child(getRegiao())
                .child(getCategoria())
                .child(getIdAnuncio())
                .setValue(this);
    }

    public void remover(){
        String idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        setIdUsuario(idUsuarioLogado);
        DatabaseReference anuncioRef = FirebaseConfig.getFirebaseDatabase()
                .child("meus anuncios")
                .child(getIdUsuario())
                .child(getIdAnuncio());
        anuncioRef.removeValue();
        removerAnuncioPublico();

    }

    public void removerAnuncioPublico(){

        DatabaseReference anuncioRef = FirebaseConfig.getFirebaseDatabase()
                .child("anuncios")
                .child(getRegiao())
                .child(getCategoria())
                .child(getIdAnuncio());
        anuncioRef.removeValue();

    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdAnuncio() {
        return idAnuncio;
    }

    public void setIdAnuncio(String idAnuncio) {
        this.idAnuncio = idAnuncio;
    }

    public String getRegiao() {
        return regiao;
    }

    public void setRegiao(String regiao) {
        this.regiao = regiao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<String> getFotos() {
        return fotos;
    }

    public void setFotos(List<String> fotos) {
        this.fotos = fotos;
    }
}
