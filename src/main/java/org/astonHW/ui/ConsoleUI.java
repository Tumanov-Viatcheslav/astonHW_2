package org.astonHW.ui;

import org.astonHW.entities.User;
import org.astonHW.exceptions.DaoException;
import org.astonHW.exceptions.UserNotFoundException;
import org.astonHW.services.UserService;

import java.util.Scanner;
import java.util.stream.Collectors;

public class ConsoleUI {

    private final UserService userService;
    private final Scanner scanner;

    public ConsoleUI(UserService userService, Scanner scanner) {
        this.userService = userService;
        this.scanner = scanner;
    }

    public void run() {
        System.out.println("==Application AstonHW2 running==");
        while (true) {
            printMenu();
            try {
                switch (scanner.nextLine()) {
                    case "c" -> createUser();
                    case "r" -> findUserById();
                    case "u" -> updateUser();
                    case "d" -> deleteUserById();
                    case "s" -> findAllUsers();
                    case "e" -> {
                        System.out.println("==Closing app==");
                        return;
                    }
                    default -> System.out.println("Wrong command");
                }
            } catch (UserNotFoundException e) {
                System.out.println(e.getMessage());
            } catch (DaoException e) {
                System.out.println("DB Error: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Incorrect input: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println("Print: \nc - to create user\nr - to find user by id\nu - to update user\nd - to delete user by id\ns - to show all users\ne - to close app");
    }

    private void createUser() {
        User user = new User();
        System.out.print("Enter name: ");
        user.setName(scanner.nextLine());
        System.out.print("Enter email: ");
        user.setEmail(scanner.nextLine());
        System.out.print("Enter age: ");
        user.setAge(Integer.parseInt(scanner.nextLine()));
        System.out.println("User created: " + userService.create(user));
    }

    private void findUserById() {
        System.out.println("Enter ID: ");
        Long id = Long.parseLong(scanner.nextLine());
        userService.findById(id)
                .ifPresentOrElse(
                        user -> System.out.println(user),
                        () -> { throw new UserNotFoundException(id); }
                );
    }

    private void findAllUsers() {
        System.out.println(userService.findAll()
                .stream()
                .map(User::toString)
                .collect(Collectors.joining("\n"))
        );
    }

    private void updateUser() {
        System.out.println("Enter ID: ");
        Long id = Long.parseLong(scanner.nextLine());
        User user = userService.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        System.out.print("Enter name: ");
        user.setName(scanner.nextLine());
        System.out.print("Enter email: ");
        user.setEmail(scanner.nextLine());
        System.out.print("Enter age: ");
        user.setAge(Integer.parseInt(scanner.nextLine()));
        System.out.println(userService.update(user));
    }

    private void deleteUserById() {
        System.out.println("Enter ID: ");
        Long id = Long.parseLong(scanner.nextLine());
        boolean foundUserToDelete = userService.deleteById(id);
        if (!foundUserToDelete) throw new UserNotFoundException(id);
    }
}
