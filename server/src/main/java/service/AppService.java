package service;

import dataaccess.AppDAO;
import dataaccess.exceptions.DataAccessException;

public class AppService {
    final AppDAO appDAO;

    public AppService(AppDAO appDAO) {
        this.appDAO = appDAO;
    }

    public void clear() throws DataAccessException {
        this.appDAO.clear();
    }
}
