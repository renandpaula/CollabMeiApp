package br.edu.ucsal.colabmeiapp.model;

public class Usuario {

    private String id;
    private String nomeXrazao;
    private String cpfXcnpj;
    private String endereco;
    private String telefone;
    private String email;
    private String senha;
    private String tipo;

    public Usuario() {
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
        this.nomeXrazao = nomeXrazao;
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
