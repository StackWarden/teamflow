package org.teamflow;

import org.teamflow.controllers.UserController;

import java.util.Scanner;

public class Main {
    public static UserController userController = new UserController();

    public static void main(String[] args) {
        register();
    }

    public static int getLoginOrRegister() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to login or register an account?");
        System.out.println("1. Login");
        System.out.println("2. Sign up");

        int input = scanner.nextInt();

        return switch (input) {
            case 1, 2 -> {
                System.out.println("You chose: " + input);
                yield input;
            }
            default -> {
                getLoginOrRegister();
                yield input;
            }
        };
    }

    public static void register() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("What is your name?");
        String name = scanner.nextLine();

        try {
            userController.registerUser(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
