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

            final List<Map<String, Object>> schemas = getSchemas();
            final String sql = "CREATE TABLE IF NOT EXISTS schema_version(\n" +
                    "\n" +
                    "    id serial primary key,\n" +
                    "    description varchar(300),\n" +
                    "    fileName varchar(300),\n" +
                    "    dataexecution TIMESTAMP\n" +
                    ");";
            schemas.forEach(item -> {
                item.get("schema_name");
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

            final List<Map<String, Object>> schemas = getSchemas();

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
                    item.get("schema_name");
                    String db = item.get("schema_name").toString();

                    MigrationDataBaseService migrationDataBaseService = new MigrationDataBaseService();
                    migrationDataBaseService.executeQuery(finalSql,db);

                    String nameFile = obj.split("__")[1].replace(".sql","").replace("_"," ");
                    String insetLog = "INSERT INTO schema_version(description,filename,dataexecution) VALUES('"+nameFile+"','"+ obj +"',current_timestamp)";
                    migrationDataBaseService.executeQuery(insetLog,db);

                    logger.info("\n" + db +" - " + finalSql1);
                });
            });

        } else {
            logger.severe("Falha na conexão com o banco de dados");
        }

    }

    private List<Map<String, Object>> getSchemas() {
        Object schemas = null;


        MigrationDataBaseService migrationDataBaseService = new MigrationDataBaseService();

        JAtomParameters jAtomParameters = new JAtomParameters();
        jAtomParameters.put(JAtomTypes.SQL, "SELECT schema_name FROM information_schema.schemata where schema_name like '%hub_platform_%'");

        schemas = migrationDataBaseService.get(jAtomParameters);

        if(schemas instanceof ArrayList){
            return (List<Map<String, Object>>)schemas;
        } else {
            List<Map<String, Object>> ret = new ArrayList<>();
            ret.add((Map<String, Object>)schemas);
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
