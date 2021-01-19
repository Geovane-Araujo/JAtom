package com.pain.controller;

import com.pain.conections.ConectionsDatabases;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Crud implements com.pain.repository.Crud {

    String sql = "";

    public Object inserted(Object obj, Class clazz, String db,  ConectionsDatabases conectionsDatabases)throws SQLException {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        Class<?> classe = obj.getClass();
        Field[] campos = classe.getDeclaredFields();

        List<String> cp = new ArrayList<String>();

        List<String> camposAnotacoes = new ArrayList<String>();

        List<String> ids = new ArrayList<String>();
        List<Class<?>> listObjects = new ArrayList<Class<?>>();
        List<Class<?>> objectLocal = new ArrayList<Class<?>>();

        for(Field campo : campos){
            campo.setAccessible(false);
            if(campo.getAnnotations().length > 0){
                Annotation[] an = campo.getDeclaredAnnotations();
                if(an.length > 0){
                    for(Annotation ano: an){
                        Class<?> anotacao = ano.annotationType();
                        String nameAn = anotacao.getSimpleName();
                        if(nameAn.equals("Id")){
                            ids.add(campo.getName());
                        }
                        if(nameAn.equals("ListObjectLocal")){
                            Class<?> a = (Class<?>) ((ParameterizedType) campo.getGenericType()).getActualTypeArguments()[0];
                            listObjects.add(a);
                            camposAnotacoes.add(campo.getName());
                        }
                        if(nameAn.equals("ObjectLocal")){
                            objectLocal.add((Class<?>)campo.getType());
                            camposAnotacoes.add(campo.getName());
                        }
                    }
                }

            }
            else{
                camposAnotacoes.add(campo.getName());
                cp.add(campo.getName());
            }
        }

        try{
            con = conectionsDatabases.newDbConection(db);
            sql = constructorQuery(1,cp, classe.getSimpleName());
            stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            int i = 1;
            for (Field campo: campos) {
                try{
                    campo.setAccessible(true);
                    if(!verificaAnotacao(campo.getDeclaredAnnotations(),"Id")) {
                        stmt.setObject(i, campo.get(obj));
                        i++;
                    }
                }
                catch (IllegalAccessException e){
                    System.out.println(e);
                }

            }
            stmt.execute();
            rs = stmt.getGeneratedKeys();
            int id = 0;
            while (rs.next()){
                id = rs.getInt(1);
            }

            if(objectLocal.size() > 0){
                for (Class<?> cl: objectLocal){
                    Field[] cpobjLocal = cl.getDeclaredFields();
                }
            }


        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        finally {
            con.close();
        }
        return null;
    }

    public Object deleted(Object obj, Class clazz, String db) {
        return null;
    }

    public List<Object> getAll(Object obj, Class clazz, String db) {
        return null;
    }

    public Object getById(long id, Class clazz, String db) {
        return null;
    }

    public Object searchByParameters(Object obj, Class clazz, String db) {
        return null;
    }

    public String constructorQuery(int tipo,List<String> colunas, String className){

        String sql = "";
        // inserted
        if(tipo == 1){
            sql = "INSERT INTO "+ className +" (";
            String atributos = "";
            String ins = "";
            for (String name: colunas) {
                atributos += name +",";
                ins += "?,";
            }
            atributos = atributos.substring(0,atributos.length()-1);
            ins = ins.substring(0,ins.length()-1);
            sql += atributos +") VALUES ("+  ins+ ")";

        }
        return sql;
    }

    public boolean verificaAnotacao(Annotation[] an,String tipoAn){
        boolean ret = false;

        for(Annotation a :an){
            Class<?> anotacao = a.annotationType();
            if(tipoAn.equals(anotacao.getSimpleName())){
                return true;
            }
        }
        return ret;
    }
}
