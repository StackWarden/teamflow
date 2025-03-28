package org.teamflow;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        LoginOrRegister loginOrRegister = new LoginOrRegister();
        System.out.println(loginOrRegister.getLoginOrRegister());
    }
}

class LoginOrRegister {
    public int getLoginOrRegister() {
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
}
