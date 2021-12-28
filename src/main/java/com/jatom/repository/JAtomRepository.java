package com.jatom.repository;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface JAtomRepository {

    /**
     * Método responsável por fazer inserção no banco
     *
     * O uso deste método é recomendado quando é um objeto simples e que não contenha instancias de outras classes do seu projeto
     *
     * É possível utiliza-lo quando contenha variaveis anotadas com @ListObjetc ou @SimpleObject porém os mesmos não serão inseridos
     *
     * Este médodo ganha em performance, porém quando há anotações @listObject ou @SimpleObject ele não insere.
     *
     * @param obj objeto que será inserido no banco
     * @param con conexão aberta
     * @return
     */
    public int insertedOne(Object obj, Connection con) throws SQLException, IllegalAccessException;


    /**
     * Método responsável por fazer inserção no banco
     *
     * Este médodo faz a inserção completa no banco, até dos objetos que possuem as anotações @listObject e @SimpleObject
     *
     * Atenção muito cuidado ao fazer uma referencia circular pois pode ocorrer de um loop.
     *
     * @param obj
     * @param con
     * @return
     */
    @Deprecated
    public int inserted(Object obj, Connection con)throws SQLException, IllegalAccessException;

    /**
     * Método responsável por fazer edição dos registros no banco
     *
     * O uso deste método é recomendado quando é um objeto simples e que não contenha instancias de outras classes do seu projeto
     *
     * É possível utiliza-lo quando contenha variaveis anotadas com @ListObjetc ou @SimpleObject porém os mesmos não serão editados
     *
     * Este médodo ganha em performance, porém quando há anotações @listObject ou @SimpleObject ele não edita.
     *
     * @param obj objeto que será inserido no banco
     * @param con conexão aberta
     * @return
     */
    public void editingOne(Object obj, Connection con)throws SQLException, IllegalAccessException;


    /**
     * Método responsável por fazer edição de registros no banco
     *
     * Este médodo faz a edição completa no banco, até dos objetos que possuem as anotações @listObject e @SimpleObject
     *
     * Atenção muito cuidado ao fazer uma referencia circular pois pode ocorrer de um loop.
     *
     * @param obj
     * @param con
     * @return
     */
    public void editing(Object obj, Connection con) throws SQLException, IllegalAccessException;


    /**
     * Método Responsável por fazer a exclusão de um objeto
     *
     * @param con Conexão aberta
     * @param className Nome da classe ou tabela no banco
     * @param identity campo de identidade
     * @param obj id da identidade
     */
    public void deleted(Connection con, String className, String identity, Object obj)throws SQLException, IllegalAccessException;

    /**
     * método que retorna uma lista de registros
     *
     * @param clazz Classe mapeada
     * @param con Conexão aberta
     * @param sql Código SQL
     * @return
     */
    public Object getAll(Class clazz,Connection con, String sql)throws SQLException, IllegalAccessException;

    /**
     * Método que retorna um registro do banco
     *
     * @param cazz Classe mapeada
     * @param con Conexão aberta
     * @param sql Código SQL
     * @return
     * @throws SQLException
     */
    public Object getOne(Class cazz,Connection con, String sql) throws SQLException, IllegalAccessException;

    /**
     * Método que possíbilita executar comandos sql
     *
     * @param sql Código sql a ser executado
     * @throws SQLException
     */
    public void executeQuery(String sql) throws SQLException, IllegalAccessException;
    /**
     * Método que possíbilita executar comandos sql
     *
     * @param con Conexão Aberta
     * @param sql Código sql a ser executado
     * @throws SQLException
     */
    public void executeQuery(Connection con, String sql) throws SQLException, IllegalAccessException;

    /**
     * Método responsável por salvar um objeto
     * @param obj
     */
    public void save(Object obj);

    public void operationPercistence(Object obj, Connection con, int type) throws IllegalAccessException, SQLException;

    public List<Object> getAll(String sql);

    public Object get(String sql);


}
