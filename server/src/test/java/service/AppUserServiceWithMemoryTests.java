package service;

import dataaccess.memory.MemoryAppDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import shared.AppUserServiceTests;
import org.junit.jupiter.api.*;

import java.lang.reflect.InvocationTargetException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppUserServiceWithMemoryTests extends AppUserServiceTests {
    @BeforeAll
    public static void init() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        initializeWith(MemoryUserDAO.class, MemoryGameDAO.class, MemoryAppDAO.class);
    }
}
