package com.consultoraestrategia.messengeracademico.entities;


import android.util.Log;

import com.consultoraestrategia.messengeracademico.db.MessengerAcademicoDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

/**
 * Created by Steve on 14/03/2017.
 */
@Table(database = MessengerAcademicoDatabase.class)
public class Chat extends BaseModel {

    public static final int TYPE_ONE_TO_ONE = 1000;

    public static final int STATE_ACTIVE = 100;
    public static final int STATE_DELETED = 101;
    private static final String TAG = Chat.class.getSimpleName();

    private long id;

    @Column
    @PrimaryKey
    private String chatKey;

    @Column
    private String idEmisor;

    @Column
    private String idReceptor;

    @ForeignKey(stubbedRelationship = true, saveForeignKeyModel = true)
    private Contact emisor;

    @ForeignKey(stubbedRelationship = true, saveForeignKeyModel = true)
    private Contact receptor;

    @Column
    private int state; //STATE_ACTIVE/ STATE_DELETED

    @Column
    private long stateTimestamp;//CHANGE TO -> lastMessageTimestamp

    @Column
    private long timestamp;

    List<ChatMessage> messageList;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    /*@OneToMany(methods = {OneToMany.Method.LOAD}, variableName = "chatReceptor")
    public Contact getChatReceptor() {
        return SQLite.select()
                .from(Contact.class)
                .where(Contact_Table.phoneNumber.eq(receptor.getPhoneNumber()))
                .querySingle();
    }*/

    public void setEmisor(Contact emisor) {
        this.emisor = emisor;
    }

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "messageList")
    public List<ChatMessage> getMessageList() {

        if (messageList == null || messageList.isEmpty()) {
            messageList = SQLite.select()
                    .from(ChatMessage.class)
                    .where(ChatMessage_Table.chatKey.eq(chatKey))
                    .and(ChatMessage_Table.timestamp.greaterThanOrEq(timestamp))
                    .queryList();
        }
        return messageList;
    }


    @Override
    public boolean equals(Object obj) {
        boolean equal = false;
        if (obj instanceof Chat) {
            Chat objChat = (Chat) obj;
            Log.d(TAG, "id: " + getId());
            Log.d(TAG, "objChat id: " + objChat.getId());
            equal = this.getId() == objChat.getId();
        }
        return equal;
    }

    public ChatMessage getLastMessage() {
        return SQLite.select()
                .from(ChatMessage.class)
                .where(ChatMessage_Table.chatKey.eq(chatKey))
                .and(ChatMessage_Table.timestamp.greaterThanOrEq(stateTimestamp))
                .querySingle();
    }

    public long countMessagesNoReaded() {
        return SQLite.select()
                .from(ChatMessage.class)
                .where(ChatMessage_Table.chatKey.eq(chatKey))
                .and(ChatMessage_Table.timestamp.greaterThanOrEq(state))
                .and(ChatMessage_Table.receptor_userKey.eq(receptor.getUserKey()))
                .and(ChatMessage_Table.messageStatus.notEq(ChatMessage.STATUS_READED))
                .count();
    }
}
