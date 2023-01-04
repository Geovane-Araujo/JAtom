package com.jatom.repository;

import com.jatom.model.JAtomParameters;

import java.sql.Connection;
import java.sql.SQLException;

public interface JAtomRepository<T> {

    /**
     * Método que possíbilita executar comandos sql
     *
     * @param sql Código sql a ser executado
     * @throws SQLException
     */
    void executeQuery(String sql);

    /**
     * Método que possíbilita executar comandos sql
     *
     * @param sql Código sql a ser executado
     * @param db Banco de dados a ser executado
     */
    void executeQuery(String sql, String db);

    /**
     * Método responsável por salvar um objeto
     * @param obj
     */
    void save(Object obj);

    /**
     *  @param obj
     * @param db
     */
    void save(Object obj, String db);

    /**
     * Método responsável por salvar um objeto
     * @param obj
     */
    Connection save(Object obj,Connection con, boolean finishTransaction);

    /**
     * Método responsável por salvar um objeto
     * @param obj
     */
    Connection save(Object obj, String db,Connection con, boolean finishTransaction);

    <T extends Object> T get(JAtomParameters jAtomParameters);

    <T extends Object> T getByID(Class clazz, Object id);

    <T extends Object> T getByID(Class clazz, Object id, String db);

    <T extends Object> T find(Class clazz,String filter);

    <T extends Object> T find(Class clazz,String db, String filter);

    void remove(Class clazz,String db, String filter);

    void remove(Class clazz, String filter);

}
