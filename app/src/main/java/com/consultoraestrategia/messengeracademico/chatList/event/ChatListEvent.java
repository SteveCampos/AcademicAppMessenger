package com.consultoraestrategia.messengeracademico.chatList.event;

import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.Contact;

import java.util.List;

/**
 * Created by jairc on 24/03/2017.
 */

public class ChatListEvent {

    public static final int TYPE_CHATLIST = 100;
    public static final int TYPE_CONTACT = 101;
    public static final int TYPE_PHONENUMBER = 102;
    public static final int TYPE_CHAT = 103;

    private int type;
    private String phoneNumber;
    private Contact contact;
    private List<Chat> chats;
    private Chat chat;

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

    public List<Chat> getChats() {
        return chats;
    }

    public void setChats(List<Chat> chats) {
        this.chats = chats;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }
}
