package com.jatom.migrationdatabase;

import com.jatom.ConnectionDatabase;
import com.jatom.enuns.JAtomTypes;
import com.jatom.model.JAtomParameters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class MigrationDataBase {

    Logger logger = Logger.getLogger(MigrationDataBase.class.getName());


    public void createInformationSchemaIntoSchema(){
        ConnectionDatabase connectionDatabase = new ConnectionDatabase();
        Connection con = null;
        con = connectionDatabase.openConnection();

        if(con != null){

            final List<Map<String, Object>> schemas = getSchemas(connectionDatabase.getPrefixschema());
            final String sql = "CREATE TABLE IF NOT EXISTS schema_version(\n" +
                    "\n" +
                    "    id serial primary key,\n" +
                    "    description varchar(300),\n" +
                    "    fileName varchar(300),\n" +
                    "    dataexecution TIMESTAMP\n" +
                    ");";
            schemas.forEach(item -> {
                String db = item.get("schema_name").toString();

                MigrationDataBaseService migrationDataBaseService = new MigrationDataBaseService();
                migrationDataBaseService.executeQuery(sql,db);

            });
        }
    }

    public void executeMigrationDataBaseResourcesIntoSchema(){
        createInformationSchemaIntoSchema();
        ConnectionDatabase connectionDatabase = new ConnectionDatabase();
        Connection con = null;
        con = connectionDatabase.openConnection();

        if(con != null){

            if(connectionDatabase.getPrefixschema() != null){
                final List<Map<String, Object>> schemas = getSchemas(connectionDatabase.getPrefixschema());

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

                        if(!verifySqlExecuted(db,obj)){
                            MigrationDataBaseService migrationDataBaseService = new MigrationDataBaseService();
                            migrationDataBaseService.executeQuery(finalSql,db);

                            String nameFile = obj.split("__")[1].replace(".sql","").replace("_"," ");
                            String insetLog = "INSERT INTO schema_version(description,filename,dataexecution) VALUES('"+nameFile+"','"+ obj +"',current_timestamp)";
                            migrationDataBaseService.executeQuery(insetLog,db);

                            logger.info("\n" + db +" - " + finalSql1);
                        }
                    });
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
                    String finalSql = sql;
                    String finalSql1 = sql;

                    String db = connectionDatabase.getUrl().split("/")[connectionDatabase.getUrl().split("/").length - 1];

                    if(!verifySqlExecuted(db,obj)){
                        MigrationDataBaseService migrationDataBaseService = new MigrationDataBaseService();


                        String nameFile = obj.split("__")[1].replace(".sql","").replace("_"," ");
                        String insetLog = "INSERT INTO schema_version(description,filename,dataexecution) VALUES('"+nameFile+"','"+ obj +"',current_timestamp)";

                        if(db != null && db.equals("")){
                            migrationDataBaseService.executeQuery(finalSql,db);
                            migrationDataBaseService.executeQuery(insetLog,db);
                        }
                        else{
                            migrationDataBaseService.executeQuery(finalSql);
                            migrationDataBaseService.executeQuery(insetLog);
                        }

                        logger.info("\n" + db +" - " + finalSql1);
                    }

                });
            }

        } else {
            logger.severe("Falha na conexão com o banco de dados");
        }

    }

    private boolean verifySqlExecuted(String db,String filename){
        boolean ret = false;
        Object obj = null;

        MigrationDataBaseService migrationDataBaseService = new MigrationDataBaseService();

        JAtomParameters jAtomParameters = new JAtomParameters();
        jAtomParameters.put(JAtomTypes.SQL, "SELECT * from schema_version where filename = '"+ filename +"'");
        if(db != null && db.equals(""))
            jAtomParameters.put(JAtomTypes.DB_NAME,db);

        obj = migrationDataBaseService.get(jAtomParameters);

        if(obj != null)
            ret = true;

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
