package org.teamflow.screens;

import org.teamflow.ScreenManager;
import org.teamflow.controllers.ChatController;
import org.teamflow.enums.ChatroomLinkType;
import org.teamflow.enums.ScreenType;
import org.teamflow.abstracts.Screen;
import org.teamflow.models.Chatroom;
import org.teamflow.models.Epic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class EpicScreen extends Screen {

    public EpicScreen(ScreenManager screenManager) {
        super(screenManager);
    }

    @Override
    public void show() {
        AtomicBoolean running = new AtomicBoolean(true);
        List<MenuOption> menuList = new ArrayList<>();
        menuList.add(new MenuOption("Create new epic", this::createEpic));
        menuList.add(new MenuOption("View epic", this::listEpics));
        menuList.add(new MenuOption("Select epic", this::selectEpic));

        while (running.get()) {
            printBreadcrumb("Dashboard", "Project", "Epic");
            System.out.println();
            displayMenu(menuList, () -> {
                System.out.println("Returning to project screen...");
                running.set(false);
            });
        }
    }

    private void selectEpic() {
        List<Epic> epics = projectController.getEpics();
        System.out.println("Select a Epic:");
        for (int i = 0; i < epics.size(); i++) {
            System.out.println((i + 1) + ". " + epics.get(i).getTitle());
        }

        int roleIndex;
        try {
            roleIndex = Integer.parseInt(scanner.nextLine()) - 1;
            if (roleIndex < 0 || roleIndex >= epics.size()) {
                setAlertMessage("Invalid epic selection.");
                return;
            }
        } catch (NumberFormatException e) {
            setAlertMessage("Please enter a valid number.");
            return;
        }

        Epic selectedEpic = epics.get(roleIndex);
        projectController.setCurrentEpic(selectedEpic);

        if (projectController.getCurrentEpic() != null) {
            showEpicDetailsMenu();
        }
    }

    private void showEpicDetailsMenu() {
        printBreadcrumb("Dashboard", "Project", "Epic", projectController.getCurrentEpic().getTitle());

        boolean isScrumMaster = userController.isScrumMaster(projectController.getCurrentProjectId());
        List<MenuOption> menuList = new ArrayList<>();

        menuList.add(new MenuOption("View user stories", () -> screenManager.switchTo(ScreenType.USER_STORY)));
        menuList.add(new MenuOption("Edit epic", this::editEpic, isScrumMaster));
        menuList.add(new MenuOption("Delete epic", this::deleteEpic, isScrumMaster));
        menuList.add(new MenuOption("View linked chatrooms", this::listEpicChatrooms));
        menuList.add(new MenuOption("Create chatroom", this::createEpicChatroom));

        displayMenu(menuList, null);
    }

    private void createEpic() {
        String title;

        System.out.print("Enter name for your epic ");
        title = scanner.nextLine();

        projectController.createEpic(title);
    }

    private void listEpics() {
        ArrayList<String> epics = projectController.listEpics();
        for (String epic : epics) {
            System.out.println(epic);
        }
    }

    private void editEpic() {
        String title;
        System.out.print("Enter the new title for the epic you want to edit: ");
        title = scanner.nextLine().trim();

        if (title.isEmpty()) {
            System.out.println("Title cannot be empty. Please try again.");
            return;
        }

        System.out.println("Editing epic with new title: " + title);
        projectController.editEpic(title);
    }

    private void deleteEpic() {
        if (!userController.isScrumMaster(projectController.getCurrentProjectId())) {
            setAlertMessage("Only Scrum Masters can delete epics.");
            return;
        }
        Epic epic = projectController.getCurrentEpic();

        if (epic == null) {
            setAlertMessage("No epic selected.");
            return;
        }

        epic.delete();
    }

    private void listEpicChatrooms() {
        ChatController chatController = screenManager.getChatController();
        List<Chatroom> chatrooms = chatController.getChatroomsForEpic(projectController.getCurrentEpic().getId());

        System.out.println("Select a Chatroom:");

        for (int i = 0; i < chatrooms.size(); i++) {
            System.out.println((i + 1) + ". " + chatrooms.get(i).getName());
        }

        int roleIndex;
        try {
            roleIndex = Integer.parseInt(scanner.nextLine()) - 1;
            if (roleIndex < 0 || roleIndex >= chatrooms.size()) {
                setAlertMessage("Invalid chatroom selection.");
                return;
            }
        } catch (NumberFormatException e) {
            setAlertMessage("Please enter a valid number.");
            return;
        }

        Chatroom selectedChatroom = chatrooms.get(roleIndex);
        chatController.setCurrentChatroom(selectedChatroom);

        if (chatController.getCurrentChatroom() != null) {
            screenManager.switchTo(ScreenType.CHATROOM);
        }
    }

    private void createEpicChatroom() {
        ChatController chatController = screenManager.getChatController();
        System.out.println("What is the name of the Chatroom: ");
        String name = scanner.nextLine();

        Chatroom chatroom = new Chatroom(name);
        chatroom.setLinkType(ChatroomLinkType.EPIC);
        chatroom.setLinkedEntityId(projectController.getCurrentEpic().getId());
        chatController.createChatroom(chatroom);
    }
}
