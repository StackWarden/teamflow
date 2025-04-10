package org.teamflow.controllers;

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
}
