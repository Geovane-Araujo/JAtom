package com.pain_crud;

import java.sql.Connection;
import java.sql.SQLException;

public interface MethodsCrud {

    // inserts

    /**
     * Método responsável por fazer o insert no banco de dados, este método mapeia a classe pegando todas as classes filhas
     * seja List ou um objeto Simples
     * @param obj objeto a ser inserido
     * @param clazz Classe Principal do Objeto
     * @param db String com o nome do Banco de Dados
     * @param connectionsDatabases Objeto com os Atributos de Conexão
     * @return Retorna o Obeeto
     * @throws SQLException
     */
    //public Object insertedAll(Object obj, Class clazz,String db, ConectionsDatabases connectionsDatabases) throws SQLException;

    /**
     * Método responsável por fazer o insert no banco de dados, este método mapeia a classe pegando todas as classes filhas
     * seja List ou um objetoSimples este método pega uma conexão aberta
     * @param obj Objeto
     * @param clazz Classe do Objeto
     * @param con Conexão Aberta
     * @return retorna um inteiro
     * @throws SQLException
     */

    //public Object insertedAll(Object obj, Class clazz, Connection con) throws SQLException;
    /**
     *  Método responsável por fazer o insert, só que diferente do All ele ignora as anotações ListObjects e Object
     *
     * @param obj objeto a ser inserido
     * @param clazz Classe Principal do Objeto
     * @param db String com o nome do Banco de Dados
     * @param connectionsDatabases Objeto com os Atributos de Conexão
     * @return
     * @throws SQLException
     */
    public int insertedOne(Object obj, Class clazz, Connection con) throws SQLException, IllegalAccessException;

    /**
     *  Método responsável por fazer o insert de uma lista de objectos, só que diferente do All ele ignora as anotações ListObjects e Object
     *
     * @param obj objeto a ser inserido
     * @param clazz Classe Principal do Objeto
     * @param db String com o nome do Banco de Dados
     * @param connectionsDatabases Objeto com os Atributos de Conexão
     * Não há retorno
     * @throws SQLException
     */
    public void listInsertedOne(Object[] obj, Class clazz, Connection con) throws SQLException, IllegalAccessException;
    /**
     *  Método responsável por fazer o insert, só que diferente do All ele ignora as anotações ListObjects e Object
     *
     * @param obj objeto a ser inserido
     * @param clazz Classe Principal do Objeto
     * @return
     * @throws SQLException
     */
    //Editing
    /**
     *  Método responsável por fazer o Editing, só que diferente do All ele ignora as anotações ListObjects e Object
     *
     * @param obj objeto a ser inserido
     * @param clazz Classe Principal do Objeto
     * @param idobject id do item a ser editado
     * @return
     * @throws SQLException
     */
    public int editingOne(Object obj, Class clazz, Connection con,int idobject) throws SQLException, IllegalAccessException;

    /**
     * Método Responsável por fazer a exclusão de um objeto
     *
     * @param con Conexão Aberta
     * @param idobject id do item a ser excluido
     * @param className Nome da Tabela
     * @return
     * @throws SQLException
     */
    public Object deleted(Connection con,int idobject, String className) throws SQLException;


    public Object getAll(Class clazz,Connection con,String sql) throws SQLException;

    public Object getOne(Class cazz,Connection con,String sql) throws SQLException;
}
