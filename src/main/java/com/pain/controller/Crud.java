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

            // Insere Sempre na Tabela Principal Primeiro
            int id = montaStatement(campos,classe,obj,con,cp);

            int anotacao = 0;
            while(anotacao == 0){
                if(objectLocal.size() > 0){
                    for (Class<?> cl: objectLocal){
                        Field[] cpobjLocal = cl.getDeclaredFields();

                    }
                }
                anotacao = 1;
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

    @Override
    public Object inserted(Object obj, Class clazz, Connection con) throws SQLException {
        return null;
    }

    /**
     *
     * @param tipo  Tipo 1 - Insert, 2 - Update, 3 Delete e 4- Select
     * @param colunas Colunas dos Campos do banco de dados que seão inseridos
     * @param className Nome da classe pois é o mesmo do banco de dados
     * @return Retorna uma String com a Query
     */
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

    /**
     * Método Responsável por verificar as Anotações
     * @param an Aqui entra um Array, pois cada compo pode ter mais de uma anotação por campo
     * @param tipoAn Tipo da Anotação [ID, FK, ListObjects, Objects]
     * @return
     */
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

    /**
     * Método Responsável por retirar todas as anotações do campos
     * @param campos Campos ddo Objeto Mapeado
     * @return campos da classe sem anotação pra gravar no banco
     */
    public List<Field> retiraAnotacao(Field[] campos,int tipo){
        List<Field> ret = new ArrayList<>();

        if(tipo == 1){
            for(Field c : campos){
                Annotation[] a = c.getDeclaredAnnotations();
                if(a.length == 0){
                    ret.add(c);
                }
            }
        }
        else {
            for(Field c : campos){
                Annotation[] a = c.getDeclaredAnnotations();
                if(a.length > 0){
                    for (Annotation b: a) {
                        Class<?> d = b.annotationType();
                        if(d.getSimpleName().equals("Fk")){
                            ret.add(c);
                        }
                    }
                }
                else{
                    ret.add(c);
                }
            }
        }

        return ret;
    }


    public int montaStatement(Field[] campos, Class<?> classe, Object obj, Connection con, List<String> cp) throws SQLException{

        PreparedStatement stmt = null;
        ResultSet rs = null;


        sql = constructorQuery(1,cp, classe.getSimpleName());
        stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);


        int i = 1;
        List<Field> notAnotation = retiraAnotacao(campos,1);

        for (Field campo: notAnotation) {
            try{
                campo.setAccessible(true);
                stmt.setObject(i, campo.get(obj));
                i++;
            }
            catch (IllegalAccessException | SQLException e){
                System.out.println(e);
            }
        }

        stmt.execute();
        rs = stmt.getGeneratedKeys();
        int id = 0;
        while (rs.next()){
            id = rs.getInt(1);
        }

        return id;
    }

    public void insereObjectSimple(Field[] campos, Class<?> classe, Object obj, Connection con, List<String> cp, int id) throws SQLException{


        PreparedStatement stmt = null;
        ResultSet rs = null;


        sql = constructorQuery(1,cp, classe.getSimpleName());
        stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);


        int i = 1;
        List<Field> notAnotation = retiraAnotacao(campos,1);

        for (Field campo: notAnotation) {
            try{
                campo.setAccessible(true);
                Annotation[] a = campo.getDeclaredAnnotations();
                if(a.length > 0){
                    for (Annotation b : a){
                        Class<?> anotacao = b.annotationType();
                        if(anotacao.getSimpleName().equals("Fk")){
                            stmt.setObject(i, campo.get(id));
                        }
                    }
                }
                else {
                    stmt.setObject(i, campo.get(obj));
                }
                i++;
            }
            catch (IllegalAccessException | SQLException e){
                System.out.println(e);
            }
        }

        stmt.execute();
        rs = stmt.getGeneratedKeys();
        int idi = 0;
        while (rs.next()){
            idi = rs.getInt(1);
        }

    }
}
