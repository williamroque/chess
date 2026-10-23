package dataaccess;

import dataaccess.sql.SQLAppDAO;
import dataaccess.sql.SQLGameDAO;
import dataaccess.sql.SQLUserDAO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import shared.AppUserServiceTests;

import java.lang.reflect.InvocationTargetException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppUserServiceWithSQLTests extends AppUserServiceTests {
    @BeforeAll
    public static void init() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        initializeWith(SQLUserDAO.class, SQLGameDAO.class, SQLAppDAO.class);
    }
}
