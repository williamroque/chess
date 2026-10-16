package dataaccess.memory;

import dataaccess.ApplicationDAO;

public class MemoryApplicationDAO implements ApplicationDAO {
    final MemoryGameDAO gameDAO;
    final MemoryUserDAO userDAO;

    MemoryApplicationDAO(MemoryGameDAO gameDAO, MemoryUserDAO userDAO) {
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    @Override
    public void clear() {
        this.gameDAO.clear();
        this.userDAO.clear();
    }
}
