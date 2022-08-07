package com.jatom.utils;

import com.google.gson.Gson;
import com.jatom.anotations.*;
import com.jatom.enuns.JAtomTypes;
import com.jatom.model.GlobalVariables;
import com.jatom.model.JAtomParameters;
import com.jatom.model.JAtomResults;
import com.jatom.repository.JAtomRepository;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public abstract class Atom extends GlobalVariables implements JAtomRepository {

    Logger logger = Logger.getLogger(InitialConnection.class.getName());
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
                    if(fi.getAnnotation(Id.class).value().equals(""))
                        colunaId = fi.getName();
                    else
                        colunaId = fi.getAnnotation(Id.class).value();
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
    @Deprecated
    public void executeQuery(Connection con, String sql) throws SQLException {
        PreparedStatement stmt = null;
        stmt = con.prepareStatement(sql);
        stmt.execute();
        stmt.close();
    }

    //-------------------------------NEW IMPLEMENTATIONS---------------------------------------

    @Override
    public void executeQuery(String sql) throws Exception {
        Connection con = null;

        try {

            con = connectionDatabase.openConnection();
            PreparedStatement stmt = null;
            stmt = con.prepareStatement(sql);
            stmt.execute();
            stmt.close();

        } catch (SQLException e) {
            logger.severe("Não foi possível conectar a base de dados: " + e.getMessage());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void executeQuery(String sql, String db) {
        Connection con = null;

        try {

            con = connectionDatabase.openConnection(db);
            PreparedStatement stmt = null;
            stmt = con.prepareStatement(sql);
            stmt.execute();
            stmt.close();

        } catch (SQLException e) {
            logger.severe("Não foi possível fazer a operação " + e.getMessage());
            try {
                throw new Exception("Não foi possível fazer a operação " + e.getMessage());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void save(Object obj) throws Exception {

        Connection con = null;
        String columnId = "";

        Field fieldIdentifier = Arrays.stream(obj.getClass().getDeclaredFields()).filter(item -> item.getAnnotation(Id.class) != null).findFirst().orElse(null);

        try{

            con = connectionDatabase.openConnection();
            con.setAutoCommit(false);
            fieldIdentifier.setAccessible(true);
            if(fieldIdentifier.get(obj) == null || fieldIdentifier.get(obj).equals("") || fieldIdentifier.get(obj).toString().equals("0"))
                this.operationPercistence(obj,con,0);
            else
                this.operationPercistence(obj,con,1);

            con.commit();

        } catch (SQLException | IllegalAccessException ex){
            try {
                con.rollback();
            } catch (SQLException e) {
            }
            logger.severe("Não foi possível fazer a operação " + ex.getMessage());
            throw new Exception("Não foi possível fazer a operação " + ex.getMessage());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                logger.severe("Não foi possível fazer a operação " + e.getMessage());
                throw new Exception("Não foi possível fazer a operação " + e.getMessage());
            }
        }
    }

    @Override
    public void save(Object obj, String db) throws Exception {

        Connection con = null;

        Field fieldIdentifier = Arrays.stream(obj.getClass().getDeclaredFields()).filter(item -> item.getAnnotation(Id.class) != null).findFirst().orElse(null);

        try{

            con = connectionDatabase.openConnection(db);
            con.setAutoCommit(false);
            fieldIdentifier.setAccessible(true);
            if(fieldIdentifier.get(obj) == null || fieldIdentifier.get(obj).equals("") || fieldIdentifier.get(obj).toString().equals("0"))
                this.operationPercistence(obj,con,0);
            else
                this.operationPercistence(obj,con,1);

            con.commit();

        } catch (SQLException ex){
            try {
                con.rollback();
            } catch (SQLException e) {
            }
            logger.severe("Não foi possível fazer a operação " + ex.getMessage());
            throw new Exception("Não foi possível fazer a inserção " + ex.getMessage());
        } catch (IllegalAccessException e) {
            logger.severe("Não foi possível fazer a operação " + e.getMessage());
            throw  new Exception("Não foi possível fazer a inserção " + e.getMessage());
        }
    }

    @Override
    public Connection save(Object obj, Connection con, boolean finishTransaction) throws Exception {


        String columnId = "";

        Field fieldIdentifier = Arrays.stream(obj.getClass().getDeclaredFields()).filter(item -> item.getAnnotation(Id.class) != null).findFirst().orElse(null);

        try{
            if(con == null){
                con = connectionDatabase.openConnection();
                con.setAutoCommit(false);
            }

            fieldIdentifier.setAccessible(true);
            if(fieldIdentifier.get(obj) == null || fieldIdentifier.get(obj).equals("") || fieldIdentifier.get(obj).toString().equals("0"))
                this.operationPercistence(obj,con,0);
            else
                this.operationPercistence(obj,con,1);

            if(finishTransaction)
                con.commit();

        } catch (SQLException | IllegalAccessException ex){
            try {
                con.rollback();
            } catch (SQLException e) {
            }

            logger.severe("Não foi possível fazer a operação " + ex.getMessage());
            throw  new Exception("Não foi possível fazer a operação " + ex.getMessage());
        } finally {
            try {
                if(finishTransaction)
                    con.close();
            } catch (SQLException e) {
                logger.severe("Não foi possível fazer a operação " + e.getMessage());
                throw  new Exception("Não foi possível fazer a operação " + e.getMessage());
            }
        }

        return con;
    }

    @Override
    public Connection save(Object obj, String db, Connection con, boolean finishTransaction) throws Exception {

        String columnId = "";

        Field fieldIdentifier = Arrays.stream(obj.getClass().getDeclaredFields()).filter(item -> item.getAnnotation(Id.class) != null).findFirst().orElse(null);

        try{
            if(con == null){
                con = connectionDatabase.openConnection(db);
                con.setAutoCommit(false);
            }

            fieldIdentifier.setAccessible(true);
            if(fieldIdentifier.get(obj) == null || fieldIdentifier.get(obj).equals("") || fieldIdentifier.get(obj).toString().equals("0"))
                this.operationPercistence(obj,con,0);
            else
                this.operationPercistence(obj,con,1);

            if(finishTransaction)
                con.commit();

        } catch (SQLException | IllegalAccessException ex){
            try {
                con.rollback();
            } catch (SQLException e) {
            }
            logger.severe("Não foi possível fazer a operação " + ex.getMessage());
            throw  new Exception("Não foi possível fazer a operação " + ex.getMessage());
        } finally {
            try {
                if(finishTransaction)
                    con.close();
            } catch (SQLException e) {
                logger.severe("Não foi possível fazer a operação " + e.getMessage());
                throw  new Exception("Não foi possível fazer a operação " + e.getMessage());
            }
        }

        return con;
    }

    @Override
    public <T> T get(JAtomParameters jAtomParameters)  {


        Connection con = null;
        // palavras reservadas

        String columns = (String)(jAtomParameters.get(JAtomTypes.COLUMNS) == null ? "*" : jAtomParameters.get(JAtomTypes.COLUMNS).toString());
        Class clazz = (Class)(jAtomParameters.get(JAtomTypes.CLASS) == null ? null : jAtomParameters.get(JAtomTypes.CLASS));
        String sql = (String)(jAtomParameters.get(JAtomTypes.SQL) == null ? null: jAtomParameters.get(JAtomTypes.SQL)).toString();
        String db = (String)(jAtomParameters.get(JAtomTypes.DB_NAME) == null ? null: jAtomParameters.get(JAtomTypes.DB_NAME).toString());

        try{
            if(db == null)
                con = connectionDatabase.openConnection();
            else
                con = connectionDatabase.openConnection(db);

            // retorno da Sql Nativa
            if(sql != null){
                return (T)execute(con,sql,clazz);
            }


        } catch (SQLException e) {
            logger.severe("Não foi possível fazer a operação " + e.getMessage());
            try {
                throw  new Exception( e.getMessage());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                logger.severe("Não foi possível fazer a operação " + e.getMessage());
            }
        }

        return null;
    }

    @Override
    public <T> T getByID(Class clazz, Object id) throws Exception {

        String columnId =  "";
        Object obj = null;
        try{
            obj= clazz.getDeclaredConstructor().newInstance();
            con = connectionDatabase.openConnection();

            Field fieldpai = Arrays.stream(clazz.getDeclaredFields()).filter(item -> item.getAnnotation(Id.class) != null).findFirst().orElse(null);
            if(fieldpai != null){
                columnId =  fieldpai.getName();
            } else
                return null;

            String sql = constructQuery(clazz,columnId, id);

            obj = execute(con,sql,clazz);

            operationGet(con,obj);


        } catch (SQLException e) {
            throw  new Exception( e.getMessage());
        } catch (InvocationTargetException e) {
            throw  new Exception( e.getMessage());
        } catch (InstantiationException e) {
            throw  new Exception( e.getMessage());
        } catch (IllegalAccessException e) {
            throw  new Exception( e.getMessage());
        } catch (NoSuchMethodException e) {
            throw  new Exception( e.getMessage());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return (T)obj;
    }

    @Override
    public <T> T getByID(Class clazz, Object id, String db) {
        return null;
    }

    private void operationPercistence(Object obj, Connection con, int type) throws IllegalAccessException, SQLException {

        Object id = 0;
        ResultSet rs = null;
        Field aux = null;
        boolean noEntity = false;

        if(obj.getClass().getAnnotation(NoEntity.class) == null){

            // coloca o id no objeto para ter como retorno
            aux = Arrays.stream(obj.getClass().getDeclaredFields()).filter(item -> item.getAnnotation(Id.class) != null).findFirst().orElse(null);
            aux.setAccessible(true);

            PreparedStatement stmt = contructorCommand(obj,con,type);
            stmt.execute();
            rs = stmt.getGeneratedKeys();
            while (rs.next()) {
                id = rs.getInt(1);
            }
            aux.set(obj,id);
        } else{
            // pega o ID na variavel de FK quando for noEntity
            aux = Arrays.stream(obj.getClass().getDeclaredFields()).filter(item -> item.getAnnotation(Fk.class) != null).findFirst().orElse(null);
            aux.setAccessible(true);

            if(aux != null){
                id = aux.get(obj);
            }
            noEntity = true;
        }

        String colunaId = "";
        Boolean recurse = true;

        while(recurse){

            Field[] fields = obj.getClass().getDeclaredFields();
            for(Field fi : fields){

                if(!noEntity) {
                    if(fi.getAnnotation(Id.class) != null){//#1
                        if(fi.getAnnotation(Id.class).value().equals(""))
                            colunaId = fi.getName();
                        else
                            colunaId = fi.getAnnotation(Id.class).value();
                    }
                } else {
                    if(fi.getAnnotation(Fk.class) != null){//#1
                        if(fi.getAnnotation(Fk.class).value().equals(""))
                            colunaId = fi.getName();
                        else
                            colunaId = fi.getAnnotation(Fk.class).value();
                    }
                }


                if(fi.getAnnotation(SimpleObject.class) != null){//#2

                    fi.setAccessible(true);

                    Object classObjFilho = fi.get(obj);

                    if(classObjFilho != null){


                        String finalColunaId = colunaId;
                        Field fieldFk = Arrays.stream(classObjFilho.getClass().getDeclaredFields()).filter(item -> item.getAnnotation(Fk.class) != null && item.getAnnotation(Fk.class).value().equals(finalColunaId)).findFirst().orElse(null);

                        if(fieldFk != null){
                            fieldFk.setAccessible(true);
                            fieldFk.set(classObjFilho,id);
                        }

                        this.operationPercistence(classObjFilho,con,type);
                    }
                }
                if(fi.getAnnotation(ListObject.class) != null){

                    fi.setAccessible(true);
                    List<?> classListObjFilho = (List<?>) fi.get(obj);

                    if(classListObjFilho != null){

                        for(Object classObjFilho : classListObjFilho){

                            String finalColunaId = colunaId;
                            Field fieldFk = Arrays.stream(classObjFilho.getClass().getDeclaredFields()).filter(item -> item.getAnnotation(Fk.class) != null && item.getAnnotation(Fk.class).value().equals(finalColunaId)).findFirst().orElse(null);

                            if(fieldFk != null){
                                fieldFk.setAccessible(true);
                                fieldFk.set(classObjFilho,id);
                            }

                            this.operationPercistence(classObjFilho,con,type);
                        }
                    }
                }
            }
            recurse = false;
        }
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
                    if(di.getAnnotation(Alias.class) != null)
                        strfield += di.getAnnotation(Alias.class).value() + ",";
                    else
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
                    if(di.getAnnotation(Alias.class) != null)
                        strfield += di.getAnnotation(Alias.class).value() + ",";
                    else
                        strfield += di.getName() + "=?,";
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

    private <T> T operationGet(Connection con, Object obj){

        boolean recursive = true;
        String sql = "";

        Field fieldId = Arrays.stream(obj.getClass().getDeclaredFields()).filter(item -> item.getAnnotation(Id.class) != null).findFirst().orElse(null);
        String entityIdName = (fieldId.getAnnotation(Id.class).value().equals("") ? fieldId.getName() : fieldId.getAnnotation(Id.class).value());
        fieldId.setAccessible(true);


        try {
            Object valueId = fieldId.get(obj);

            while (recursive){

                Field[] fieldsPai = obj.getClass().getDeclaredFields();

                for (Field field: fieldsPai) {
                    field.setAccessible(true);
                    if(field.getAnnotation(SimpleObject.class) != null){

                        Field fkReference = Arrays.stream(field.get(obj).getClass().getDeclaredFields()).filter(item -> item.getAnnotation(Fk.class) != null && item.getAnnotation(Fk.class).value().equals(entityIdName)).findFirst().orElse(null);

                        sql = constructQuery(field.get(obj).getClass(),fkReference.getName(),valueId);
                        Object value = execute(con,sql,field.get(obj).getClass());

                        operationGet(con,value);
                        field.set(obj,value);
                    }
                    else if(field.getAnnotation(ListObject.class) != null){

                        List<?> classListObjFilho = (List<?>) field.get(obj);

                        Type fi = ((ParameterizedTypeImpl) field.getGenericType()).getActualTypeArguments()[0];
                        Field fkReference = Arrays.stream(((Class) fi).getDeclaredFields()).filter(item -> item.getAnnotation(Fk.class) != null && item.getAnnotation(Fk.class).value().equals(entityIdName)).findFirst().orElse(null);

                        sql = constructQuery(((Class) fi),fkReference.getName(),valueId);

                        Object value = execute(con,sql,((Class) fi));

                        for (T a :(List<T>) value) {
                            operationGet(con,a);
                        }

                        field.set(obj,value);
                    }
                }
                recursive = false;
            }
        }catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return (T)obj;
    }

    private <T> T execute(Connection con,String sql, Class clazz) throws SQLException {

        List<Object> results = new ArrayList<>();
        List<String> columns = new ArrayList<>();


        stmt = con.prepareStatement(sql);
        rs = stmt.executeQuery();

        ResultSetMetaData rsm = rs.getMetaData();
        String col = "";

        for(int i = 1;i <= rsm.getColumnCount();i++){
            col = rsm.getColumnName(i);
            columns.add(col);
        }
        JAtomResults obj = null;
        while (rs.next()){
            obj = new JAtomResults();
            for(int i = 0; i < columns.size();i++){
                obj.put(columns.get(i),rs.getObject(columns.get(i)));
            }

            if(clazz != null){
                results.add(gson.fromJson(gson.toJson(obj),clazz));
            } else {
                results.add(obj);
            }
        }

        if(results.size() > 1)
            return (T)results;
        else if(results.size() == 1)
            return (T)results.get(0);
        else
            return null;

    }

    private String constructQuery(Class clazz,String coumnId, Object valueId){

        String sqlJoins = "";
        try{

            Object ob = clazz.getDeclaredConstructor().newInstance();
            String tablename = (clazz.getAnnotation(TableName.class) == null ? clazz.getSimpleName() : ob.getClass().getAnnotation(TableName.class).value());

            sqlJoins = onJoin(ob.getClass(),tablename, coumnId, valueId);


        }catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return sqlJoins;
    }

    private String onJoin(Class clazz,String tableName, String coumnId,Object valueId) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        String join = "";
        String tableUnion = "";
        String sql = "";
        String col = "";


        List<Field> fields = Arrays.stream(clazz.getDeclaredFields()).filter(obj -> obj.getAnnotation(Join.class) != null).collect(Collectors.toList());

        for (Field f: fields) {

            Object c = f.getAnnotation(Join.class).reference().getDeclaredConstructor().newInstance();

            if(c.getClass().getAnnotation(TableName.class) == null){
                tableUnion = c.getClass().getSimpleName();
            } else {
                tableUnion = c.getClass().getAnnotation(TableName.class).value();
            }

            join += " INNER JOIN  " + tableUnion + " ON " +tableName + "."+f.getAnnotation(Join.class).columnReference() + " = " + tableUnion + "."+onJoinId(c.getClass()) + " \n";


            col += ", " + tableUnion + "." + f.getAnnotation(Join.class).columnName() + " as " + f.getName();

        }

        if(!col.equals(""))
            sql = "SELECT "+ tableName + ".* "+col+" FROM "+ tableName + join +" WHERE "+tableName+ "." + coumnId + " = '" + valueId +"'";
        else
            sql = "SELECT "+ tableName + ".* FROM "+ tableName + " WHERE " + coumnId + " = '" + valueId +"'";
        return sql;
    }

    private String onJoinId(Class c){

        String idName = "";

        Field f = Arrays.stream(c.getDeclaredFields()).filter(obj -> obj.getAnnotation(Id.class) != null).findFirst().orElse(null);

        if(!f.getAnnotation(Id.class).value().equals(""))
            idName = f.getAnnotation(Id.class).value();
        else
            idName = f.getName();

        return idName;
    }

}
