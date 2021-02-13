package com.pain_crud;

import com.pain_crud.Metodos.Crud;
import com.pain_crud.conections.ConectionsDatabases;
import com.pain_crud.model.Pessoa;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;

public class Main {

    public static void main(String[] args){
//        System.out.println("Tese");
//
//        ConectionsDatabases conectionsDatabases = new ConectionsDatabases(
//            "localhost","5432","postgres","1816","r", 1);
//        if(!conectionsDatabases.openConnections()){
//            System.out.println("Falha na conexão");
//        }
//        else{
//            try{
//                Connection con = conectionsDatabases.newDbConection("r");
//                Pessoa p = new Pessoa("Geovane", 23);
//                Crud c = new Crud();
//
//                Hashtable obj = (Hashtable) c.insertedOne(p, Pessoa.class,con);
//                p.setNome("Ednak");
//                Object edit = c.editingOne(p,Pessoa.class,con,(int) obj.get("id"));
//                c.deleted(con,(int) obj.get("id"),"Pessoa");
//                Object a = c.getAll(con,"SELECT id, nome, idade FROM pessoa");
//                System.out.println(obj);
//            }
//            catch (SQLException e){
//                System.out.println(e.getMessage());
//            }
//        }
    }
}
