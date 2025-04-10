package org.teamflow.screens;

import org.teamflow.ScreenManager;
import org.teamflow.controllers.ChatController;
import org.teamflow.controllers.UserController;
import org.teamflow.interfaces.Screen;
import org.teamflow.models.Chatroom;
import org.teamflow.models.Message;

import java.util.List;
import java.util.Scanner;

public class ChatroomScreen implements Screen {

    private final Scanner scanner;
    private final ChatController chatController;
    private final UserController userController;
    private final ScreenManager screenManager;

    public ChatroomScreen(Scanner scanner, ChatController chatController, UserController userController, ScreenManager screenManager) {
        this.scanner = scanner;
        this.chatController = chatController;
        this.userController = userController;
        this.screenManager = screenManager;
    }

    @Override
    public void show() {
        Chatroom current = chatController.getCurrentChatroom();

        if (current != null) {
            openChatroom(current);
        } else {
            showChatroomOverview();
        }
    }

    private void showChatroomOverview() {
        boolean running = true;

        while (running) {
            System.out.println("\n===== Chatrooms =====");
            System.out.println("1. View accessible chatrooms");
            System.out.println("2. Create new chatroom");
            System.out.println("0. Back");

            String input = scanner.nextLine();

            switch (input) {
                case "1" -> listAndOpenChatrooms();
                case "2" -> createChatroom();
                case "0" -> running = false;
                default -> System.out.println("Invalid input.");
            }
        }
    }

    private void listAndOpenChatrooms() {
        List<Chatroom> chatrooms = chatController.getAllAccessibleChatrooms(userController.getUserId());

        if (chatrooms.isEmpty()) {
            System.out.println("You are not part of any chatrooms.");
            return;
        }

        for (int i = 0; i < chatrooms.size(); i++) {
            System.out.println((i + 1) + ". " + chatrooms.get(i).getName());
        }

        System.out.print("Select chatroom: ");
        try {
            int index = Integer.parseInt(scanner.nextLine()) - 1;
            if (index >= 0 && index < chatrooms.size()) {
                Chatroom selected = chatrooms.get(index);
                chatController.setCurrentChatroom(selected);
                openChatroom(selected);
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a number.");
        }
    }

    private void createChatroom() {
        System.out.print("Enter chatroom name: ");
        String name = scanner.nextLine();
        Chatroom created = chatController.createChatroom(name);
        chatController.setCurrentChatroom(created);
        openChatroom(created);
    }

    private void openChatroom(Chatroom chatroom) {
        boolean chatting = true;

        while (chatting) {
            System.out.println("\n===== Chatroom: " + chatroom.getName() + " =====");

            List<Message> messages = chatController.getMessagesForChatroom(chatroom.getId());
            for (Message message : messages) {
                System.out.println("[" + message.getTimestamp() + "] " + message.getUserId() + ": " + message.getContent());
            }

            System.out.println("Type your message (or 0 to leave):");
            String input = scanner.nextLine();

            if ("0".equals(input)) {
                chatting = false;
                chatController.setCurrentChatroom(null);
            } else {
                chatController.sendMessage(chatroom.getId(), userController.getUserId(), input);
            }
        }
    }
}
