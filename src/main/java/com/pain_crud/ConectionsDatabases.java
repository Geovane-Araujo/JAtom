package com.pain_crud;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConectionsDatabases extends ConfigConection{

    public ConectionsDatabases() {
    }

    public ConectionsDatabases(String host, String port, String user, String password, String dbName, int instanceDb) {
        super(host, port, user, password, dbName, instanceDb);
    }

    /**
     *
     *
     *   O Atributo instanceDb recebe um valor de 1 a 4 sendo
     *   1 - Postgres
     *   2 - SQL Server
     *   3 - MySql
     *   4 - SQlite
     * @return retorna true se a foi efetuada a conexão e false se houve algum problema, notar o console
     */
    public boolean openConnections(){

        boolean ret = false;

        if(super.getInstanceDb() == 1){
            try {
                Class.forName("org.postgresql.Driver");
                String url = "jdbc:postgresql://"+super.getHost()+":"+super.getPort()+"/";
                DriverManager.getConnection(url,super.getUser(), super.getPassword());
                ret = true;
            }
            catch (ClassNotFoundException  e){
                ret = false;
            }
            catch (SQLException ex){
                ret = false;
            }
        }

        return  ret;
    }

    /**
     *
     * @param db deverá receber uma String com o nome do banco de dados
     * @return
     */
    public  Connection newDbConection(String db) throws SQLException{
        Connection con = null;
        String url = "jdbc:postgresql://"+super.getHost()+":"+super.getPort()+"/";
        con = DriverManager.getConnection(url+db,super.getUser(),super.getPassword());
        return con;
    }

}
