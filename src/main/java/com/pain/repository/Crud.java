package com.pain.repository;

import com.pain.conections.ConectionsDatabases;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface Crud {

    public Object inserted(Object obj, Class clazz,String db, ConectionsDatabases connectionsDatabases) throws SQLException;
    public Object inserted(Object obj, Class clazz, Connection con) throws SQLException;
//    public Object deleted(Object obj,Class clazz,String db);
//    public List<Object> getAll(Object obj,Class clazz,String db);
//    public Object getById(long id,Class clazz,String db);
//    public Object searchByParameters(Object obj,Class clazz,String db);
}
