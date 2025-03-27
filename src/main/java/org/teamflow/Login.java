package org.teamflow;

// imports
import java.util.Scanner;

public class Login {
    public void LoginOrRegister() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to login or register an account?");
        System.out.println("1. Login");
        System.out.println("2. Sign up");

        int input = scanner.nextInt();

        switch(input) {
            case 1:
                System.out.println("going to login");
                break;
            case 2:
                System.out.println("going to register");
                break;
            default:
                LoginOrRegister();
        }

        scanner.close();
    }
}
