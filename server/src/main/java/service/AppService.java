package service;

import dataaccess.AppDAO;

public class AppService {
    final AppDAO appDAO;

    public AppService(AppDAO appDAO) {
        this.appDAO = appDAO;
    }

    public void clear() {
        this.appDAO.clear();
    }
}
