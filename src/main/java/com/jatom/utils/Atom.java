package com.jatom.utils;

import com.jatom.anotations.Id;
import com.jatom.connections.postgres.ConnectionDatabase;
import com.jatom.enuns.JAtomTypes;
import com.jatom.exceptions.ServiceException;
import com.jatom.migrationdatabase.postgres.MigrationDataBase;
import com.jatom.model.JAtomParameters;
import com.jatom.repository.JAtomRepository;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Atom implements JAtomRepository<Object> {

    Logger logger = Logger.getLogger(InitialConnection.class.getName());
    ConnectionDatabase connectionDatabase = new ConnectionDatabase();

    private boolean executeMigration = false;

    private void verifyMigration(String db){
        if(!executeMigration){
            MigrationDataBase mig = new MigrationDataBase(db);
            mig.executeMigrationDataBaseResourcesIntoSchema();
            executeMigration = true;
        }
    }

    @Override
    public void executeQuery(String sql){

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
            ConnectionDatabase.onCloseConnection(con);
        }
    }

    @Override
    public void executeQuery(String sql, String db) {
        Connection con = null;

        try {
            verifyMigration(db);
            con = connectionDatabase.openConnection(db);
            PreparedStatement stmt = null;
            stmt = con.prepareStatement(sql);
            stmt.execute();
            stmt.close();

        } catch (SQLException e) {
            logger.severe("Não foi possível fazer a operação " + e.getMessage());
            throw new ServiceException("BAD_REQUEST","Não foi possível fazer a operação " + e.getMessage(),e);
        } finally {
            ConnectionDatabase.onCloseConnection(con);
        }
    }

    @Override
    public void save(Object obj){

        Connection con = null;
        String columnId = "";
        
        Field fieldIdentifier = Arrays.stream(obj.getClass().getDeclaredFields()).filter(item -> item.getAnnotation(Id.class) != null).findFirst().orElse(null);

        try{
            verifyMigration(null);
            con = connectionDatabase.openConnection();
            con.setAutoCommit(false);
            fieldIdentifier.setAccessible(true);
            if(fieldIdentifier.get(obj) == null || fieldIdentifier.get(obj).equals("") || fieldIdentifier.get(obj).toString().equals("0"))
                Operations.operationPersistence(obj,con,0);
            else
                Operations.operationPersistence(obj,con,1);

            con.commit();

        } catch (Exception ex){
            ConnectionDatabase.onRollback(con);
            logger.log(Level.INFO,"Não foi possível fazer a operação " + ex.getMessage());
            throw new ServiceException("BAD_REQUEST","Não foi possível fazer a operação " + ex.getMessage(),ex);
        } finally {
            ConnectionDatabase.onCloseConnection(con);
        }
    }

    @Override
    public void save(Object obj, String db){

        Connection con = null;
        
        Field fieldIdentifier = Arrays.stream(obj.getClass().getDeclaredFields()).filter(item -> item.getAnnotation(Id.class) != null).findFirst().orElse(null);

        try{
            verifyMigration(db);
            con = connectionDatabase.openConnection(db);
            con.setAutoCommit(false);
            fieldIdentifier.setAccessible(true);
            if(fieldIdentifier.get(obj) == null || fieldIdentifier.get(obj).equals("") || fieldIdentifier.get(obj).toString().equals("0"))
                Operations.operationPersistence(obj,con,0);
            else
                Operations.operationPersistence(obj,con,1);

            con.commit();

        } catch (Exception ex){
            ConnectionDatabase.onRollback(con);
            logger.severe("Não foi possível fazer a operação " + ex.getMessage());
            throw new ServiceException("BAD_REQUEST","Não foi possível fazer a operação " + ex.getMessage(),ex);
        } finally {
            ConnectionDatabase.onCloseConnection(con);
        }
    }

    @Override
    public Connection save(Object obj, Connection con, boolean finishTransaction) {

        verifyMigration(null);
        String columnId = "";
        
        Field fieldIdentifier = Arrays.stream(obj.getClass().getDeclaredFields()).filter(item -> item.getAnnotation(Id.class) != null).findFirst().orElse(null);

        try{
            if(con == null){
                con = connectionDatabase.openConnection();
                con.setAutoCommit(false);
            }

            fieldIdentifier.setAccessible(true);
            if(fieldIdentifier.get(obj) == null || fieldIdentifier.get(obj).equals("") || fieldIdentifier.get(obj).toString().equals("0"))
                Operations.operationPersistence(obj,con,0);
            else
                Operations.operationPersistence(obj,con,1);

            if(finishTransaction)
                con.commit();

        } catch (Exception ex){
            ConnectionDatabase.onRollback(con);
            logger.severe("Não foi possível fazer a operação " + ex.getMessage());
            throw new ServiceException("BAD_REQUEST","Não foi possível fazer a operação " + ex.getMessage(),ex);
        } finally {
            ConnectionDatabase.onCloseConnection(con);
        }

        return con;
    }

    @Override
    public Connection save(Object obj, String db, Connection con, boolean finishTransaction) {

        String columnId = "";
        
        Field fieldIdentifier = Arrays.stream(obj.getClass().getDeclaredFields()).filter(item -> item.getAnnotation(Id.class) != null).findFirst().orElse(null);

        try{
            if(con == null){
                con = connectionDatabase.openConnection(db);
                con.setAutoCommit(false);
            }

            fieldIdentifier.setAccessible(true);
            if(fieldIdentifier.get(obj) == null || fieldIdentifier.get(obj).equals("") || fieldIdentifier.get(obj).toString().equals("0"))
                Operations.operationPersistence(obj,con,0);
            else
                Operations.operationPersistence(obj,con,1);

            if(finishTransaction)
                con.commit();

        } catch (Exception ex){
            ConnectionDatabase.onRollback(con);
            logger.severe("Não foi possível fazer a operação " + ex.getMessage());
            throw new ServiceException("BAD_REQUEST","Não foi possível fazer a operação " + ex.getMessage(),ex);
        } finally {
            ConnectionDatabase.onCloseConnection(con);
        }

        return con;
    }

    @Override
    public <T> T get(JAtomParameters jAtomParameters)  {


        Connection con = null;
        // palavras reservadas
        verifyMigration(null);
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
                return (T)Operations.execute(con,sql,clazz);
            }


        } catch (Exception  ex){
            logger.severe("Não foi possível fazer a operação " + ex.getMessage());
            throw new ServiceException("BAD_REQUEST","Não foi possível fazer a operação " + ex.getMessage(),ex);
        } finally {
            ConnectionDatabase.onCloseConnection(con);
        }

        return null;
    }

    @Override
    @Deprecated
    public <T> T getByID(Class clazz, Object id) {

        String columnId =  "";
        Object obj = null;
        Connection con = null;
        try{
            verifyMigration(null);
            obj= clazz.getDeclaredConstructor().newInstance();
            con = connectionDatabase.openConnection();

            Field fieldpai = Arrays.stream(clazz.getDeclaredFields()).filter(item -> item.getAnnotation(Id.class) != null).findFirst().orElse(null);
            if(fieldpai != null){
                columnId =  fieldpai.getName();
            } else
                return null;

            String sql = Operations.constructQuery(clazz,columnId, id);

            obj = Operations.execute(con,sql,clazz);

            Operations.operationGet(con,obj);


        } catch (Exception e) {
            logger.severe("Não foi possível fazer a operação " + e.getMessage());
            throw new ServiceException("BAD_REQUEST","Não foi possível fazer a operação " + e.getMessage(),e);
        } finally {
            ConnectionDatabase.onCloseConnection(con);
        }
        return (T)obj;
    }

    @Override
    public <T> T getByID(Class clazz, Object id, String db) {
        return null;
    }

    @Override
    public void remove(Class clazz,String db, String filter){
        Connection con = null;
        try{

            con = connectionDatabase.openConnection(db);
            verifyMigration(db);
            Removeimpl removeimpl = new Removeimpl(filter, con, clazz);
            removeimpl.remove();

        } catch (Exception ex){
            logger.log(Level.INFO,"Não foi possível fazer a operação " + ex.getMessage());
            throw new ServiceException("BAD_REQUEST","Não foi possível fazer a operação " + ex.getMessage(),ex);
        } finally {
            ConnectionDatabase.onCloseConnection(con);
        }
    }

    @Override
    public void remove(Class clazz, String filter){
        Connection con = null;
        try{

            con = connectionDatabase.openConnection();
            verifyMigration(null);
            Removeimpl removeimpl = new Removeimpl(filter, con, clazz);
            removeimpl.remove();

        } catch (Exception ex){
            logger.log(Level.INFO,"Não foi possível fazer a operação " + ex.getMessage());
            throw new ServiceException("BAD_REQUEST","Não foi possível fazer a operação " + ex.getMessage(),ex);
        } finally {
            ConnectionDatabase.onCloseConnection(con);
        }
    }

    @Override
    public <T> T find(Class clazz,String filter) {

//        String columnId =  "";
//        Object obj = null;
//        Connection con = null;
//        try{
//            verifyMigration(null);
//            obj= clazz.getDeclaredConstructor().newInstance();
//            con = connectionDatabase.openConnection();
//
//            Field fieldpai = Arrays.stream(clazz.getDeclaredFields()).filter(item -> item.getAnnotation(Id.class) != null).findFirst().orElse(null);
//            if(fieldpai != null){
//                columnId =  fieldpai.getName();
//            } else
//                return null;
//
//            String sql = Operations.constructQuery(clazz,columnId, id);
//
//            obj = Operations.execute(con,sql,clazz);
//
//            Operations.operationGet(con,obj);
//
//
//        } catch (Exception e) {
//            logger.severe("Não foi possível fazer a operação " + e.getMessage());
//            throw new ServiceException("BAD_REQUEST","Não foi possível fazer a operação " + e.getMessage(),e);
//        } finally {
//            ConnectionDatabase.onCloseConnection(con);
//        }
//        return (T)obj;
        return null;
    }

    @Override
    public <T> T find(Class clazz,String db,String filter) {
        return null;
    }
}
