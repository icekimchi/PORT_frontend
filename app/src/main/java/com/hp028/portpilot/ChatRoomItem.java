package com.hp028.portpilot;

public class ChatRoomItem {
    private String title;
    private String description;

    public ChatRoomItem(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}