package service;

import dataaccess.DatabaseManager;
import dataaccess.exceptions.*;
import dataaccess.memory.MemoryAppDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import org.junit.jupiter.api.*;
import shared.GameServiceTests;

import java.lang.reflect.InvocationTargetException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameServiceWithMemoryTests extends GameServiceTests {
    @BeforeAll
    public static void init() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        initializeWith(MemoryUserDAO.class, MemoryGameDAO.class, MemoryAppDAO.class);
    }
}
