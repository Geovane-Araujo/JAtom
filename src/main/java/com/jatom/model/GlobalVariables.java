package com.jatom.model;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jatom.connections.postgres.ConnectionDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GlobalVariables {

    protected Connection con = null;
    protected Boolean collect = false;
    protected static PreparedStatement stmt = null;
    protected static Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();
    protected static ResultSet rs = null;
    protected ConnectionDatabase connectionDatabase = new ConnectionDatabase();

}
