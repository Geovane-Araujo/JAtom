package com.jatom.utils;

import com.jatom.anotations.TableName;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;

class Removeimpl {

    private String filter = "";

    private Connection con = null;

    private Class clazz = null;

    public Removeimpl() {
    }

    public Removeimpl(String filter, Connection con, Class clazz) {
        this.filter = filter;
        this.con = con;
        this.clazz = clazz;
    }

    public void remove() throws SQLException {

        try {
            String className = "";
            Object cls = null;
            cls = clazz.getDeclaredConstructor().newInstance();
            if(con != null){
                String sql;
                if(cls.getClass().getAnnotation(TableName.class) != null){
                    className = cls.getClass().getAnnotation(TableName.class).value();
                }
                else {
                    className = cls.getClass().getSimpleName();
                }

                filter = SqlUtils.filter(filter);
                sql =  String.format("DELETE FROM %s WHERE %s",className,filter);

                Operations.execute(con,sql);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

    }


}
