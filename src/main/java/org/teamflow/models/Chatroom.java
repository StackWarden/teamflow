package org.teamflow.models;

public class Chatroom {
    private int id;
    private String name;

    public Chatroom() {}

    public Chatroom(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Chatroom(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Chatroom{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
