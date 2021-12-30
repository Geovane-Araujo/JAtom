package com.jatom.services;

import com.google.gson.Gson;
import com.jatom.ConnectionDatabase;
import com.jatom.anotations.*;
import com.jatom.repository.JAtomRepository;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class Atom implements JAtomRepository {

    Connection con = null;

    ConnectionDatabase connectionDatabase = new ConnectionDatabase();

    @Override
    @Deprecated
    public int insertedOne(Object obj, Connection con) throws SQLException, IllegalAccessException {

        int id = 0;
        ResultSet rs = null;

        PreparedStatement stmt = contructorCommand(obj,con,0);
        stmt.execute();
        rs = stmt.getGeneratedKeys();
        while (rs.next()) {
            id = rs.getInt(1);
        }

        return id;
    }

    @Override
    @Deprecated
    public int inserted(Object obj, Connection con) throws SQLException, IllegalAccessException {

        int id = 0;
        ResultSet rs = null;

        PreparedStatement stmt = contructorCommand(obj,con,0);
        stmt.execute();
        rs = stmt.getGeneratedKeys();
        while (rs.next()) {
            id = rs.getInt(1);
        }

        String colunaId = "";
        Boolean recurse = true;
        while(recurse){

            Field[] fields = obj.getClass().getDeclaredFields();
            for(Field fi : fields){

                if(fi.getAnnotation(Id.class) != null){//#1
                    if(fi.getAnnotation(Id.class).identificador().equals(""))
                        colunaId = fi.getName();
                    else
                        colunaId = fi.getAnnotation(Id.class).identificador();
                }

                if(fi.getAnnotation(SimpleObject.class) != null){//#2

                    fi.setAccessible(true);

                    Object classObjFilho = fi.get(obj);

                    if(classObjFilho != null){

                        Field[] objFields = classObjFilho.getClass().getDeclaredFields();

                        for(Field objField : objFields){

                            if(objField.getAnnotation(Fk.class) != null && objField.getAnnotation(Fk.class).value().equals(colunaId)) {//#3
                                objField.setAccessible(true);
                                objField.set(classObjFilho,id);
                            }
                        }

                        inserted(classObjFilho,con);
                    }
                }
                if(fi.getAnnotation(ListObject.class) != null){

                    fi.setAccessible(true);
                    List<?> classListObjFilho = (List<?>) fi.get(obj);

                    if(classListObjFilho != null){

                        for(Object classObjFilho : classListObjFilho){
                            Field[] objFields = classObjFilho.getClass().getDeclaredFields();

                            for(Field objField : objFields){

                                if(objField.getAnnotation(Fk.class) != null && objField.getAnnotation(Fk.class).value().equals(colunaId)) {//#3
                                    objField.setAccessible(true);
                                    objField.set(classObjFilho,id);
                                }
                            }

                            inserted(classObjFilho,con);
                        }
                    }
                }
            }
            recurse = false;
        }
        return id;
    }

    @Override
    @Deprecated
    public void editingOne(Object obj, Connection con) throws SQLException, IllegalAccessException {
        PreparedStatement stmt = contructorCommand(obj,con,1);
        stmt.execute();
    }

    @Override
    @Deprecated
    public void editing(Object obj, Connection con) throws SQLException, IllegalAccessException {
        PreparedStatement stmt = contructorCommand(obj,con,1);
        stmt.execute();

        String colunaId = "";
        Boolean recurse = true;
        Object id = 0;

        while(recurse){

            Field[] fields = obj.getClass().getDeclaredFields();
            for(Field fi : fields){

                if(fi.getAnnotation(Id.class) != null){//#1
                    colunaId = fi.getName();
                    fi.setAccessible(true);
                    id = fi.get(obj);
                }

                if(fi.getAnnotation(SimpleObject.class) != null){//#2

                    fi.setAccessible(true);

                    Object classObjFilho = fi.get(obj);

                    if(classObjFilho != null){

                        Field[] objFields = classObjFilho.getClass().getDeclaredFields();
                        boolean novo = false;
                        for(Field objField : objFields){

                            if(objField.getAnnotation(Fk.class) != null && objField.getAnnotation(Fk.class).value().equals(colunaId)) {//#3
                                objField.setAccessible(true);

                                Object variavel = objField.get(classObjFilho);

                                if(variavel.equals(0) || variavel.equals("")){
                                    novo = true;
                                }
                                objField.set(classObjFilho,id);
                            }
                        }
                        if(novo)
                            inserted(classObjFilho,con);
                        else
                            editing(classObjFilho,con);
                    }
                }
                if(fi.getAnnotation(ListObject.class) != null){

                    fi.setAccessible(true);
                    List<?> classListObjFilho = (List<?>) fi.get(obj);

                    if(classListObjFilho != null){

                        for(Object classObjFilho : classListObjFilho){
                            Field[] objFields = classObjFilho.getClass().getDeclaredFields();

                            boolean novo = false;//verifica se é um novo objeto

                            for(Field objField : objFields){

                                if(objField.getAnnotation(Fk.class) != null && objField.getAnnotation(Fk.class).value().equals(colunaId)) {//#3
                                    objField.setAccessible(true);

                                    Object variavel = objField.get(classObjFilho);

                                    if(variavel.equals(0) || variavel.equals("")){
                                        novo = true;
                                    }
                                    objField.set(classObjFilho,id);
                                }
                            }

                            if(novo)
                                inserted(classObjFilho,con);
                            else
                                editing(classObjFilho,con);
                        }
                    }
                }
            }
            recurse = false;
        }
    }

    @Override
    @Deprecated
    public void deleted(Connection con, String className, String identity, Object obj) throws SQLException {

        String sql = "";
        PreparedStatement stmt = null;

        sql = "DELETE FROM " + className + " WHERE "+identity+" =  ?";
        stmt = con.prepareStatement(sql);
        stmt.setObject(0,obj);
        stmt.execute();
    }

    @Override
    @Deprecated
    public Object getAll(Class clazz,Connection con, String sql) throws SQLException {

        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<String> cabecalho = new ArrayList<>();
        Gson gson = new Gson();

        stmt = con.prepareStatement(sql);
        rs = stmt.executeQuery();

        ResultSetMetaData rsm = rs.getMetaData();
        String col = "";
        for(int i = 1;i <= rsm.getColumnCount();i++){
            col = rsm.getColumnName(i);
            cabecalho.add(col);
        }

        Map objR ;
        List<Object> obj = new ArrayList<>();
        while (rs.next()){
            objR = new LinkedHashMap<String, Object>();
            for (String cabe: cabecalho) {
                objR.put(cabe,rs.getObject(cabe));
            }
            String json = gson.toJson(objR);
            obj.add(gson.fromJson(json,clazz));
        }
        return obj;
    }

    @Override
    @Deprecated
    public Object getOne(Class cazz,Connection con, String sql) throws SQLException {

        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<String> cabecalho = new ArrayList<>();


        stmt = con.prepareStatement(sql);
        rs = stmt.executeQuery();

        ResultSetMetaData rsm = rs.getMetaData();
        String col = "";
        for(int i = 1;i <= rsm.getColumnCount();i++){
            col = rsm.getColumnName(i);
            cabecalho.add(col);
        }

        Hashtable objR = new Hashtable();
        while (rs.next()){
            for (String cabe: cabecalho) {
                if(rs.getObject(cabe)!= null)
                    objR.put(cabe,rs.getObject(cabe));
            }
        }
        Gson gson = new Gson();
        String json = gson.toJson(objR);
        Object ret = gson.fromJson(json,cazz);
        return ret;
    }

    @Override
    public void executeQuery(String sql) throws SQLException, IllegalAccessException {

    }

    @Override
    @Deprecated
    public void executeQuery(Connection con, String sql) throws SQLException {
        PreparedStatement stmt = null;
        stmt = con.prepareStatement(sql);
        stmt.execute();
        stmt.close();
    }

    @Override
    public void save(Object obj) {

        Connection con = null;
        final String[] columnId = {""};

        Arrays.stream(obj.getClass().getDeclaredFields()).forEach(item -> {
            if(item.getAnnotation(Id.class) != null){
                if(item.getAnnotation(Id.class).identificador().equals(""))
                    columnId[0] = item.getName();
                else
                    columnId[0] = item.getAnnotation(Id.class).identificador();
                return;
            }
        });

        con = connectionDatabase.openConnection();

        try{

            if(columnId[0].isEmpty())
                this.operationPercistence(obj,con,0);
            else
                this.operationPercistence(obj,con,1);

            con.commit();

        } catch (SQLException ex){
            new Throwable("Não foi possível fazer a inserção" + ex.getMessage());
        } catch (IllegalAccessException e) {
            new Throwable("Não foi possível fazer a inserção" + e.getMessage());
        }
    }

    @Override
    public void operationPercistence(Object obj, Connection con, int type) throws IllegalAccessException, SQLException {

        int id = 0;
        ResultSet rs = null;


        PreparedStatement stmt = contructorCommand(obj,con,0);
        stmt.execute();
        rs = stmt.getGeneratedKeys();
        while (rs.next()) {
            id = rs.getInt(1);
        }


        String colunaId = "";
        Boolean recurse = true;

        while(recurse){

            Field[] fields = obj.getClass().getDeclaredFields();
            for(Field fi : fields){

                if(fi.getAnnotation(Id.class) != null){//#1
                    if(fi.getAnnotation(Id.class).identificador().equals(""))
                        colunaId = fi.getName();
                    else
                        colunaId = fi.getAnnotation(Id.class).identificador();
                }

                if(fi.getAnnotation(SimpleObject.class) != null){//#2

                    fi.setAccessible(true);

                    Object classObjFilho = fi.get(obj);

                    if(classObjFilho != null){

                        Field[] objFields = classObjFilho.getClass().getDeclaredFields();

                        for(Field objField : objFields){

                            if(objField.getAnnotation(Fk.class) != null && objField.getAnnotation(Fk.class).value().equals(colunaId)) {//#3
                                objField.setAccessible(true);
                                objField.set(classObjFilho,id);
                            }
                        }

                        this.operationPercistence(classObjFilho,con,type);
                    }
                }
                if(fi.getAnnotation(ListObject.class) != null){

                    fi.setAccessible(true);
                    List<?> classListObjFilho = (List<?>) fi.get(obj);

                    if(classListObjFilho != null){

                        for(Object classObjFilho : classListObjFilho){
                            Field[] objFields = classObjFilho.getClass().getDeclaredFields();

                            for(Field objField : objFields){

                                if(objField.getAnnotation(Fk.class) != null && objField.getAnnotation(Fk.class).value().equals(colunaId)) {//#3
                                    objField.setAccessible(true);
                                    objField.set(classObjFilho,id);
                                }
                            }

                            this.operationPercistence(classObjFilho,con,type);
                        }
                    }
                }
            }
            recurse = false;
        }
        // return id;
    }

    @Override
    public List<Object> getAll(String sql) {
        return null;
    }

    @Override
    public Object get(String sql) {
        return null;
    }

    private static PreparedStatement contructorCommand(Object clazz, Connection con, int type) throws SQLException, IllegalAccessException {

        PreparedStatement stmt = null;

        String sql = "";
        Field[] fields = clazz.getClass().getDeclaredFields();
        String strfield = "",strstatements = "";
        List<Object> liValues = new ArrayList<>();// pensar sobre esdte

        String className = "";

        //Aqui pega o nome da tabela, caso não tiver o alias pega o nome da classe
        if(clazz.getClass().getAnnotation(TableName.class) != null){
            className = clazz.getClass().getAnnotation(TableName.class).value();
        }
        else {
            className = clazz.getClass().getSimpleName();
        }

        //insert
        if(type == 0){
            int i = 1;
            for (Field di: fields) {

                if(di.getAnnotation(Id.class) == null && di.getAnnotation(Ignore.class) == null && di.getAnnotation(ListObject.class
                ) == null && di.getAnnotation(SimpleObject.class) == null){
                    di.setAccessible(true);
                    strfield += di.getName() + ",";
                    strstatements += "?,";
                    liValues.add(di.get(clazz));
                    i++;
                }
            }
            strstatements = strstatements.substring(0,strstatements.length() - 1);//verificar que pode ter forma melhor
            strfield = strfield.substring(0,strfield.length() - 1);

            sql = "INSERT INTO " + className + " ("+strfield+") values " + "("+strstatements+");";
            stmt = con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);

            i = 1;
            for (Object ob: liValues) {
                stmt.setObject(i,ob);
                i++;
            }
        }
        else {
            String identifield = "";
            Object vlIdentifield = 0;
            int i = 1;
            for (Field di: fields) {
                if(di.getAnnotation(Id.class) == null && di.getAnnotation(Ignore.class) == null && di.getAnnotation(ListObject.class
                ) == null && di.getAnnotation(SimpleObject.class) == null){
                    di.setAccessible(true);
                    strfield += di.getName() + " = ?,";
                    liValues.add(di.get(clazz));
                    i++;
                }
                if(di.getAnnotation(Id.class) != null){
                    di.setAccessible(true);
                    identifield = di.getName();
                    vlIdentifield = di.get(clazz);

                }
            }
            strfield = strfield.substring(0,strfield.length() - 1);

            sql = "UPDATE  " + className + " SET " + strfield + " where " + identifield + " = ?";
            stmt = con.prepareStatement(sql);

            i = 1;
            for (Object ob: liValues) {
                stmt.setObject(i,ob);
                i++;
            }
            stmt.setObject((liValues.size() + 1),vlIdentifield);
        }

        return stmt;
    }


}
