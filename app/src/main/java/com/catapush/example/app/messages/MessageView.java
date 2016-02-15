package com.catapush.example.app.messages;

import com.catapush.library.messages.CatapushMessage;

import java.util.List;

public interface MessageView {

    void setMessages(List<CatapushMessage> messages);
}
