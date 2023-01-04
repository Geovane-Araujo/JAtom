package com.jatom.utils.services;

import com.jatom.anotations.TableName;
import com.jatom.utils.Operations;
import com.jatom.utils.SqlUtils;

import java.sql.Connection;
import java.sql.SQLException;

public class Remove {

    private String filter = "";

    private Connection con = null;

    private Class clazz = null;

    public Remove() {
    }

    public Remove(String filter, Connection con, Class clazz) {
        this.filter = filter;
        this.con = con;
        this.clazz = clazz;
    }

    public void remove() throws SQLException {
        String className = "";
        if(con != null){
            String sql;
            if(clazz.getClass().getAnnotation(TableName.class) != null){
                className = clazz.getClass().getAnnotation(TableName.class).value();
            }
            else {
                className = clazz.getClass().getSimpleName();
            }

            filter = SqlUtils.filter(filter);
            sql =  String.format("DELETE FROM %s WHERE %s",className,filter);

            Operations.execute(con,sql);
        }
    }


}
