package dataaccess.memory;

import dataaccess.AppDAO;

public class MemoryAppDAO implements AppDAO {
    final MemoryUserDAO userDAO;
    final MemoryGameDAO gameDAO;

    public MemoryAppDAO(MemoryUserDAO userDAO, MemoryGameDAO gameDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
    }

    @Override
    public void clear() {
        this.gameDAO.clear();
        this.userDAO.clear();
    }
}
