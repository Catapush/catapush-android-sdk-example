package com.catapush.demo.catapush36integrationtest.app.messages;

import com.catapush.library.storage.models.IPMessage;

import java.util.List;

public interface MessageView {

    void addMessage(IPMessage message);

    void setMessages(List<IPMessage> messages);
}
