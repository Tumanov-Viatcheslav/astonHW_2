import org.astonHW.dao.UserDao;
import org.astonHW.entities.User;
import org.astonHW.services.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void printStartTestMessage(TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName() + " started");
    }

    @AfterEach
    void printEndTestMessage(TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName() + " ended");
    }

    @Nested
    @DisplayName("Create user method - 'create'")
    class CreateUser {
        private User validUser;

        @BeforeEach
        void setUp() {
            validUser = new User("ValidName", "valid@email.com", 20);
        }

        @Test
        @DisplayName("Valid user creation test")
        void create_whenOk() {
            when(userDao.create(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            User user = userService.create(validUser);
            verify(userDao).create(same(validUser));

            assertEquals("ValidName", user.getName());
            assertEquals("valid@email.com", user.getEmail());
            assertEquals(20, user.getAge());
        }

        @Nested
        @DisplayName("Create User Validation Errors")
        class CreateUserValidationErrors {

            @ParameterizedTest
            @NullAndEmptySource
            @ValueSource(strings = {" ", "\t", "\n"})
            @DisplayName("Empty or blank name test")
            void create_whenNameIsEmptyOrBlank_validationError(String name) {
                validUser.setName(name);
                assertThrows(IllegalArgumentException.class, () -> userService.create(validUser), "Empty or blank name test failed");
                verifyNoInteractions(userDao);
            }

            @Test
            @DisplayName("Name too long test")
            void create_whenNameIsTooLong_validationError() {
                validUser.setName("A".repeat(256));
                assertThrows(IllegalArgumentException.class, () -> userService.create(validUser), "Name is too long test failed");
                verifyNoInteractions(userDao);
            }

            @ParameterizedTest
            @NullAndEmptySource
            @ValueSource(strings = {" ", "\t", "\n"})
            @DisplayName("Empty or blank email test")
            void create_whenEmailIsEmptyOrBlank_validationError(String email) {
                validUser.setEmail(email);
                assertThrows(IllegalArgumentException.class, () -> userService.create(validUser), "Empty or blank email test failed");
                verifyNoInteractions(userDao);
            }

            @Test
            @DisplayName("Email too long test")
            void create_whenEmailIsTooLong_validationError() {
                validUser.setName("A".repeat(240) + "@" + "b".repeat(12) + "." + "cc");
                assertThrows(IllegalArgumentException.class, () -> userService.create(validUser), "Email is too long test failed");
                verifyNoInteractions(userDao);
            }

            @ParameterizedTest
            @DisplayName("Wrong age test")
            @ValueSource(ints = {-1, 151})
            void create_whenAgeIsWrong_validationError(Integer age) {
                validUser.setAge(age);
                assertThrows(IllegalArgumentException.class, () -> userService.create(validUser), "Wrong age test failed");
                verifyNoInteractions(userDao);
            }
        }
    }

    @Nested
    @DisplayName("Find user method - 'findById'")
    class FindUser {
        private User validUser;

        @BeforeEach
        void setUp() {
            validUser = new User(1L, "ValidName", "valid@email.com", 20);
        }

        @Test
        @DisplayName("Valid user search test")
        void findById_whenOk() {
            when(userDao.findById(1L)).thenReturn(Optional.of(validUser));

            User user = userService.findById(1L).orElse(null);
            verify(userDao).findById(1L);

            assertNotEquals(null, user);
            assertEquals("ValidName", user.getName());
            assertEquals("valid@email.com", user.getEmail());
            assertEquals(20, user.getAge());
        }
    }

    @Nested
    @DisplayName("Find all users method - 'findAll'")
    class FindAllUsers {
        private List<User> validUsersList;

        @BeforeEach
        void setUp() {
            validUsersList = List.of(
                    new User(1L, "ValidName1", "valid1@email.com", 20),
                    new User(2L, "ValidName2", "valid2@email.com", 30)
            );
        }

        @Test
        @DisplayName("Valid user search test")
        void findById_whenOk() {
            when(userDao.findAll()).thenReturn(validUsersList);

            List<User> users = userService.findAll();
            verify(userDao).findAll();

            assertEquals(2, users.size());
        }
    }

    @Nested
    @DisplayName("Update user method - 'update'")
    class UpdateUser {
        private User existingUser;
        private User updatedUser;

        @BeforeEach
        void setUp() {
            existingUser = new User(1L, "ValidName", "valid@email.com", 20);
            updatedUser = new User("NewValidName", "newvalid@email.com", 22);
        }

        @Test
        @DisplayName("Valid user update test")
        void update_whenOk() {
            when(userDao.update(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            User result = userService.update(updatedUser);
            verify(userDao).update(updatedUser);

            assertEquals("NewValidName", result.getName());
            assertEquals("newvalid@email.com", result.getEmail());
            assertEquals(22, result.getAge());
        }

        @Nested
        @DisplayName("Update User Validation Errors")
        class UpdateUserValidationErrors {

            @ParameterizedTest
            @NullAndEmptySource
            @ValueSource(strings = {" ", "\t", "\n"})
            @DisplayName("Empty or blank name test")
            void update_whenNameIsEmptyOrBlank_validationError(String name) {
                existingUser.setName(name);
                assertThrows(IllegalArgumentException.class, () -> userService.update(existingUser), "Empty or blank name test failed");
                verifyNoInteractions(userDao);
            }

            @Test
            @DisplayName("Name too long test")
            void update_whenNameIsTooLong_validationError() {
                existingUser.setName("A".repeat(256));
                assertThrows(IllegalArgumentException.class, () -> userService.update(existingUser), "Name is too long test failed");
                verifyNoInteractions(userDao);
            }

            @ParameterizedTest
            @NullAndEmptySource
            @ValueSource(strings = {" ", "\t", "\n"})
            @DisplayName("Empty or blank email test")
            void update_whenEmailIsEmptyOrBlank_validationError(String email) {
                existingUser.setEmail(email);
                assertThrows(IllegalArgumentException.class, () -> userService.update(existingUser), "Empty or blank email test failed");
                verifyNoInteractions(userDao);
            }

            @Test
            @DisplayName("Email too long test")
            void update_whenEmailIsTooLong_validationError() {
                existingUser.setName("A".repeat(240) + "@" + "b".repeat(12) + "." + "cc");
                assertThrows(IllegalArgumentException.class, () -> userService.update(existingUser), "Email is too long test failed");
                verifyNoInteractions(userDao);
            }

            @ParameterizedTest
            @DisplayName("Wrong age test")
            @ValueSource(ints = {-1, 151})
            void update_whenAgeIsWrong_validationError(Integer age) {
                existingUser.setAge(age);
                assertThrows(IllegalArgumentException.class, () -> userService.update(existingUser), "Wrong age test failed");
                verifyNoInteractions(userDao);
            }
        }
    }

    @Nested
    @DisplayName("Delete user method - 'deleteById'")
    class DeleteUser {
        @Test
        @DisplayName("Valid user deletion test")
        void deleteById_whenOk() {
            when(userDao.deleteById(1L)).thenReturn(true);

            boolean foundUserToDelete = userService.deleteById(1L);
            verify(userDao).deleteById(1L);

            assertTrue(foundUserToDelete);
        }
    }
}
