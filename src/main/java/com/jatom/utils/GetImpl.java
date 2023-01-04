package com.jatom.utils;

import java.sql.Connection;
import java.sql.SQLException;

class GetImpl {

    private String filter = "";

    private Connection con = null;

    private Class clazz = null;

    public GetImpl() {
    }

    public GetImpl(String filter, Connection con, Class clazz) {
        this.filter = filter;
        this.con = con;
        this.clazz = clazz;
    }

    public void find() throws SQLException {

        String className = SqlUtils.getClassName(clazz);

        if(con != null){
            String sql;
            filter = SqlUtils.filter(filter);
            sql =  String.format("SELECT * FROM %s WHERE %s",className,filter);
            Operations.execute(con,sql);
        }


    }


}
