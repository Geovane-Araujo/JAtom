package com.atom;

import com.atom.model.Pessoa;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Main {

    public static void main(String[] args){
//        System.out.println("Tese");
//
        ConectionsDatabases conectionsDatabases = new ConectionsDatabases(
            "localhost","5432","postgres","1816","teste", 1);
        if(!conectionsDatabases.openConnections()){
            System.out.println("Falha na conexão");
        }
        else{
            try{
                Connection con = conectionsDatabases.newDbConection("teste");
                Pessoa p = new Pessoa("Geovane", 23);
                Atom c = new Atom();

                int obj =  c.insertedOne(p,con);
                p.setNome("Ednak");
                p.setIdpessoa(obj);
                c.editingOne(p,con);
                c.deleted(con,"Pessoa","idpessoa",obj);
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
}
