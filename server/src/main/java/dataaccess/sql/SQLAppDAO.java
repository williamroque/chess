package dataaccess.sql;

import dataaccess.AppDAO;
import dataaccess.exceptions.DataAccessException;

public class SQLAppDAO implements AppDAO {
    final SQLUserDAO userDAO;
    final SQLGameDAO gameDAO;

    public SQLAppDAO(SQLUserDAO userDAO, SQLGameDAO gameDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
    }

    @Override
    public void clear() throws DataAccessException {
        this.gameDAO.clear();
        this.userDAO.clear();
    }
}
