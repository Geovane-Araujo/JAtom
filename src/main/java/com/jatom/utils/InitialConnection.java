package com.jatom.utils;

import com.jatom.connections.postgres.ConnectionDatabase;
import java.util.logging.Logger;

public class InitialConnection {

    Logger logger = Logger.getLogger(InitialConnection.class.getName());

    public void initialSession(){
        ConnectionDatabase con = new ConnectionDatabase();
        try{
            con.openConnection();
            logger.info("Initial connection successful");
        } catch (Exception ex){
            logger.severe(ex.getMessage());
        }
    }
}
