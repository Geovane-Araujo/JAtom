package com.jatom.utils;

import com.jatom.ConnectionDatabase;
import java.util.logging.Logger;

public class InitialConnection {

    Logger logger = Logger.getLogger(InitialConnection.class.getPackage().getName());

    public void initialSession(){
        ConnectionDatabase con = new ConnectionDatabase();
        try{
            con.openConnection();
            logger.info("Initial connection successful");
        } catch (Exception ex){
            logger.info(ex.getMessage());
        }
    }
}
