package com.jatom;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDatabase {


    private String drive = "";
    private String password = "";
    private String url = "";
    private String user = "";


    public Connection openConnection() throws Exception {

        this.load();
        try{
            Class.forName(drive);
            if(!password.isEmpty() && !user.isEmpty())
                return DriverManager.getConnection(url,user,password);
            else
                return DriverManager.getConnection(url);

        } catch (SQLException ex){
            System.err.println("Não foi possível conectar a base de dados: " + ex.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Não foi possível encontrar a classe de conexão: " + e.getMessage());
        }
        return null;
    }

    public Connection openConnection(String db) throws Exception {

        this.load();
        try{
            Class.forName(drive);
            if(!password.isEmpty() && !user.isEmpty())
                return DriverManager.getConnection(url+"/"+db,user,password);
            else
                return DriverManager.getConnection(url);

        } catch (SQLException ex){
            throw  new Exception("Não foi possível conectar a base de dados " + ex.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Class NotFound" + e.getMessage());
        }
        return null;
    }

    private void load() throws Exception {

        try{
            PropertiesConfiguration config = new PropertiesConfiguration();
            config.load("application.properties");

            drive = config.getString("org.connection.jatom.driver",null);
            url = config.getString("org.connection.jatom.url",null);
            password = config.getString("org.connection.jatom.password",null);
            user = config.getString("org.connection.jatom.user",null);

        } catch (ConfigurationException ex){
            throw  new Exception("Could not read as file properties " + ex.getMessage());
        }


    }

}
