package com.example.letschat.model;

import com.google.firebase.Timestamp;

import java.util.List;

public class ChatRoomModel {
    String chatroomId;
    List<String> userIds;
    Timestamp lastMessagTimestamp;
    String lastMessageSendId;
    String lastMessage;


    public ChatRoomModel() {
    }

    public ChatRoomModel(String chatroomId, List<String> userIds, Timestamp lastMessagTimestamp, String lastMessageSendId) {
        this.chatroomId = chatroomId;
        this.userIds = userIds;
        this.lastMessagTimestamp = lastMessagTimestamp;
        this.lastMessageSendId = lastMessageSendId;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public Timestamp getLastMessagTimestamp() {
        return lastMessagTimestamp;
    }

    public void setLastMessagTimestamp(Timestamp lastMessagTimestamp) {
        this.lastMessagTimestamp = lastMessagTimestamp;
    }

    public String getLastMessageSendId() {
        return lastMessageSendId;
    }

    public void setLastMessageSendId(String lastMessageSendId) {
        this.lastMessageSendId = lastMessageSendId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
