package com.jatom.migrationdatabase.postgres;

import com.jatom.exceptions.ServiceException;
import com.jatom.utils.Atom;
import com.jatom.utils.Operations;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

class MigrationDataBaseService extends Atom {

    Logger logger = Logger.getLogger(MigrationDataBaseService.class.getName());

    public void executeQuery(Connection con, String sql){
        try {
            Operations.execute(con,sql);
        } catch (SQLException e) {
            logger.log(Level.INFO,"Não foi possível fazer a operação " + e.getMessage());
            throw new ServiceException("BAD_REQUEST","Não foi possível fazer a operação " + e.getMessage(),e);
        }
    }

}
