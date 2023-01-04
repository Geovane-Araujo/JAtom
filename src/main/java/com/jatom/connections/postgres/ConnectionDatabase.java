package com.jatom.connections.postgres;

import com.jatom.exceptions.ServiceException;
import com.jatom.utils.InitialConnection;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionDatabase {

    Logger logger = Logger.getLogger(InitialConnection.class.getName());

    public static boolean runMigration = false;

    private String drive = "";

    private String password = "";

    private String url = "";

    private String user = "";

    public static boolean schema = false;

    public static String prefixschema = "";

    public static boolean multitenant;

    public static String schemaName;




    public String getUrl() {
        return url;
    }

    public String getPrefixschema() {
        return prefixschema;
    }

    public Connection openConnection() {

        try{
            this.load();
            Class.forName(drive);
            if(!password.isEmpty() && !user.isEmpty())
                return DriverManager.getConnection(url,user,password);
            else
                return DriverManager.getConnection(url);

        } catch (Exception ex){
            String message = "Não foi possível conectar a base de dados: " + ex.getMessage();
            logger.severe(message);
            throw new ServiceException("BAD_REQUEST",message,ex);
        }
    }

    public Connection openConnection(String db) {


        try{
            this.load();
            Class.forName(drive);
            if(!password.isEmpty() && !user.isEmpty()){

                if(schema){
                    return DriverManager.getConnection(url+"?currentSchema="+db,user,password);
                } else
                    return DriverManager.getConnection(url+"/"+db,user,password);
            }
            else
                return DriverManager.getConnection(url);

        } catch (Exception ex){
            String message = "Não foi possível conectar a base de dados: " + ex.getMessage();
            logger.log(Level.SEVERE,message);
            throw new ServiceException("BAD_REQUEST",message,ex);
        }
    }

    public static void onCloseConnection(Connection con){
        try{
            con.close();
        } catch (SQLException ex){
            throw new ServiceException("BAD_REQUEST","Não foi possível fazer a operação " + ex.getMessage(),ex);
        }
    }

    public static void onRollback(Connection con){
        try{
            con.rollback();
        } catch (SQLException ex){
            throw new ServiceException("BAD_REQUEST","Não foi possível fazer a operação " + ex.getMessage(),ex);
        }
    }

    private void load() {

        try{
            PropertiesConfiguration config = new PropertiesConfiguration();
            config.load("application.properties");

            drive = config.getString("org.jatom.connection.driver",null);
            url = config.getString("org.jatom.connection.url",null);
            password = config.getString("org.jatom.connection.password",null);
            user = config.getString("org.jatom.connection.user",null);
            schema = config.getBoolean("org.jatom.connection.schema",false);
            prefixschema = config.getString("org.jatom.connection.prefixschema",null);
            multitenant = config.getBoolean("org.jatom.connection.multi-tenant",false);
            schemaName = config.getString("org.jatom.connection.schema-name",null);

        } catch (Exception ex){
            String message = "Não foi possível conectar a base de dados: ";
            logger.severe(message);
            throw new ServiceException("BAD_REQUEST",message,ex);
        }


    }

}
