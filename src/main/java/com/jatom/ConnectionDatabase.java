package com.jatom;

import com.jatom.exceptions.ServiceException;
import com.jatom.utils.InitialConnection;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class ConnectionDatabase {

    Logger logger = Logger.getLogger(InitialConnection.class.getName());
    private String drive = "";
    private String password = "";
    private String url = "";
    private String user = "";
    private boolean schema = false;

    private String prefixschema = "";


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
            try {
                throw new Exception(message);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
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
            logger.severe(message);
            try {
                throw new Exception(message);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void load() {

        try{
            PropertiesConfiguration config = new PropertiesConfiguration();
            config.load("application.properties");

            drive = config.getString("org.connection.jatom.driver",null);
            url = config.getString("org.connection.jatom.url",null);
            password = config.getString("org.connection.jatom.password",null);
            user = config.getString("org.connection.jatom.user",null);
            schema = config.getBoolean("org.connection.jatom.schema",null);
            prefixschema = config.getString("org.connection.jatom.prefixschema",null);

        } catch (Exception ex){
            String message = "Não foi possível conectar a base de dados: ";
            logger.severe(message);
            throw new ServiceException("BAD_REQUEST",message,ex);
        }


    }

}
