package com.atom;

import com.atom.model.Pessoa;
import com.atom.model.Telefones;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args){

        ConectionsDatabases conectionsDatabases = new ConectionsDatabases(
            "E:\\Banco de Dados Teste","","","","dbteste.db", 4);
        conectionsDatabases.openConnections();
        try{
            Connection con = conectionsDatabases.newDbConection("dbteste.db");
            Telefones tel = new Telefones("440000000000");

            List<Telefones> tels = new ArrayList<>();
            int i = 0;
            while (i < 4){
                Telefones tel1 = new Telefones("4499859723"+i);
                tels.add(tel1);
                i++;
            }

            Pessoa p = new Pessoa("Geovane", 23);
            p.setTelefones(tels);
            p.setTel(tel);
            Atom c = new Atom();

            int obj =  c.inserted(p,con);
            p.setNome("Ednak");
            p.setIdpessoa(obj);
            final int[] x = {0};

            tels.forEach(item -> {
                item.setTelefone("44" + x[0]);
                x[0]++;
            });

            tel.setTelefone("Thunai");
            p.getTelefones().add(tel);
            c.editing(p,con);
            //c.deleted(con,"Pessoa","idpessoa",obj);

            List<Pessoa> a = (List<Pessoa>) c.getAll(Pessoa.class,con,"SELECT idpessoa, nome, idade FROM pessoa");
            Object b = (Pessoa) c.getOne(Pessoa.class,con,"SELECT * FROM pessoa where idpessoa = 1");
            System.out.println(obj);
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}
