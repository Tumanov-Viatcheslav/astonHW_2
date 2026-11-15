package org.astonHW.dao;

import jakarta.persistence.PersistenceException;
import org.astonHW.entities.User;
import org.astonHW.exceptions.DaoException;
import org.astonHW.exceptions.UserNotFoundException;
import org.astonHW.utils.HibernateSessionFactoryUtil;

import org.hibernate.Session;
import org.hibernate.Transaction;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    @Override
    public User create(User user) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(user);
                transaction.commit();
                return user;
            } catch (RuntimeException e) {
                try {
                    transaction.rollback();
                } catch (Exception ex) {
                    logger.warn("Rollback failed", ex);
                }
                throw translate(e, "'create'");
            }

        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.find(User.class, id));
        } catch (RuntimeException e) {
            throw translate(e, "'findById'");
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            return session
                    .createQuery("from User order by id", User.class)
                    .getResultList();
        } catch (RuntimeException e) {
            throw translate(e, "'findAll'");
        }
    }

    @Override
    public User update(User user) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            User userFromDB = session.find(User.class, user.getId());
            if (userFromDB == null) {
                throw new UserNotFoundException(user.getId());
            }
            userFromDB.setName(user.getName());
            userFromDB.setEmail(user.getEmail());
            userFromDB.setAge(user.getAge());
            session.merge(userFromDB);
            transaction.commit();
            return userFromDB;
        } catch (RuntimeException e) {
            throw translate(e, "update");
        }
    }

    @Override
    public boolean deleteById(Long id) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            User userFromDB = session.find(User.class, id);
            if (userFromDB == null) {
                try {
                    transaction.rollback();
                } catch (Exception ex) {
                    logger.warn("Rollback failed", ex);
                }
                return false;
            }
            session.remove(userFromDB);
            transaction.commit();
            return true;
        } catch (RuntimeException e) {
            throw translate(e, "delete");
        }
    }

    private DaoException translate(RuntimeException e, String operation) {
        if (e instanceof ConstraintViolationException cve) {
            String constraint = cve.getConstraintName();
            logger.warn("Constraint Violation {}: {}", constraint, cve.getMessage());
            return new DaoException(String.format("Data Base Constraint Violation (%s) executing %s", constraint, operation), e);
        }

        if (e instanceof PersistenceException) {
            return new DaoException(String.format("ORM error executing %s", operation), e);
        }

        return new DaoException(String.format("Error executing %s: %s", operation, e.getMessage()), e);
    }
}
