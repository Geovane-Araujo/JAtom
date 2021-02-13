package com.pain_crud.Metodos;

import com.pain_crud.conections.ConectionsDatabases;
import com.pain_crud.interfaces.MethodsCrud;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.*;
import java.util.*;

@RestController
public class Crud implements MethodsCrud {

    String sql = "";
    PreparedStatement stmt = null;
    ResultSet rs = null;

    Hashtable retorno = new Hashtable();

    @Override
    public Object insertedAll(Object obj, Class clazz, String db, ConectionsDatabases conectionsDatabases)
            throws SQLException {

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
        List<Class<?>> pilha = new ArrayList<>();

        separaObject(objectLocal, cp, listObjects, campos, camposAnotacoes, ids);

        try {
            con = conectionsDatabases.newDbConection(db);

            // Insere Sempre na Tabela Principal Primeiro
            int id = montaStatement(campos, classe, obj, con, cp, 1, 0);

            int anotacao = 0;
            if (objectLocal.size() > 0) {
                for (Class<?> cl : objectLocal) {
                    Field[] cpobjLocal = cl.getDeclaredFields();
                    // montaStatement(cpobjLocal,cl.getClass(),obj,con);
                    System.out.println(cpobjLocal.getClass());
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            con.close();
        }
        return null;
    }

    @Override
    public Object insertedOne(Object obj, Class clazz, String db, ConectionsDatabases connectionsDatabases)
            throws SQLException {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        Hashtable retorno = new Hashtable();

        Class<?> classe = obj.getClass();
        Field[] campos = classe.getDeclaredFields();

        List<String> cp = new ArrayList<String>();

        List<String> camposAnotacoes = new ArrayList<String>();

        List<String> ids = new ArrayList<String>();
        List<Class<?>> listObjects = new ArrayList<Class<?>>();
        List<Class<?>> objectLocal = new ArrayList<Class<?>>();
        List<Class<?>> pilha = new ArrayList<>();

        separaObject(objectLocal, cp, listObjects, campos, camposAnotacoes, ids);

        try {
            con = connectionsDatabases.newDbConection(db);

            // Insere Sempre na Tabela Principal Primeiro
            int id = montaStatement(campos, classe, obj, con, cp, 1, 0);
            retorno.put("ret", "success");
            retorno.put("msg", "ok");
            retorno.put("obj", obj);
            retorno.put("id", id);
        } catch (SQLException e) {
            retorno.put("ret", "unsuccess");
            retorno.put("msg", e.getMessage());
            retorno.put("obj", obj);
        } finally {
            con.close();
        }
        return retorno;
    }

    @Override
    public Object insertedOne(Object obj, Class clazz, Connection con) throws SQLException {

        Class<?> classe = obj.getClass();
        Field[] campos = classe.getDeclaredFields();

        List<String> cp = new ArrayList<String>();

        List<String> camposAnotacoes = new ArrayList<String>();

        List<String> ids = new ArrayList<String>();
        List<Class<?>> listObjects = new ArrayList<Class<?>>();
        List<Class<?>> objectLocal = new ArrayList<Class<?>>();
        List<Class<?>> pilha = new ArrayList<>();

        separaObject(objectLocal, cp, listObjects, campos, camposAnotacoes, ids);

        // Insere Sempre na Tabela Principal Primeiro
        int id = montaStatement(campos, classe, obj, con, cp, 1, 0);
        retorno.put("ret", "success");
        retorno.put("msg", "ok");
        retorno.put("obj", obj);
        retorno.put("id", id);

        return retorno;
    }

    @Override
    public Object editingOne(Object obj, Class clazz, Connection con, int idobject) throws SQLException {

        Class<?> classe = obj.getClass();
        Field[] campos = classe.getDeclaredFields();

        List<String> cp = new ArrayList<String>();

        List<String> camposAnotacoes = new ArrayList<String>();

        List<String> ids = new ArrayList<String>();
        List<Class<?>> listObjects = new ArrayList<Class<?>>();
        List<Class<?>> objectLocal = new ArrayList<Class<?>>();
        List<Class<?>> pilha = new ArrayList<>();

        separaObject(objectLocal, cp, listObjects, campos, camposAnotacoes, ids);

        // Insere Sempre na Tabela Principal Primeiro
        montaStatement(campos, classe, obj, con, cp, 2, idobject);
        retorno.put("ret", "success");
        retorno.put("msg", "ok");
        retorno.put("obj", obj);
        retorno.put("id", idobject);

        return retorno;
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
    public Object getAll(Connection con, String sql) throws SQLException {

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

        Map objR ;
        List<Object> obj = new ArrayList<>();
        while (rs.next()){
            objR = new LinkedHashMap<String, Object>();
            for (String cabe: cabecalho) {
                objR.put(cabe,rs.getObject(cabe));
            }
            obj.add(objR);
        }
        return obj;
    }

    @Override
    public Object insertedAll(Object obj, Class clazz, Connection con) throws SQLException {

        Class<?> classe = obj.getClass();
        Field[] campos = classe.getDeclaredFields();

        List<String> cp = new ArrayList<String>();

        List<String> camposAnotacoes = new ArrayList<String>();

        List<String> ids = new ArrayList<String>();
        List<Class<?>> listObjects = new ArrayList<Class<?>>();
        List<Class<?>> objectLocal = new ArrayList<Class<?>>();
        List<Class<?>> pilha = new ArrayList<>();

        separaObject(objectLocal, cp, listObjects, campos, camposAnotacoes, ids);

        try {
            // Insere Sempre na Tabela Principal Primeiro
            int id = montaStatement(campos, classe, obj, con, cp, 1, 0);

            int anotacao = 0;
            if (objectLocal.size() > 0) {
                for (Class<?> cl : objectLocal) {
                    Field[] cpobjLocal = cl.getDeclaredFields();
                    // montaStatement(cpobjLocal,cl.getClass(),obj,con);
                    System.out.println(cpobjLocal.getClass());
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            con.close();
        }
        return null;
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

    public int montaStatement(Field[] campos, Class<?> classe, Object obj, Connection con, List<String> cp, int tipo,
            int idobject) throws SQLException {

        PreparedStatement stmt = null;
        ResultSet rs = null;

        sql = constructorQuery(tipo, cp, classe.getSimpleName(), idobject);
        stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        int i = 1;
        List<Field> notAnotation = retiraAnotacao(campos, 1);

        for (Field campo : notAnotation) {
            try {
                campo.setAccessible(true);
                stmt.setObject(i, campo.get(obj));
                i++;
            } catch (IllegalAccessException | SQLException e) {
                System.out.println(e);
            }
        }

        stmt.execute();
        rs = stmt.getGeneratedKeys();
        int id = 0;
        while (rs.next()) {
            id = rs.getInt(1);
        }

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
