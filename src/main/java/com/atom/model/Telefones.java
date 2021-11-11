package com.atom.model;

import com.atom.anotations.Fk;
import com.atom.anotations.Id;

public class Telefones {

    @Id
    private int idTelefone;

    private String telefone;

    @Fk(value="idpessoa")
    private int idpessoa;


    public Telefones() {
    }

    public Telefones(String telefone) {
        this.telefone = telefone;
    }

    public int getIdTelefone() {
        return idTelefone;
    }

    public void setIdTelefone(int idTelefone) {
        this.idTelefone = idTelefone;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public int getIdpessoa() {
        return idpessoa;
    }

    public void setIdpessoa(int idpessoa) {
        this.idpessoa = idpessoa;
    }
}
