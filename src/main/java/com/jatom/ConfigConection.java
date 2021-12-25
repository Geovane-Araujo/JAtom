package com.jatom;

/**
 *
 * o Atributo instanceDb recebe um valor de 1 a 4 sendo
 * 1 - Postgres
 * 2 - SQL Server
 * 3 - MySql
 * 4 - SQlite
 */
public abstract class  ConfigConection {

    private String host = "";
    private String port = "";
    private String user = "";
    private String password = "";
    private String dbName = "";
    private int instanceDb = 0;


    public ConfigConection() {
    }

    public ConfigConection( String host, String port, String user, String password, String dbName, int instanceDb) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.dbName = dbName;
        this.instanceDb = instanceDb;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public int getInstanceDb() {
        return instanceDb;
    }

    public void setInstanceDb(int instanceDb) {
        this.instanceDb = instanceDb;
    }
}
