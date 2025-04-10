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
        // [TODO] Query to get all chatrooms user has access to
        return new ArrayList<>();
    }

    public List<Message> getMessagesForChatroom(int chatroomId) {
        // [TODO] Get messages from DB
        return new ArrayList<>();
    }

    public void sendMessage(int chatroomId, int userId, String content) {
        // [TODO] Insert new message into DB
    }

    public Chatroom createChatroom(String name) {
        // [TODO] Insert new chatroom and return it
        return new Chatroom(0, name);
    }
}
