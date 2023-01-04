package com.jatom.migrationdatabase.postgres;

import com.jatom.connections.postgres.ConnectionDatabase;
import com.jatom.enuns.JAtomTypes;
import com.jatom.enuns.ServiceCodeException;
import com.jatom.exceptions.ServiceException;
import com.jatom.model.JAtomParameters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MigrationDataBase {

    Logger logger = Logger.getLogger(MigrationDataBase.class.getName());
    private String db = null;

    public MigrationDataBase(){
    }


    public MigrationDataBase(String db) {
        this.db = db;
    }

    public void createInformationSchemaIntoSchema(){
        ConnectionDatabase connectionDatabase = new ConnectionDatabase();
        Connection con = null;
        if(db == null)
            con = connectionDatabase.openConnection();
        else
            con = connectionDatabase.openConnection(db);

        if(con != null){
            final String sql = "CREATE TABLE IF NOT EXISTS schema_version(\n" +
                    "\n" +
                    "    id serial primary key,\n" +
                    "    description varchar(300),\n" +
                    "    fileName varchar(300),\n" +
                    "    dataexecution TIMESTAMP\n" +
                    ");";
            if(ConnectionDatabase.schema && ConnectionDatabase.multitenant){
                final List<Map<String, Object>> schemas = getSchemas(connectionDatabase.getPrefixschema());
                schemas.forEach(item -> {
                    String db = item.get("schema_name").toString();
                    Connection finalCon  = connectionDatabase.openConnection(db);
                    MigrationDataBaseService migrationDataBaseService = new MigrationDataBaseService();
                    migrationDataBaseService.executeQuery(finalCon,sql);

                });
            } else if(!ConnectionDatabase.multitenant && ConnectionDatabase.schema){
                MigrationDataBaseService migrationDataBaseService = new MigrationDataBaseService();
                migrationDataBaseService.executeQuery(con,sql);
            } else {
                MigrationDataBaseService migrationDataBaseService = new MigrationDataBaseService();
                migrationDataBaseService.executeQuery(con,sql);
            }
        }
    }

    public void executeMigrationDataBaseResourcesIntoSchema(){
        createInformationSchemaIntoSchema();
        ConnectionDatabase connectionDatabase = new ConnectionDatabase();

        try{
            if(ConnectionDatabase.schema && ConnectionDatabase.multitenant){

                if(ConnectionDatabase.prefixschema == null || ConnectionDatabase.prefixschema.isEmpty()){
                    throw  new ServiceException(ServiceCodeException.BAD_REQUEST.toString(),"Prefixschema is required",null);
                }

                final List<Map<String, Object>> schemas = getSchemas(ConnectionDatabase.prefixschema);

                List<String> listFiles = filesName();
                listFiles.stream().forEach(obj -> {
                    String sql = "";
                    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                    InputStream in = classLoader.getResourceAsStream("database/"+obj);
                    if(in != null){
                        sql = stringInputStream(in);
                    }
                    String finalSql = sql;
                    String finalSql1 = sql;
                    schemas.forEach(item -> {

                        String db = item.get("schema_name").toString();

                        Connection con = connectionDatabase.openConnection(db);

                        if(!verifySqlExecuted(con,obj)){
                            MigrationDataBaseService migrationDataBaseService = new MigrationDataBaseService();
                            migrationDataBaseService.executeQuery(finalSql,db);

                            String nameFile = obj.split("__")[1].replace(".sql","").replace("_"," ");
                            String insetLog = "INSERT INTO schema_version(description,filename,dataexecution) VALUES('"+nameFile+"','"+ obj +"',current_timestamp)";
                            migrationDataBaseService.executeQuery(con,insetLog);
                            logger.log(Level.INFO,"\n" + db +" - " + finalSql1);
                        }
                    });
                });
            } else if(!ConnectionDatabase.multitenant && ConnectionDatabase.schema){

                if(ConnectionDatabase.schemaName == null || ConnectionDatabase.schemaName.isEmpty()){
                    throw new ServiceException(ServiceCodeException.BAD_REQUEST.toString(),"SchemaName is required",null);
                }

                List<String> listFiles = filesName();
                listFiles.stream().forEach(obj -> {
                    String sql = "";
                    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                    InputStream in = classLoader.getResourceAsStream("database/"+obj);
                    if(in != null){
                        sql = stringInputStream(in);
                    }

                    Connection con = connectionDatabase.openConnection(ConnectionDatabase.schemaName);

                    if(!verifySqlExecuted(con,obj)){
                        MigrationDataBaseService migrationDataBaseService = new MigrationDataBaseService();

                        String nameFile = obj.split("__")[1].replace(".sql","").replace("_"," ");
                        String insetLog = "INSERT INTO schema_version(description,filename,dataexecution) VALUES('"+nameFile+"','"+ obj +"',current_timestamp)";

                        migrationDataBaseService.executeQuery(con,sql);
                        migrationDataBaseService.executeQuery(con,insetLog);
                        logger.log(Level.INFO,"\n" + db +" - " + sql);
                    }

                });
            }
            else{

                List<String> listFiles = filesName();
                listFiles.stream().forEach(obj -> {
                    String sql = "";
                    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                    InputStream in = classLoader.getResourceAsStream("database/"+obj);
                    if(in != null){
                        sql = stringInputStream(in);
                    }
                    Connection con = null;
                    if(db == null)
                        con = connectionDatabase.openConnection();
                    else
                        con = connectionDatabase.openConnection(db);

                    if(!verifySqlExecuted(con,obj)){
                        MigrationDataBaseService migrationDataBaseService = new MigrationDataBaseService();

                        String nameFile = obj.split("__")[1].replace(".sql","").replace("_"," ");
                        String insetLog = "INSERT INTO schema_version(description,filename,dataexecution) VALUES('"+nameFile+"','"+ obj +"',current_timestamp)";

                        migrationDataBaseService.executeQuery(con,sql);
                        migrationDataBaseService.executeQuery(con,insetLog);


                        logger.log(Level.INFO,"\n" + db +" - " + sql);
                    }
                });
            }
        }catch (Exception ex){
            logger.log(Level.INFO, ex.getMessage(),ex);
        }
    }

    private boolean verifySqlExecuted(Connection con,String filename) {
        boolean ret = false;
        int qtds = 0;

        String sql = "SELECT count(1)as count from schema_version where filename = '"+ filename +"'";

        try{
            PreparedStatement smtp = con.prepareStatement(sql);
            ResultSet rs = smtp.executeQuery();

            while (rs.next()){
                qtds = rs.getInt(1);
            }

            if(qtds > 0){
                ret = true;
            }

            rs.close();
            smtp.close();
        } catch (SQLException ex){
            logger.log(Level.INFO, ex.getMessage(),ex);
        }
        return ret;
    }
    private List<Map<String, Object>> getSchemas(String prefixschema) {
        Object schemas = null;


        MigrationDataBaseService migrationDataBaseService = new MigrationDataBaseService();

        JAtomParameters jAtomParameters = new JAtomParameters();
        jAtomParameters.put(JAtomTypes.SQL, "SELECT schema_name FROM information_schema.schemata where schema_name like '%"+prefixschema+"%'");

        schemas = migrationDataBaseService.get(jAtomParameters);

        if(schemas instanceof ArrayList){
            return (List<Map<String, Object>>)schemas;
        } else {
            List<Map<String, Object>> ret = new ArrayList<>();
            if(schemas != null){
                ret.add((Map<String, Object>)schemas);
            }
            return ret;
        }
    }

    private String stringInputStream(InputStream in) {
        String text = "";
        try (InputStreamReader streamReader = new InputStreamReader(in, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {

            String line;

            while ((line = reader.readLine()) != null) {
                text += line + "\n";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    private List<String> filesName(){
        List<String> filesName = new ArrayList<>();

        try {
            InputStream in = getResourceAsStream("database");
            if(in == null) {
                return filesName;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String resource = "";

            while ((resource = br.readLine()) != null){
                filesName.add(resource);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return filesName;
    }

    private InputStream getResourceAsStream(String resource) {
        final InputStream in
                = getContextClassLoader().getResourceAsStream(resource);

        return in == null ? getClass().getResourceAsStream(resource) : in;
    }

    private ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }


}
