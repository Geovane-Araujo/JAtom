package com.jatom.model;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jatom.ConnectionDatabase;
import com.jatom.exceptions.Logs;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GlobalVariables {

    protected Connection con = null;
    protected Boolean collect = false;
    protected PreparedStatement stmt = null;
    protected Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();
    protected ResultSet rs = null;
    protected ConnectionDatabase connectionDatabase = new ConnectionDatabase();

}
