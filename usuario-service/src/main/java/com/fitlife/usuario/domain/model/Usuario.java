package com.fitlife.usuario.domain.model;

public class Usuario {
    private long id;
    private String nome;
    private String data_nasc;
    private String cpf;
    private String genero;
    private String telefone;
    private String endereco;
    private String email;
    private String senha;

    public Usuario() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getData_nasc() { return data_nasc; }
    public void setData_nasc(String data_nasc) { this.data_nasc = data_nasc; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}
