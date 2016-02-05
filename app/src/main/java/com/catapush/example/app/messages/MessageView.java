package com.catapush.example.app.messages;

import com.catapush.library.messages.IPMessage;

import java.util.List;

public interface MessageView {

    void setMessages(List<IPMessage> messages);
}
