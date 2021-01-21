package com.pain.model;

import com.pain.anotacoes.Fk;
import com.pain.anotacoes.Id;

public class Telefones {

    @Id
    private int idTelefone;
    private String telefone;
    @Fk(value="id")
    private int idpessoa;

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
