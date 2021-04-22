package com.pain_crud;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class MappingClass {

    public Object atClass(Object clazz) throws IllegalAccessException {
        Class<?> classe = clazz.getClass();
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

        Hashtable retorno = new Hashtable();
        retorno.put("campos", cp);

        return retorno;
    }

    // *************************************************** Métodos que um dia serão uteis *******************************
    //@Override
    //    public Object insertedAll(Object obj, Class clazz, Connection con) throws SQLException {
    //
    //        Class<?> classe = obj.getClass();
    //        Field[] campos = classe.getDeclaredFields();
    //
    //        List<String> cp = new ArrayList<String>();
    //
    //        List<String> camposAnotacoes = new ArrayList<String>();
    //
    //        List<String> ids = new ArrayList<String>();
    //        List<Class<?>> listObjects = new ArrayList<Class<?>>();
    //        List<Class<?>> objectLocal = new ArrayList<Class<?>>();
    //        List<Class<?>> pilha = new ArrayList<>();
    //
    //        separaObject(objectLocal, cp, listObjects, campos, camposAnotacoes, ids);
    //
    //        try {
    //            // Insere Sempre na Tabela Principal Primeiro
    //            int id = montaStatement(campos, classe, obj, con, cp, 1, 0);
    //
    //            int anotacao = 0;
    //            if (objectLocal.size() > 0) {
    //                for (Class<?> cl : objectLocal) {
    //                    Field[] cpobjLocal = cl.getDeclaredFields();
    //                    // montaStatement(cpobjLocal,cl.getClass(),obj,con);
    //                    System.out.println(cpobjLocal.getClass());
    //                }
    //            }
    //
    //        } catch (SQLException e) {
    //            System.out.println(e.getMessage());
    //        } finally {
    //            con.close();
    //        }
    //        return null;
    //    }

    //    @Override
//    public Object insertedAll(Object obj, Class clazz, String db, ConectionsDatabases conectionsDatabases) throws SQLException {
//
//        Connection con = null;
//        PreparedStatement stmt = null;
//        ResultSet rs = null;
//
//        Class<?> classe = obj.getClass();
//        Field[] campos = classe.getDeclaredFields();
//
//        List<String> cp = new ArrayList<String>();
//
//        List<String> camposAnotacoes = new ArrayList<String>();
//
//        List<String> ids = new ArrayList<String>();
//        List<Class<?>> listObjects = new ArrayList<Class<?>>();
//        List<Class<?>> objectLocal = new ArrayList<Class<?>>();
//        List<Class<?>> pilha = new ArrayList<>();
//
//        separaObject(objectLocal, cp, listObjects, campos, camposAnotacoes, ids);
//
//        try {
//            con = conectionsDatabases.newDbConection(db);
//
//            // Insere Sempre na Tabela Principal Primeiro
//            int id = montaStatement(campos, classe, obj, con, cp, 1, 0);
//
//            int anotacao = 0;
//            if (objectLocal.size() > 0) {
//                for (Class<?> cl : objectLocal) {
//                    Field[] cpobjLocal = cl.getDeclaredFields();
//                    // montaStatement(cpobjLocal,cl.getClass(),obj,con);
//                    System.out.println(cpobjLocal.getClass());
//                }
//            }
//
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        } finally {
//            con.close();
//        }
//        return null;
//    }
}
