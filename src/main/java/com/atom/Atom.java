package com.atom;

import com.google.gson.Gson;
import org.sqlite.SQLiteConnection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.*;
import java.util.*;

public class Atom {

//    @Override
//    public int insertedOne(Object obj, Connection con) throws SQLException, IllegalAccessException {
//
//        Class<?> classe = obj.getClass();
//        Field[] campos = classe.getDeclaredFields();
//
//        List<String> cp = new ArrayList<String>();
//        List<String> camposAnotacoes = new ArrayList<String>();
//
//        List<String> ids = new ArrayList<String>();
//        List<Class<?>> listObjects = new ArrayList<Class<?>>();
//        List<Class<?>> objectLocal = new ArrayList<Class<?>>();
//
//        separaObject(objectLocal, cp, listObjects, campos, camposAnotacoes, ids);
//
//        int id = montaStatement(campos, classe, obj, con, cp, 1, 0);
//
//        return id;
//    }
    /**
     * Método responsável por fazer o insert, só que diferente do All ele ignora as anotações ListObjects e Object
     *
     * @param obj objeto a ser inserido
     * @param con Objeto com os Atributos de Conexão
     * @return
     * @throws SQLException
     **/
    public static int insertedOne(Object obj, Connection con) throws SQLException, IllegalAccessException {

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

//    @Override
//    public int editingOne(Object obj, Connection con) throws SQLException, IllegalAccessException {
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
//
//        separaObject(objectLocal, cp, listObjects, campos, camposAnotacoes, ids);
//
//        montaStatement(campos, classe, obj, con, cp, 2, idobject);
//
//        return idobject;
//    }
    /**
     *  Método responsável por fazer o Editing, só que diferente do All ele ignora as anotações ListObjects e Object
     *
     * @param obj objeto a ser inserido
     * @param con conexão aberta
     * @return
     * @throws SQLException
     */
    public static void editingOne(Object obj, Connection con) throws SQLException, IllegalAccessException {
        PreparedStatement stmt = contructorCommand(obj,con,1);
        stmt.execute();
    }

//    @Override
//    public void deleted(Connection con, int idobject, String className) throws SQLException {
//
//        sql = constructorQuery(className, idobject);
//        PreparedStatement stmt = null;
//        stmt = con.prepareStatement(sql);
//        stmt.execute();
//    }
    /**
     * Método Responsável por fazer a exclusão de um objeto
     *
     * @param con Conexão Aberta
     * @param className Nome da Tabela
     * @return
     * @throws SQLException
     */
    public static void deleted(Connection con, String className, String identity, Object obj) throws SQLException {

        String sql = "";
        PreparedStatement stmt = null;

        sql = "DELETE FROM " + className + " WHERE "+identity+" =  " + obj;
        stmt = con.prepareStatement(sql);
        stmt.execute();
    }

    public static Object getAll(Class clazz,Connection con, String sql) throws SQLException {

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

    public static Object getOne(Class cazz,Connection con, String sql) throws SQLException {

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

    public static void executeQuery(Connection con, String sql) throws SQLException {
        PreparedStatement stmt = null;
        stmt = con.prepareStatement(sql);
        stmt.execute();
        stmt.close();
    }

//    /**
//     *
//     * @param tipo      Tipo 1 - Insert, 2 - Update, 3 Delete e 4- Select
//     * @param colunas   Colunas dos Campos do banco de dados que seão inseridos
//     * @param className Nome da classe pois é o mesmo do banco de dados
//     * @return Retorna uma String com a Query
//     */
//    public String constructorQuery(int tipo, List<String> colunas, String className, int id) {
//
//        String sql = "";
//        // inserted
//        if (tipo == 1) {
//            sql = "INSERT INTO " + className + " (";
//            String atributos = "";
//            String ins = "";
//            for (String name : colunas) {
//                atributos += name + ",";
//                ins += "?,";
//            }
//            atributos = atributos.substring(0, atributos.length() - 1);
//            ins = ins.substring(0, ins.length() - 1);
//            sql += atributos + ") VALUES (" + ins + ")";
//
//        } else if (tipo == 2) {
//            sql = "UPDATE " + className + " SET ";
//            String atributos = "";
//            String ins = " WHERE ID = " + id;
//            for (String name : colunas) {
//                atributos += name + " = ?,";
//
//            }
//            atributos = atributos.substring(0, atributos.length() - 1);
//            sql += atributos + ins;
//        }
//        return sql;
//    }

//    public String constructorQuery(String className, int id) {
//
//        String sql = "";
//        sql = "DELETE FROM " + className + " WHERE id =  " + id;
//
//        return sql;
//    }

    /**
     * Método Responsável por retirar todas as anotações do campos
     *
     * @param campos Campos ddo Objeto Mapeado
     * @return campos da classe sem anotação pra gravar no banco
     */
    public List<Field> retiraAnotacao(Field[] campos, int tipo) {
        List<Field> ret = new ArrayList<>();

        if (tipo == 1) {
            for (Field c : campos) {
                Annotation[] a = c.getDeclaredAnnotations();
                if (a.length == 0) {
                    ret.add(c);
                }
            }
        } else {
            for (Field c : campos) {
                Annotation[] a = c.getDeclaredAnnotations();
                if (a.length > 0) {
                    for (Annotation b : a) {
                        Class<?> d = b.annotationType();
                        if (d.getSimpleName().equals("Fk")) {
                            ret.add(c);
                        }
                    }
                } else {
                    ret.add(c);
                }
            }
        }

        return ret;
    }

//    public int montaStatement(Field[] campos, Class<?> classe, Object obj, Connection con, List<String> cp, int tipo,
//            int idobject) throws SQLException, IllegalAccessException {
//
//        String alias = classe.getAnnotation(Alias.class).value();
//        sql = constructorQuery(tipo, cp, alias, idobject);
//        stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
//
//        int i = 1;
//        List<Field> notAnotation = retiraAnotacao(campos, 1);
//
//        for (Field campo : notAnotation) {
//            campo.setAccessible(true);
//            stmt.setObject(i, campo.get(obj));
//            i++;
//        }
//
//        stmt.execute();
//        rs = stmt.getGeneratedKeys();
//        int id = 0;
//        while (rs.next()) {
//            id = rs.getInt(1);
//        }
//        rs.close();
//        stmt.close();
//        return id;
//    }

//    /**
//     *
//     * @param objectLocal     List referente a anotação objectLocal
//     * @param cp              Array de string referente aos campos que não tem chave
//     *                        nem outro tipo de relacionamento
//     * @param listObjects     List Referente ao objeto que é um relacionaento de um
//     *                        para muitos
//     * @param campos          campos Field do objeto class
//     * @param camposAnotacoes campos de anotações
//     * @param ids             ids dos campos
//     */
//    @Deprecated
//    public void separaObject(List<Class<?>> objectLocal, List<String> cp, List<Class<?>> listObjects, Field[] campos,
//            List<String> camposAnotacoes, List<String> ids) {
//
//        for (Field campo : campos) {
//            campo.setAccessible(false);
//            if (campo.getAnnotations().length > 0) {
//                Annotation[] an = campo.getDeclaredAnnotations();
//                for (Annotation ano : an) {
//                    Class<?> anotacao = ano.annotationType();
//                    String nameAn = anotacao.getSimpleName();
//                    if (!nameAn.equals("Ignore")) {
//                        if (nameAn.equals("ListObjectLocal")) {
//                            Class<?> a = (Class<?>) ((ParameterizedType) campo.getGenericType())
//                                    .getActualTypeArguments()[0];
//                            Field[] f = a.getDeclaredFields();
//                            listObjects.add(a);
//                            camposAnotacoes.add(campo.getName());
//                        } else if (nameAn.equals("ObjectLocal")) {
//                            objectLocal.add((Class<?>) campo.getType());
//                            camposAnotacoes.add(campo.getName());
//                        } else {
//                            camposAnotacoes.add(campo.getName());
//                            cp.add(campo.getName());
//                        }
//                    }
//                }
//            } else {
//                camposAnotacoes.add(campo.getName());
//                cp.add(campo.getName());
//            }
//        }
//    }
//
//    public void separaObject(List<Class<?>> objectLocal, List<String> cp, List<Class<?>> listObjects, Field[] campos,
//                             List<String> camposAnotacoes) {
//
//        for (Field campo : campos) {
//            campo.setAccessible(false);
//            if (campo.getAnnotations().length > 0) {
//                Annotation[] an = campo.getDeclaredAnnotations();
//                for (Annotation ano : an) {
//                    Class<?> anotacao = ano.annotationType();
//                    String nameAn = anotacao.getSimpleName();
//                    if (!nameAn.equals("Ignore")) {
//                        if (nameAn.equals("ListObjectLocal")) {
//                            Class<?> a = (Class<?>) ((ParameterizedType) campo.getGenericType())
//                                    .getActualTypeArguments()[0];
//                            Field[] f = a.getDeclaredFields();
//                            listObjects.add(a);
//                            camposAnotacoes.add(campo.getName());
//                        } else if (nameAn.equals("ObjectLocal")) {
//                            objectLocal.add((Class<?>) campo.getType());
//                            camposAnotacoes.add(campo.getName());
//                        } else {
//                            camposAnotacoes.add(campo.getName());
//                            cp.add(campo.getName());
//                        }
//                    }
//                }
//            } else {
//                camposAnotacoes.add(campo.getName());
//                cp.add(campo.getName());
//            }
//        }
//    }


    private static PreparedStatement contructorCommand(Object clazz, Connection con, int type) throws SQLException, IllegalAccessException {

        PreparedStatement stmt = null;

        String sql = "";
        Field[] fields = clazz.getClass().getDeclaredFields();
        String strfield = "",strstatements = "";
        List<Object> liValues = new ArrayList<>();

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

                if(di.getAnnotation(Id.class) == null && di.getAnnotation(Ignore.class) == null && di.getAnnotation(ListObjectLocal.class
                ) == null && di.getAnnotation(ObjectLocal.class) == null){
                    di.setAccessible(true);
                    strfield += di.getName() + ",";
                    strstatements += "?,";
                    liValues.add(di.get(clazz));
                    i++;
                }
            }
            strstatements = strstatements.substring(0,strstatements.length() - 1);
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
                if(di.getAnnotation(Id.class) == null && di.getAnnotation(Ignore.class) == null && di.getAnnotation(ListObjectLocal.class
                ) == null && di.getAnnotation(ObjectLocal.class) == null){
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
