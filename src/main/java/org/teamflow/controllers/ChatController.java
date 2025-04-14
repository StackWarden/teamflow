package org.teamflow.controllers;

import org.teamflow.enums.ChatroomLinkType;
import org.teamflow.models.Chatroom;
import org.teamflow.models.Message;

import java.util.ArrayList;
import java.util.List;

public class ChatController {

    private Chatroom currentChatroom;

    public void setCurrentChatroom(Chatroom chatroom) {
        this.currentChatroom = chatroom;
    }

    public Chatroom getCurrentChatroom() {
        return currentChatroom;
    }

    public List<Chatroom> getAllAccessibleChatrooms(int userId) {
        return Chatroom.getAccessibleChatroomsForUser(userId);
    }

    public List<Message> getMessagesForChatroom(int chatroomId) {
        return currentChatroom.getMessagesForChatroom();
    }

    public void sendMessage(int userId, String content) {
        Message message = new Message(currentChatroom.getId(), userId, content);
        Message.insertMessage(message);
    }

    public Chatroom createChatroom(Chatroom chatroom) {
        chatroom.createChatroom(chatroom);
        return chatroom;
    }

    public List<Chatroom> getChatroomsForEpic(int epicId) {
        return Chatroom.getLinkedChatrooms(ChatroomLinkType.EPIC, epicId);
    }
    public List<Chatroom> getChatroomsForUserStory(int storyId) {
        return Chatroom.getLinkedChatrooms(ChatroomLinkType.STORY, storyId);
    }

    public List<Chatroom> getChatroomsForTask(int taskId) {
        return Chatroom.getLinkedChatrooms(ChatroomLinkType.TASK, taskId);
    }
}
