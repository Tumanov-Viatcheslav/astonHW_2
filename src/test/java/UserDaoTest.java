import org.astonHW.dao.UserDao;
import org.astonHW.dao.UserDaoImpl;
import org.astonHW.entities.User;
import org.astonHW.exceptions.DaoException;
import org.astonHW.utils.HibernateSessionFactoryUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class UserDaoTest {
    @Container
    static final PostgreSQLContainer POSTGRES =
            new PostgreSQLContainer("postgres:latest")
                    .withDatabaseName("testdb")
                    .withUsername("testuser")
                    .withPassword("testpassword");

    private static SessionFactory sessionFactory;
    private UserDao userDao;

    @BeforeAll
    static void initSessionFactory() {
        Configuration cfg = new Configuration()
                .addAnnotatedClass(User.class)
                .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                .setProperty("hibernate.connection.url", POSTGRES.getJdbcUrl())
                .setProperty("hibernate.connection.username", POSTGRES.getUsername())
                .setProperty("hibernate.connection.password", POSTGRES.getPassword())
                .setProperty("hibernate.hbm2ddl.auto", "create-drop")
                .setProperty("hibernate.show_sql", "false");

        sessionFactory = cfg.buildSessionFactory();
        HibernateSessionFactoryUtil.setSessionFactory(sessionFactory);
    }

    @AfterAll
    static void closeSessionFactory() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
        HibernateSessionFactoryUtil.setSessionFactory(null);
    }

    @BeforeEach
    void cleanDatabase() {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.createNativeMutationQuery("TRUNCATE TABLE users RESTART IDENTITY CASCADE")
                    .executeUpdate();
            tx.commit();
        }
        userDao = new UserDaoImpl();
    }

    @Nested
    @DisplayName("Create user method - 'create'")
    class CreateUser {
        @Test
        @DisplayName("User creation test")
        void create_whenOk() {
            User user = userDao.create(new User("ValidName", "valid@email.com", 20));

            assertNotNull(user.getId());
            assertNotNull(user.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("Find user method - 'findById'")
    class FindUser {
        @Test
        @DisplayName("Existing user search test")
        void findById_whenUserExist_returnUser() {
            User user = userDao.create(new User("ValidName", "valid@email.com", 20));

            Optional<User> found = userDao.findById(user.getId());

            assertTrue(found.isPresent());
            assertEquals("ValidName", found.get().getName());
            assertEquals("valid@email.com", found.get().getEmail());
            assertEquals(20, found.get().getAge());
        }

        @Test
        @DisplayName("No such user search test")
        void findById_whenUserDoesNotExist_returnEmpty() {
            Optional<User> found = userDao.findById(4L);

            assertFalse(found.isPresent());
        }
    }

    @Nested
    @DisplayName("Find all users method - 'findAll'")
    class FindAllUsers {
        @Test
        @DisplayName("Existing users show list test")
        void findAll_whenUserExist_returnUser() {
            User user1 = userDao.create(new User("ValidName1", "valid1@email.com", 20));
            User user2 = userDao.create(new User("ValidName2", "valid2@email.com", 21));
            User user3 = userDao.create(new User("ValidName3", "valid3@email.com", 22));

            List<User> users = userDao.findAll();

            assertFalse(users.isEmpty());
            assertEquals(3, users.size());
            assertEquals(user1.getId(), users.getFirst().getId());
            assertEquals(user2.getId(), users.get(1).getId());
            assertEquals(user3.getId(), users.get(2).getId());
        }

        @Test
        @DisplayName("No users show list test")
        void findAll_whenNoUsersExist_returnEmpty() {
            List<User> found = userDao.findAll();

            assertTrue(found.isEmpty());
        }
    }

    @Nested
    @DisplayName("Update user method - 'update'")
    class UpdateUser {
        @Test
        @DisplayName("User update test")
        void update_whenOk() {
            User user = userDao.create(new User("ValidName", "valid@email.com", 20));
            Instant createdAt = user.getCreatedAt();

            user.setName("NewValidName");
            user.setEmail("newvalid@email.com");
            user.setAge(22);

            User updatedUser = userDao.update(user);

            assertEquals("NewValidName", updatedUser.getName());
            assertEquals("newvalid@email.com", updatedUser.getEmail());
            assertEquals(22, updatedUser.getAge());

            assertEquals(createdAt, updatedUser.getCreatedAt());
        }

        @Test
        @DisplayName("User to update doesn't exist test")
        void update_whenUserDoesNotExist_throwsException() {
            assertThrows(
                    DaoException.class,
                    () -> userDao.update(new User(1L, "ValidName", "valid@email.com", 20))
            );
        }
    }

    @Nested
    @DisplayName("Delete user method - 'deleteById'")
    class DeleteUser {
        @Test
        @DisplayName("Existing user deletion test")
        void deleteById_whenUserExist_returnTrue() {
            User user = userDao.create(new User("ValidName", "valid@email.com", 20));

            boolean deleted = userDao.deleteById(user.getId());

            assertTrue(deleted);
        }

        @Test
        @DisplayName("No such user deletion test")
        void deleteById_whenUserDoesNotExist_returnFalse() {
            boolean deleted = userDao.deleteById(4L);

            assertFalse(deleted);
        }
    }
}
