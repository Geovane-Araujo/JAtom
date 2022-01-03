package com.jatom.model;

import com.google.gson.Gson;
import com.jatom.ConnectionDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GlobalVariables {

    protected Connection con = null;
    protected Boolean collect = false;
    protected PreparedStatement stmt = null;
    protected Gson gson = new Gson();
    protected ResultSet rs = null;
    protected ConnectionDatabase connectionDatabase = new ConnectionDatabase();
}
