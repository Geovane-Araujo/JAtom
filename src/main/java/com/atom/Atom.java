package com.atom;

import com.google.gson.Gson;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.*;
import java.util.*;

public class Atom implements MethodsCrud {

    String sql = "";
    PreparedStatement stmt = null;
    ResultSet rs = null;

    Hashtable retorno = new Hashtable();


    @Override
    public int insertedOne(Object obj, Class clazz, Connection con) throws SQLException, IllegalAccessException {

        Class<?> classe = obj.getClass();
        Field[] campos = classe.getDeclaredFields();

        List<String> cp = new ArrayList<String>();

        List<String> camposAnotacoes = new ArrayList<String>();

        List<String> ids = new ArrayList<String>();
        List<Class<?>> listObjects = new ArrayList<Class<?>>();
        List<Class<?>> objectLocal = new ArrayList<Class<?>>();

        separaObject(objectLocal, cp, listObjects, campos, camposAnotacoes, ids);

        // Insere Sempre na Tabela Principal Primeiro
        int id = montaStatement(campos, classe, obj, con, cp, 1, 0);

        return id;
    }

    @Override
    public int insertedOne(Object obj, Connection con) throws SQLException, IllegalAccessException {
        Class<?> classe = obj.getClass();
        Field[] campos = classe.getDeclaredFields();

        List<String> cp = new ArrayList<String>();

        List<String> camposAnotacoes = new ArrayList<String>();

        List<String> ids = new ArrayList<String>();
        List<Class<?>> listObjects = new ArrayList<Class<?>>();
        List<Class<?>> objectLocal = new ArrayList<Class<?>>();

        separaObject(objectLocal, cp, listObjects, campos, camposAnotacoes, ids);

        int id = montaStatement(campos, classe, obj, con, cp, 1, 0);

        return id;
    }

    @Override
    public void insertedOne(Object[] obj, Connection con) throws SQLException, IllegalAccessException {
        Class<?> classe = obj.getClass();
        Field[] campos = classe.getDeclaredFields();

        List<String> cp = new ArrayList<String>();

        List<String> camposAnotacoes = new ArrayList<String>();

        List<String> ids = new ArrayList<String>();
        List<Class<?>> listObjects = new ArrayList<Class<?>>();
        List<Class<?>> objectLocal = new ArrayList<Class<?>>();

        separaObject(objectLocal, cp, listObjects, campos, camposAnotacoes, ids);

        for(Object ob : obj){
            montaStatement(campos, classe, ob, con, cp, 1, 0);
        }
    }

    @Override
    public int editingOne(Object obj, Class clazz, Connection con, int idobject) throws SQLException, IllegalAccessException {

        Class<?> classe = obj.getClass();
        Field[] campos = classe.getDeclaredFields();

        List<String> cp = new ArrayList<String>();

        List<String> camposAnotacoes = new ArrayList<String>();

        List<String> ids = new ArrayList<String>();
        List<Class<?>> listObjects = new ArrayList<Class<?>>();
        List<Class<?>> objectLocal = new ArrayList<Class<?>>();

        separaObject(objectLocal, cp, listObjects, campos, camposAnotacoes, ids);

        montaStatement(campos, classe, obj, con, cp, 2, idobject);

        return idobject;
    }

    @Override
    public int editingOne(Object obj, Connection con, int idobject) throws SQLException, IllegalAccessException {

        Class<?> classe = obj.getClass();
        Field[] campos = classe.getDeclaredFields();

        List<String> cp = new ArrayList<String>();

        List<String> camposAnotacoes = new ArrayList<String>();

        List<String> ids = new ArrayList<String>();
        List<Class<?>> listObjects = new ArrayList<Class<?>>();
        List<Class<?>> objectLocal = new ArrayList<Class<?>>();

        separaObject(objectLocal, cp, listObjects, campos, camposAnotacoes, ids);

        montaStatement(campos, classe, obj, con, cp, 2, idobject);

        return idobject;
    }

    @Override
    public void editingOne(Object[] obj, Connection con) throws SQLException, IllegalAccessException {
        Class<?> classe = obj.getClass();
        Field[] campos = classe.getDeclaredFields();

        List<String> cp = new ArrayList<String>();

        List<String> camposAnotacoes = new ArrayList<String>();

        List<String> ids = new ArrayList<String>();
        List<Class<?>> listObjects = new ArrayList<Class<?>>();
        List<Class<?>> objectLocal = new ArrayList<Class<?>>();

        separaObject(objectLocal, cp, listObjects, campos, camposAnotacoes, ids);

        for(Object ob: obj){
            Field as = retid(campos);
            montaStatement(campos, classe, ob, con, cp, 2, Integer.parseInt(as.get(ob).toString()));
        }

    }

    @Override
    public Object deleted(Connection con, int idobject, String className) throws SQLException {

        // Insere Sempre na Tabela Principal Primeiro
        sql = constructorQuery(className, idobject);
        PreparedStatement stmt = null;

        stmt = con.prepareStatement(sql);
        stmt.execute();

        retorno.put("ret", "success");
        retorno.put("msg", "ok");
        retorno.put("id", idobject);

        return retorno;
    }

    @Override
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
    public void executeQuery(Connection con, String sql) throws SQLException {
        stmt = con.prepareStatement(sql);
        stmt.execute();
        stmt.close();
    }

    /**
     *
     * @param tipo      Tipo 1 - Insert, 2 - Update, 3 Delete e 4- Select
     * @param colunas   Colunas dos Campos do banco de dados que seão inseridos
     * @param className Nome da classe pois é o mesmo do banco de dados
     * @return Retorna uma String com a Query
     */
    public String constructorQuery(int tipo, List<String> colunas, String className, int id) {

        String sql = "";
        // inserted
        if (tipo == 1) {
            sql = "INSERT INTO " + className + " (";
            String atributos = "";
            String ins = "";
            for (String name : colunas) {
                atributos += name + ",";
                ins += "?,";
            }
            atributos = atributos.substring(0, atributos.length() - 1);
            ins = ins.substring(0, ins.length() - 1);
            sql += atributos + ") VALUES (" + ins + ")";

        } else if (tipo == 2) {
            sql = "UPDATE " + className + " SET ";
            String atributos = "";
            String ins = " WHERE ID = " + id;
            for (String name : colunas) {
                atributos += name + " = ?,";

            }
            atributos = atributos.substring(0, atributos.length() - 1);
            sql += atributos + ins;
        }
        return sql;
    }

    public String constructorQuery(String className, int id) {

        String sql = "";
        sql = "DELETE FROM " + className + " WHERE id =  " + id;

        return sql;
    }

    /**
     * Método Responsável por verificar as Anotações
     *
     * @param an     Aqui entra um Array, pois cada compo pode ter mais de uma
     *               anotação por campo
     * @param tipoAn Tipo da Anotação [ID, FK, ListObjects, Objects]
     * @return
     */
    public boolean verificaAnotacao(Annotation[] an, String tipoAn) {
        boolean ret = false;

        for (Annotation a : an) {
            Class<?> anotacao = a.annotationType();
            if (tipoAn.equals(anotacao.getSimpleName())) {
                return true;
            }
        }
        return ret;
    }

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

    public Field retid(Field[] campos) {
        Field ret = null;


        for (Field c : campos) {
            Annotation[] a = c.getDeclaredAnnotations();
            if (a.length > 0) {
                for (Annotation b : a) {
                    Class<?> d = b.annotationType();
                    if (d.getSimpleName().equals("Id")) {
                        ret = c;
                    }
                }
            }
        }


        return ret;
    }

    public int montaStatement(Field[] campos, Class<?> classe, Object obj, Connection con, List<String> cp, int tipo,
            int idobject) throws SQLException, IllegalAccessException {

        String alias = classe.getAnnotation(Alias.class).value();
        sql = constructorQuery(tipo, cp, alias, idobject);
        stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        int i = 1;
        List<Field> notAnotation = retiraAnotacao(campos, 1);

        for (Field campo : notAnotation) {
            campo.setAccessible(true);
            stmt.setObject(i, campo.get(obj));
            i++;
        }

        stmt.execute();
        rs = stmt.getGeneratedKeys();
        int id = 0;
        while (rs.next()) {
            id = rs.getInt(1);
        }
        rs.close();
        stmt.close();
        return id;
    }

    /**
     *
     * @param objectLocal     List referente a anotação objectLocal
     * @param cp              Array de string referente aos campos que não tem chave
     *                        nem outro tipo de relacionamento
     * @param listObjects     List Referente ao objeto que é um relacionaento de um
     *                        para muitos
     * @param campos          campos Field do objeto class
     * @param camposAnotacoes campos de anotações
     * @param ids             ids dos campos
     */
    public void separaObject(List<Class<?>> objectLocal, List<String> cp, List<Class<?>> listObjects, Field[] campos,
            List<String> camposAnotacoes, List<String> ids) {

        for (Field campo : campos) {
            campo.setAccessible(false);
            if (campo.getAnnotations().length > 0) {
                Annotation[] an = campo.getDeclaredAnnotations();
                if (an.length > 0) {
                    for (Annotation ano : an) {
                        Class<?> anotacao = ano.annotationType();
                        String nameAn = anotacao.getSimpleName();
                        if (!nameAn.equals("Ignore")) {
                            if (nameAn.equals("Id")) {
                                ids.add(campo.getName());
                            } else if (nameAn.equals("ListObjectLocal")) {
                                Class<?> a = (Class<?>) ((ParameterizedType) campo.getGenericType())
                                        .getActualTypeArguments()[0];
                                Field[] f = a.getDeclaredFields();
                                listObjects.add(a);
                                camposAnotacoes.add(campo.getName());
                            } else if (nameAn.equals("ObjectLocal")) {
                                objectLocal.add((Class<?>) campo.getType());
                                camposAnotacoes.add(campo.getName());
                            } else {
                                camposAnotacoes.add(campo.getName());
                                cp.add(campo.getName());
                            }
                        }
                    }
                }
            } else {
                camposAnotacoes.add(campo.getName());
                cp.add(campo.getName());
            }
        }
    }
}
