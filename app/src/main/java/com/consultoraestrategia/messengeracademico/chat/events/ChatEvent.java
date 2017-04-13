package com.consultoraestrategia.messengeracademico.chat.events;

import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Connection;
import com.consultoraestrategia.messengeracademico.entities.Contact;

import java.util.List;

/**
 * Created by Steve on 9/03/2017.
 */
public class ChatEvent {
    public static final int TYPE_CONTACT_EMISOR = 100;
    public static final int TYPE_CONTACT_RECEPTOR = 101;
    public static final int TYPE_MESSAGE = 10;
    public static final int TYPE_CONNECTION = 11;
    public static final int TYPE_ACTION = 12;
    public static final int TYPE_INCOMING_MESSAGE = 13;
    public static final int TYPE_MESSAGE_LIST = 14;

    private int type;
    private Contact contact;
    private Contact receptor;
    private ChatMessage message;
    private Connection connection;
    private String action;

    private List<ChatMessage> messages;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public ChatMessage getMessage() {
        return message;
    }

    public void setMessage(ChatMessage message) {
        this.message = message;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Contact getReceptor() {
        return receptor;
    }

    public void setReceptor(Contact receptor) {
        this.receptor = receptor;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }
}
