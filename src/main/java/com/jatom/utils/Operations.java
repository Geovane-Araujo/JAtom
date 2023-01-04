package com.jatom.utils;

import com.jatom.anotations.*;
import com.jatom.model.GlobalVariables;
import com.jatom.model.JAtomResults;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Operations extends GlobalVariables {

    protected static void operationPersistence(Object obj, Connection con, int type) throws Exception {

        Object id = 0;
        ResultSet rs = null;
        Field aux = null;
        boolean noEntity = false;

        if(obj.getClass().getAnnotation(NoEntity.class) == null){

            // coloca o id no objeto para ter como retorno
            aux = Arrays.stream(obj.getClass().getDeclaredFields()).filter(item -> item.getAnnotation(Id.class) != null).findFirst().orElse(null);
            aux.setAccessible(true);

            PreparedStatement stmt = contructorCommand(obj,con,type);
            stmt.execute();
            rs = stmt.getGeneratedKeys();
            while (rs.next()) {
                id = rs.getInt(1);
            }
            aux.set(obj,id);
        } else{
            // pega o ID na variavel de FK quando for noEntity
            aux = Arrays.stream(obj.getClass().getDeclaredFields()).filter(item -> item.getAnnotation(Fk.class) != null).findFirst().orElse(null);
            aux.setAccessible(true);

            if(aux != null){
                id = aux.get(obj);
            }
            noEntity = true;
        }

        String colunaId = "";
        Boolean recurse = true;

        while(recurse){

            Field[] fields = obj.getClass().getDeclaredFields();
            for(Field fi : fields){

                if(!noEntity) {
                    if(fi.getAnnotation(Id.class) != null){//#1
                        if(fi.getAnnotation(Id.class).value().equals(""))
                            colunaId = fi.getName();
                        else
                            colunaId = fi.getAnnotation(Id.class).value();
                    }
                } else {
                    if(fi.getAnnotation(Fk.class) != null){//#1
                        if(fi.getAnnotation(Fk.class).value().equals(""))
                            colunaId = fi.getName();
                        else
                            colunaId = fi.getAnnotation(Fk.class).value();
                    }
                }


                if(fi.getAnnotation(SimpleObject.class) != null){//#2

                    fi.setAccessible(true);

                    Object classObjFilho = fi.get(obj);

                    if(classObjFilho != null){


                        String finalColunaId = colunaId;
                        Field fieldFk = Arrays.stream(classObjFilho.getClass().getDeclaredFields()).filter(item -> item.getAnnotation(Fk.class) != null && item.getAnnotation(Fk.class).value().equals(finalColunaId)).findFirst().orElse(null);

                        if(fieldFk != null){
                            fieldFk.setAccessible(true);
                            fieldFk.set(classObjFilho,id);
                        }

                        operationPersistence(classObjFilho,con,type);
                    }
                }
                if(fi.getAnnotation(ListObject.class) != null){

                    fi.setAccessible(true);
                    List<?> classListObjFilho = (List<?>) fi.get(obj);

                    if(classListObjFilho != null){

                        for(Object classObjFilho : classListObjFilho){

                            String finalColunaId = colunaId;
                            Field fieldFk = Arrays.stream(classObjFilho.getClass().getDeclaredFields()).filter(item -> item.getAnnotation(Fk.class) != null && item.getAnnotation(Fk.class).value().equals(finalColunaId)).findFirst().orElse(null);

                            if(fieldFk != null){
                                fieldFk.setAccessible(true);
                                fieldFk.set(classObjFilho,id);
                            }

                            operationPersistence(classObjFilho,con,type);
                        }
                    }
                }
            }
            recurse = false;
        }
    }
    protected static PreparedStatement contructorCommand(Object clazz, Connection con, int type) throws Exception {

        PreparedStatement stmt = null;

        String sql = "";
        Field[] fields = clazz.getClass().getDeclaredFields();
        String strfield = "",strstatements = "";
        List<Object> liValues = new ArrayList<>();// pensar sobre esdte

        String className = "";

        //Aqui pega o nome da tabela, caso não tiver o alias pega o nome da classe
        if(clazz.getClass().getAnnotation(TableName.class) != null){
            className = clazz.getClass().getAnnotation(TableName.class).value();
        }
        else {
            className = clazz.getClass().getSimpleName();
        }

        //insert
        if(type == 0){
            int i = 1;
            for (Field di: fields) {

                if(di.getAnnotation(Id.class) == null && di.getAnnotation(Ignore.class) == null && di.getAnnotation(ListObject.class) == null && di.getAnnotation(SimpleObject.class) == null){
                    di.setAccessible(true);
                    if(di.getAnnotation(Alias.class) != null)
                        strfield += di.getAnnotation(Alias.class).value() + ",";
                    else
                        strfield += di.getName() + ",";
                    strstatements += "?,";
                    liValues.add(di.get(clazz));
                    i++;
                }
            }
            strstatements = strstatements.substring(0,strstatements.length() - 1);//verificar que pode ter forma melhor
            strfield = strfield.substring(0,strfield.length() - 1);

            sql =  String.format("INSERT INTO %s(%s) VALUES(%s);",className,strfield,strstatements);
            stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            i = 1;
            for (Object ob: liValues) {
                stmt.setObject(i,ob);
                i++;
            }
        }
        else {
            String identifield = "";
            Object vlIdentifield = 0;
            int i = 1;
            for (Field di: fields) {
                if(di.getAnnotation(Id.class) == null && di.getAnnotation(Ignore.class) == null && di.getAnnotation(ListObject.class
                ) == null && di.getAnnotation(SimpleObject.class) == null){
                    di.setAccessible(true);
                    if(di.getAnnotation(Alias.class) != null)
                        strfield += di.getAnnotation(Alias.class).value() + "=?,";
                    else
                        strfield += di.getName() + "=?,";
                    liValues.add(di.get(clazz));
                    i++;
                }
                if(di.getAnnotation(Id.class) != null){
                    di.setAccessible(true);
                    identifield = di.getName();
                    vlIdentifield = di.get(clazz);

                }
            }
            strfield = strfield.substring(0,strfield.length() - 1);

            sql =  String.format("UPDATE %s SET %s WHERE %s = ?;",className,strfield,identifield);
            stmt = con.prepareStatement(sql);

            i = 1;
            for (Object ob: liValues) {
                stmt.setObject(i,ob);
                i++;
            }
            stmt.setObject((liValues.size() + 1),vlIdentifield);
        }

        return stmt;
    }
    protected static <T> T operationGet(Connection con, Object obj) throws Exception{

        boolean recursive = true;
        String sql = "";

        Field fieldId = Arrays.stream(obj.getClass().getDeclaredFields()).filter(item -> item.getAnnotation(Id.class) != null).findFirst().orElse(null);
        String entityIdName = (fieldId.getAnnotation(Id.class).value().equals("") ? fieldId.getName() : fieldId.getAnnotation(Id.class).value());
        fieldId.setAccessible(true);



        Object valueId = fieldId.get(obj);

        while (recursive){

            Field[] fieldsPai = obj.getClass().getDeclaredFields();

            for (Field field: fieldsPai) {
                field.setAccessible(true);
                if(field.getAnnotation(SimpleObject.class) != null){

                    Field fkReference = Arrays.stream(field.get(obj).getClass().getDeclaredFields()).filter(item -> item.getAnnotation(Fk.class) != null && item.getAnnotation(Fk.class).value().equals(entityIdName)).findFirst().orElse(null);

                    sql = constructQuery(field.get(obj).getClass(),fkReference.getName(),valueId);
                    Object value = execute(con,sql,field.get(obj).getClass());

                    operationGet(con,value);
                    field.set(obj,value);
                }
                else if(field.getAnnotation(ListObject.class) != null){

                    List<?> classListObjFilho = (List<?>) field.get(obj);

                    Type fi = ((ParameterizedTypeImpl) field.getGenericType()).getActualTypeArguments()[0];
                    Field fkReference = Arrays.stream(((Class) fi).getDeclaredFields()).filter(item -> item.getAnnotation(Fk.class) != null && item.getAnnotation(Fk.class).value().equals(entityIdName)).findFirst().orElse(null);

                    sql = constructQuery(((Class) fi),fkReference.getName(),valueId);

                    Object value = execute(con,sql,((Class) fi));

                    for (T a :(List<T>) value) {
                        operationGet(con,a);
                    }

                    field.set(obj,value);
                }
            }
            recursive = false;
        }


        return (T)obj;
    }
    protected static <T> T execute(Connection con,String sql, Class clazz) throws Exception {

        List<Object> results = new ArrayList<>();
        List<String> columns = new ArrayList<>();


        stmt = con.prepareStatement(sql);
        rs = stmt.executeQuery();

        ResultSetMetaData rsm = rs.getMetaData();
        String col = "";

        for(int i = 1;i <= rsm.getColumnCount();i++){
            col = rsm.getColumnName(i);
            columns.add(col);
        }
        JAtomResults obj = null;
        while (rs.next()){
            obj = new JAtomResults();
            for(int i = 0; i < columns.size();i++){
                obj.put(columns.get(i),rs.getObject(columns.get(i)));
            }

            if(clazz != null){
                results.add(gson.fromJson(gson.toJson(obj),clazz));
            } else {
                results.add(obj);
            }
        }

        if(results.size() > 1)
            return (T)results;
        else if(results.size() == 1)
            return (T)results.get(0);
        else
            return null;

    }

    public static void execute(Connection con,String sql) throws SQLException {

        stmt = con.prepareStatement(sql);
        stmt.execute();
    }
    protected static String constructQuery(Class clazz,String coumnId, Object valueId) throws Exception {

        String sqlJoins = "";


        Object ob = clazz.getDeclaredConstructor().newInstance();
        String tablename = (clazz.getAnnotation(TableName.class) == null ? clazz.getSimpleName() : ob.getClass().getAnnotation(TableName.class).value());

        sqlJoins = onJoin(ob.getClass(),tablename, coumnId, valueId);


        return sqlJoins;
    }
    protected static String onJoin(Class clazz,String tableName) throws Exception {

        String join = "";
        String tableUnion = "";
        String sql = "";
        String col = "";


        List<Field> fields = Arrays.stream(clazz.getDeclaredFields()).filter(obj -> obj.getAnnotation(Join.class) != null).collect(Collectors.toList());

        for (Field f: fields) {

            Object c = f.getAnnotation(Join.class).reference().getDeclaredConstructor().newInstance();

            if(c.getClass().getAnnotation(TableName.class) == null){
                tableUnion = c.getClass().getSimpleName();
            } else {
                tableUnion = c.getClass().getAnnotation(TableName.class).value();
            }

            join += " INNER JOIN  " + tableUnion + " ON " +tableName + "."+f.getAnnotation(Join.class).columnReference() + " = " + tableUnion + "."+onJoinId(c.getClass()) + " \n";


            col += ", " + tableUnion + "." + f.getAnnotation(Join.class).columnName() + " as " + f.getName();

        }

        if(!col.equals(""))
            sql = "SELECT "+ tableName + ".* "+col+" FROM "+ tableName + join;
        else
            sql = "SELECT "+ tableName + ".* FROM "+ tableName;
        return sql;
    }
    protected static String onJoin(Class clazz,String tableName, String coumnId,Object valueId) throws Exception {

        String join = "";
        String tableUnion = "";
        String sql = "";
        String col = "";


        List<Field> fields = Arrays.stream(clazz.getDeclaredFields()).filter(obj -> obj.getAnnotation(Join.class) != null).collect(Collectors.toList());

        for (Field f: fields) {

            Object c = f.getAnnotation(Join.class).reference().getDeclaredConstructor().newInstance();

            if(c.getClass().getAnnotation(TableName.class) == null){
                tableUnion = c.getClass().getSimpleName();
            } else {
                tableUnion = c.getClass().getAnnotation(TableName.class).value();
            }

            join += " INNER JOIN  " + tableUnion + " ON " +tableName + "."+f.getAnnotation(Join.class).columnReference() + " = " + tableUnion + "."+onJoinId(c.getClass()) + " \n";


            col += ", " + tableUnion + "." + f.getAnnotation(Join.class).columnName() + " as " + f.getName();

        }

        if(!col.equals(""))
            sql = "SELECT "+ tableName + ".* "+col+" FROM "+ tableName + join +" WHERE "+tableName+ "." + coumnId + " = '" + valueId +"'";
        else
            sql = "SELECT "+ tableName + ".* FROM "+ tableName + " WHERE " + coumnId + " = '" + valueId +"'";
        return sql;
    }
    private static String onJoinId(Class c){

        String idName = "";

        Field f = Arrays.stream(c.getDeclaredFields()).filter(obj -> obj.getAnnotation(Id.class) != null).findFirst().orElse(null);

        if(!f.getAnnotation(Id.class).value().equals(""))
            idName = f.getAnnotation(Id.class).value();
        else
            idName = f.getName();

        return idName;
    }
}
