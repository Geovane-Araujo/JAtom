package com.pain_crud.model;

import com.pain_crud.*;

import java.util.List;
@Alias(value="pessoa_pessoa")
public class Pessoa {

    @Ignore
    private boolean add = false;
    @Id
    private int idpessoa;
    private String nome;
    private int idade;
    @ObjectLocal
    private Telefones tel;
    @ListObjectLocal
    private List<Telefones> telw;

    public Pessoa() {
    }

    public Pessoa(String nome, int idade) {
        this.nome = nome;
        this.idade = idade;
    }

    public boolean isAdd() {
        return add;
    }

    public void setAdd(boolean add) {
        this.add = add;
    }

    public int getIdpessoa() {
        return idpessoa;
    }

    public void setIdpessoa(int idpessoa) {
        this.idpessoa = idpessoa;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public Telefones getTel() {
        return tel;
    }

    public void setTel(Telefones tel) {
        this.tel = tel;
    }

    public List<Telefones> getTelw() {
        return telw;
    }

    public void setTelw(List<Telefones> telw) {
        this.telw = telw;
    }
}
