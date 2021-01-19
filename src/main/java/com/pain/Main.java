package com.pain;

import com.pain.conections.ConectionsDatabases;
import com.pain.controller.Crud;
import com.pain.controller.MappingClass;
import com.pain.model.ModelTeste;
import com.pain.model.Pessoa;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args){
        System.out.println("Tese");

        MappingClass mappingClass = new MappingClass();
        ModelTeste modelTeste = new ModelTeste();
        try{
            mappingClass.atClass(modelTeste);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

        ConectionsDatabases conectionsDatabases = new ConectionsDatabases(
            "localhost","5432","postgres","1816","t", 1);
        if(!conectionsDatabases.openConnections()){
            System.out.println("Falha na conexão");
        }
        else{
            try{
                Connection con = conectionsDatabases.newDbConection("t");
                Pessoa p = new Pessoa("Nene", 23);
                Crud c = new Crud();
                c.inserted(p, Pessoa.class,"r",conectionsDatabases);
            }
            catch (SQLException e){
                System.out.println(e.getMessage());
            }

        }
    }
}
