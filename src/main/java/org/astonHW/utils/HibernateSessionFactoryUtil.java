package org.astonHW.utils;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HibernateSessionFactoryUtil {

    private static final Logger logger = LoggerFactory.getLogger(HibernateSessionFactoryUtil.class);

    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private HibernateSessionFactoryUtil() {
    }

    private static SessionFactory buildSessionFactory() {
        try {
            logger.info("SessionFactory initialization...");
            String url = System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://localhost:5433/astonhw");
            String user = System.getenv().getOrDefault("DB_USER", "myuser");
            String password = System.getenv().getOrDefault("DB_PASSWORD", "123");
            SessionFactory sessionFactory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .setProperty("hibernate.connection.url", url)
                    .setProperty("hibernate.connection.username", user)
                    .setProperty("hibernate.connection.password", password)
                    .buildSessionFactory();
            logger.info("SessionFactory created");
            return sessionFactory;
        } catch (HibernateException e) {
            logger.error("SessionFactory creation failed", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    public static void close() {
        logger.info("Closing SessionFactory...");
        SESSION_FACTORY.close();
    }
}
