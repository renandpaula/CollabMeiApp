package br.edu.ucsal.colabmeiapp.model;

public class Feed {

    private String id;
    private String fotoPublicacao;
    private String descricao;
    private String nomeUsuario;
    private String fotoUsuario;

    public Feed() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFotoPublicacao() {
        return fotoPublicacao;
    }

    public void setFotoPublicacao(String fotoPublicacao) {
        this.fotoPublicacao = fotoPublicacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getFotoUsuario() {
        return fotoUsuario;
    }

    public void setFotoUsuario(String fotoUsuario) {
        this.fotoUsuario = fotoUsuario;
    }
}
