package com.consultoraestrategia.messengeracademico.entities;

import com.consultoraestrategia.messengeracademico.db.MessengerAcademicoDatabase;
import com.google.firebase.database.Exclude;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Steve on 9/03/2017.
 */

@Table(database = MessengerAcademicoDatabase.class)
public class ChatMessage extends BaseModel {

    public static final int STATUS_WRITED = 1000;
    public static final int STATUS_SEND = 1001;
    public static final int STATUS_DELIVERED = 1002;
    public static final int STATUS_READED = 1003;

    public static final String TYPE_TEXT = "TEXT";
    public static final String TYPE_IMAGE = "IMAGE";
    public static final String TYPE_AUDIO = "AUDIO";
    public static final String TYPE_VIDEO = "VIDEO";
    public static final String TYPE_FILE = "FILE";
    //MORE TYPES HERE...

    //Firebase fields
    /*
    @Column
    private String emisorId;

    @Column
    private String receptorId;*/

    @ForeignKey(stubbedRelationship = true, saveForeignKeyModel = true)
    private Contact emisor;

    @ForeignKey(stubbedRelationship = true, saveForeignKeyModel = true)
    private Contact receptor;

    @Column
    private String messageText;
    @Column
    private int messageStatus;
    @Column
    private String messageType;

    @Column
    private long timestamp;
    @Column
    private String messageUri;


    @PrimaryKey
    @Exclude
    private String keyMessage;

    @Column
    private String chatKey; //Foreign key xd

    @Column
    private int stateChatMessage;// VISIBLE, DELETED, ETC.

    /*@ForeignKey(stubbedRelationship = true, saveForeignKeyModel = true)
    private Chat chat; // long chatId*/

    public ChatMessage() {
    }


    public ChatMessage(Contact emisor, Contact receptor, String messageText, int messageStatus, String messageType, long timestamp, String messageUri, String keyMessage, String chatKey, int stateChatMessage) {
        this.emisor = emisor;
        this.receptor = receptor;
        this.messageText = messageText;
        this.messageStatus = messageStatus;
        this.messageType = messageType;
        this.timestamp = timestamp;
        this.messageUri = messageUri;
        this.keyMessage = keyMessage;
        this.chatKey = chatKey;
        this.stateChatMessage = stateChatMessage;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public int getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(int messageStatus) {
        this.messageStatus = messageStatus;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessageUri() {
        return messageUri;
    }

    public void setMessageUri(String messageUri) {
        this.messageUri = messageUri;
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        ChatMessage message = (ChatMessage) obj;
        Long t1 = this.timestamp;
        Long t2 = message.getTimestamp();
        if (this.messageText.equals(message.getMessageText()) && t1.equals(t2)) {
            equals = true;
        }
        return equals;
    }


    public int getStateChatMessage() {
        return stateChatMessage;
    }

    public void setStateChatMessage(int stateChatMessage) {
        this.stateChatMessage = stateChatMessage;
    }

    public String getKeyMessage() {
        return keyMessage;
    }

    public void setKeyMessage(String keyMessage) {
        this.keyMessage = keyMessage;
    }

    public Contact getReceptor() {
        return receptor;
    }

    public void setReceptor(Contact receptor) {
        this.receptor = receptor;
    }

    public Contact getEmisor() {
        return emisor;
    }

    public void setEmisor(Contact emisor) {
        this.emisor = emisor;
    }

    public String getChatKey() {
        return chatKey;
    }

    public void setChatKey(String chatKey) {
        this.chatKey = chatKey;
    }


    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("emisor", emisor.toMap());
        result.put("receptor", receptor.toMap());
        result.put("messageText", messageText);
        result.put("messageStatus", messageStatus);
        result.put("messageType", messageType);
        result.put("timestamp", timestamp);
        result.put("messageUri", messageUri);
        result.put("keyMessage", keyMessage);
        result.put("chatKey", chatKey);
        return result;
    }
}
