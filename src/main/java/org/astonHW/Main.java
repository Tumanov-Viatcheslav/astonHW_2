package org.astonHW;


import org.astonHW.dao.UserDao;
import org.astonHW.dao.UserDaoImpl;
import org.astonHW.services.UserService;
import org.astonHW.ui.ConsoleUI;
import org.astonHW.utils.HibernateSessionFactoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        UserDao userDao = new UserDaoImpl();
        UserService userService = new UserService(userDao);
        try (Scanner scanner = new Scanner(System.in)) {
            new ConsoleUI(userService, scanner).run();
        } catch (Exception e) {
            logger.error("Error", e);
            e.printStackTrace();
        } finally {
            HibernateSessionFactoryUtil.close();
            logger.info("App closed");
        }
    }
}