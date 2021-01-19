package com.pain.model;

import com.pain.anotacoes.Id;
import com.pain.anotacoes.ListObjectLocal;
import com.pain.anotacoes.ObjectLocal;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

public class ModelTeste {
    @Id
    private int id;
    private String nome;
    private Timestamp dataCadastro;
    private double limite;
    private Date dataNascimento;
    private float credito;
    private long chave;
    private boolean ativo;
    @ListObjectLocal
    List<Telefones> telefones;
    @ObjectLocal
    Enderecos enderecos;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Timestamp getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Timestamp dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public double getLimite() {
        return limite;
    }

    public void setLimite(double limite) {
        this.limite = limite;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public float getCredito() {
        return credito;
    }

    public void setCredito(float credito) {
        this.credito = credito;
    }

    public long getChave() {
        return chave;
    }

    public void setChave(long chave) {
        this.chave = chave;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public List<Telefones> getTelefones() {
        return telefones;
    }

    public void setTelefones(List<Telefones> telefones) {
        this.telefones = telefones;
    }

    public Enderecos getEnderecos() {
        return enderecos;
    }

    public void setEnderecos(Enderecos enderecos) {
        this.enderecos = enderecos;
    }
}
