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




    public Connection openConnection(){

        this.load();
        try{
            Class.forName(drive);
            if(!password.isEmpty() && !user.isEmpty())
                return DriverManager.getConnection(url,user,password);
            else
                return DriverManager.getConnection(url);

        } catch (SQLException ex){
            new Throwable("Não foi possível conectar a base de dados");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Connection openConnection(String db){

        this.load();
        try{
            Class.forName(drive);
            if(!password.isEmpty() && !user.isEmpty())
                return DriverManager.getConnection(url+"/"+db,user,password);
            else
                return DriverManager.getConnection(url);

        } catch (SQLException ex){
            new Throwable("Não foi possível conectar a base de dados");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void load()  {

        try{
            PropertiesConfiguration config = new PropertiesConfiguration();
            config.load("application.properties");

            drive = config.getString("org.postgresql.Driver",null);
            url = config.getString("org.connection.jatom.url",null);
            password = config.getString("org.connection.jatom.password",null);
            user = config.getString("org.connection.jatom.user",null);

        } catch (ConfigurationException ex){
            new Throwable("Could not read as file properties");
        }


    }

}
