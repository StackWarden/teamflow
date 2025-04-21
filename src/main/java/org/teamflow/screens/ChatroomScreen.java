package org.teamflow.screens;

import org.teamflow.ScreenManager;
import org.teamflow.controllers.ChatController;
import org.teamflow.controllers.UserController;
import org.teamflow.enums.ChatroomLinkType;
import org.teamflow.abstracts.Screen;
import org.teamflow.models.Chatroom;
import org.teamflow.models.Message;

import java.util.*;

import static org.teamflow.ScreenManager.clearScreen;

public class ChatroomScreen extends Screen {

    public ChatroomScreen(ScreenManager screenManager) {
        super(screenManager);
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
            printBreadcrumb("Dashboard", "Project", "Chatroom");

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
        clearScreen();

        Map<ChatroomLinkType, List<Chatroom>> grouped = groupChatroomsByType(chatrooms);
        List<Chatroom> flattenedList = displayGroupedChatrooms(grouped);

        Chatroom selected = selectChatroomFromList(flattenedList);
        if (selected != null) {
            chatController.setCurrentChatroom(selected);
            openChatroom(selected);
        }
    }

    private Map<ChatroomLinkType, List<Chatroom>> groupChatroomsByType(List<Chatroom> chatrooms) {
        Map<ChatroomLinkType, List<Chatroom>> grouped = new LinkedHashMap<>();

        for (ChatroomLinkType type : ChatroomLinkType.values()) {
            grouped.put(type, new ArrayList<>());
        }

        for (Chatroom chatroom : chatrooms) {
            ChatroomLinkType type = chatroom.getLinkType();
            grouped.getOrDefault(type, grouped.get(ChatroomLinkType.NONE)).add(chatroom);
        }

        return grouped;
    }


    private List<Chatroom> displayGroupedChatrooms(Map<ChatroomLinkType, List<Chatroom>> grouped) {
        List<Chatroom> all = new ArrayList<>();
        int index = 1;

        for (Map.Entry<ChatroomLinkType, List<Chatroom>> entry : grouped.entrySet()) {
            ChatroomLinkType type = entry.getKey();
            List<Chatroom> chatrooms = entry.getValue();

            if (chatrooms.isEmpty()) {
                printEmptyMessage(type);
                System.out.println();
                continue;
            }

            System.out.println("────────────────────────────────────────────");
            System.out.println(getSectionTitle(type));
            System.out.println("────────────────────────────────────────────");

            for (Chatroom chatroom : chatrooms) {
                System.out.printf("%2d. %s%n", index, chatroom.getName());
                all.add(chatroom);
                index++;
            }

            System.out.println();
        }

        return all;
    }


    private Chatroom selectChatroomFromList(List<Chatroom> chatrooms) {
        System.out.print("Select chatroom: ");
        try {
            int index = Integer.parseInt(scanner.nextLine()) - 1;
            if (index >= 0 && index < chatrooms.size()) {
                return chatrooms.get(index);
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a number.");
        }
        return null;
    }

    private String getSectionTitle(ChatroomLinkType type) {
        return switch (type) {
            case EPIC -> "Epic Chatrooms:";
            case STORY -> "User Story Chatrooms:";
            case TASK -> "Task Chatrooms:";
            case SPRINT -> "Sprint Chatrooms:";
            case NONE -> "Global Chatrooms:";
        };
    }

    private void printEmptyMessage(ChatroomLinkType type) {
        switch (type) {
            case EPIC -> System.out.println("No Epic-related chatrooms found.");
            case STORY -> System.out.println("No User Story-related chatrooms found.");
            case TASK -> System.out.println("No Task-related chatrooms found.");
            case SPRINT -> System.out.println("No Sprint-related chatrooms found.");
            case NONE -> System.out.println("No global chatrooms found.");
        }
    }


    private void createChatroom() {
        System.out.print("Enter chatroom name: ");
        String name = scanner.nextLine();
        Chatroom created = chatController.createChatroom(new Chatroom(name));
        chatController.setCurrentChatroom(created);
        openChatroom(created);
    }

    private void openChatroom(Chatroom chatroom) {
        boolean chatting = true;

        while (chatting) {
            renderChatroom(chatroom);

            System.out.println();
            System.out.println("Type your message (or 0 to leave, \\\\ to refresh):");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "0" -> {
                    chatting = false;
                    chatController.setCurrentChatroom(null);
                }
                case "\\\\" -> {
                    // manual refresh
                }
                default -> {
                    if (!input.isEmpty()) {
                        chatController.sendMessage(userController.getUserId(), input);
                    }
                }
            }
        }
    }

    private void renderChatroom(Chatroom chatroom) {
        clearScreen();
        printBreadcrumb("Project", "Chatroom", chatroom.getName());
        System.out.println();
        List<Message> messages = chatController.getMessagesForChatroom(chatroom.getId());
        for (Message msg : messages) {
            String username = msg.getUserTitle() != null ? msg.getUserTitle() : "Deleted User";
            System.out.println("[" + msg.getTimestamp() + "] " + username + ": " + msg.getContent());
        }
    }
}
