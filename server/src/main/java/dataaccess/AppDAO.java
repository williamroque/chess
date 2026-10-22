package dataaccess;

import dataaccess.exceptions.DataAccessException;

public interface AppDAO {
    void clear() throws DataAccessException;
}
