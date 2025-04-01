package org.teamflow;

import org.teamflow.controllers.UserController;
import java.util.Scanner;

public class Main {
    public static UserController userController = new UserController();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (!userController.isLoggedIn()) {
            int choice = getLoginOrRegister();
            if (choice == 1) {
                login();
            } else {
                register();
            }
        }
        System.out.println("Welcome! You are now logged in.");
    }

    public static int getLoginOrRegister() {
        while (true) {
            System.out.println("Do you want to login or sign up?");
            System.out.println("1. Login");
            System.out.println("2. Sign up");

            try {
                int input = Integer.parseInt(scanner.nextLine());

                if (input == 1 || input == 2) {
                    System.out.println("You chose: " + input);
                    return input;
                } else {
                    System.out.println("Input 1 or 2");
                }
            } catch (NumberFormatException e) {
                System.out.println("Input 1 or 2");
            }
        }
    }

    public static void login() {
        System.out.println("What is your name?");
        String name = scanner.nextLine();

        int status = userController.loginUser(name);
        switch (status) {
            case 1 -> System.out.println("You are logged in.");
            case 2 -> System.out.println("You are already logged in.");
            case 0 -> System.out.println("Login failed. Try again.");
        }
    }

    public static void register() {
        System.out.println("What is your name?");
        String name = scanner.nextLine();

        int registerStatus = userController.registerUser(name);

        switch (registerStatus) {
            case 1 -> System.out.println("You have successfully registered!");
            case 2 -> {
                System.out.println("This user already exists, try another one!");
                register();
            }
        }
    }
}
