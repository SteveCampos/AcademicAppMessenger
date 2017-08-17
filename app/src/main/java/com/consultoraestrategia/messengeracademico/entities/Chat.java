package com.consultoraestrategia.messengeracademico.entities;


import android.util.Log;

import com.consultoraestrategia.messengeracademico.db.MessengerAcademicoDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

/**
 * Created by @stevecampos on 14/03/2017.
 */
@Table(database = MessengerAcademicoDatabase.class)
public class Chat extends BaseModel {

    public static final int TYPE_ONE_TO_ONE = 1000;

    public static final int STATE_ACTIVE = 100;
    public static final int STATE_DELETED = 101;
    private static final String TAG = "ChatEntity";

    @Column
    @PrimaryKey
    private String chatKey;

    @Column
    private String idEmisor;

    @Column
    private String idReceptor;

    @ForeignKey(stubbedRelationship = true)
    private Contact emisor;

    @ForeignKey(stubbedRelationship = true)
    private Contact receptor;

    @Column
    private int state; //STATE_ACTIVE/ STATE_DELETED

    @Column
    private long stateTimestamp;//CHANGE TO -> lastMessageTimestamp

    @Column
    private long timestamp;

    public String getIdEmisor() {
        return idEmisor;
    }

    public void setIdEmisor(String idEmisor) {
        this.idEmisor = idEmisor;
    }

    public String getIdReceptor() {
        return idReceptor;
    }

    public void setIdReceptor(String idReceptor) {
        this.idReceptor = idReceptor;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getStateTimestamp() {
        return stateTimestamp;
    }

    public void setStateTimestamp(long stateTimestamp) {
        this.stateTimestamp = stateTimestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getChatKey() {
        return chatKey;
    }

    public void setChatKey(String chatKey) {
        this.chatKey = chatKey;
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

    //@OneToMany(methods = {OneToMany.Method.ALL}, variableName = "messageList")
    public List<ChatMessage> getMessageList() {
        return SQLite.select()
                .from(ChatMessage.class)
                .where(ChatMessage_Table.chatKey.eq(chatKey))
                .and(ChatMessage_Table.timestamp.greaterThanOrEq(timestamp))
                .orderBy(ChatMessage_Table.timestamp, false)
                .limit(100)
                .queryList();
    }

    public List<ChatMessage> getMessageNoReadedList(String mainUserKey) {
        Log.d(TAG, "getEmisor key: " + getEmisor().getUid());
        Log.d(TAG, "getReceptor key: " + getReceptor().getUid());

        List<ChatMessage> messages = SQLite.select()
                .from(ChatMessage.class)
                .where(ChatMessage_Table.chatKey.eq(chatKey))
                .and(ChatMessage_Table.timestamp.greaterThanOrEq(timestamp))
                .and(ChatMessage_Table.messageStatus.notEq(ChatMessage.STATUS_READED))
                .and(ChatMessage_Table.emisor_uid.notEq(mainUserKey))
                .orderBy(ChatMessage_Table.timestamp, false)
                .limit(100)
                .queryList();
        Log.d(TAG, "getMessageNoReadedList size: " + messages.size());
        return messages;
    }


    @Override
    public boolean equals(Object obj) {
        boolean equal = false;
        if (obj instanceof Chat) {
            Chat objChat = (Chat) obj;
            Log.d(TAG, "chatKey: " + getChatKey());
            Log.d(TAG, "objChat id: " + objChat.getChatKey());
            equal = this.getChatKey().equals(objChat.getChatKey());
        }
        return equal;
    }

    public ChatMessage getLastMessage() {
        return SQLite.select()
                .from(ChatMessage.class)
                .where(ChatMessage_Table.chatKey.eq(chatKey))
                //.and(ChatMessage_Table.timestamp.greaterThanOrEq(stateTimestamp))
                .orderBy(OrderBy.fromProperty(ChatMessage_Table.timestamp).descending())
                .querySingle();
    }

    public long countMessagesNoReaded(String mainUserKey) {
        return SQLite.selectCountOf()
                .from(ChatMessage.class)
                .where(ChatMessage_Table.chatKey.eq(chatKey))
                //.and(ChatMessage_Table.timestamp.greaterThanOrEq(state))
                .and(ChatMessage_Table.messageStatus.eq(ChatMessage.STATUS_DELIVERED))
                .and(ChatMessage_Table.emisor_uid.notEq(mainUserKey))
                .count();
    }

    public long getLastTimeStamp() {
        ChatMessage message = SQLite.select(Method.max(ChatMessage_Table.timestamp).as("timestamp"))
                .from(ChatMessage.class)
                .where(ChatMessage_Table.chatKey.eq(chatKey))
                .querySingle();
        long timestamp = 0;
        if (message != null) {
            timestamp = message.getTimestamp();
        }
        Log.d(TAG, "getLastTimeStamp timestamp: " + timestamp);
        return timestamp;
    }
}
