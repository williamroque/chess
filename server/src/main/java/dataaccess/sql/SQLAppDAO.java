package dataaccess.sql;

import dataaccess.AppDAO;

public class SQLAppDAO implements AppDAO {
    final SQLUserDAO userDAO;
    final SQLGameDAO gameDAO;

    public SQLAppDAO(SQLUserDAO userDAO, SQLGameDAO gameDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
    }

    @Override
    public void clear() {
        this.gameDAO.clear();
        this.userDAO.clear();
    }
}
