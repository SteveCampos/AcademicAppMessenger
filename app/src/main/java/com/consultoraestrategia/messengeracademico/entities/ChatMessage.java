package com.consultoraestrategia.messengeracademico.entities;

import com.consultoraestrategia.messengeracademico.base.actionMode.Selectable;
import com.consultoraestrategia.messengeracademico.db.MessengerAcademicoDatabase;
import com.google.firebase.database.Exclude;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by @stevecampos on 9/03/2017.
 */

@Table(database = MessengerAcademicoDatabase.class)
@Parcel(analyze = {ChatMessage.class})
public class ChatMessage extends BaseModel implements Selectable {

    public static final int STATUS_WRITED = 2000;
    public static final int STATUS_SEND = 2002;
    public static final int STATUS_DELIVERED = 2004;
    public static final int STATUS_READED = 2006;

    public static final String TYPE_TEXT = "TEXT";
    public static final String TYPE_IMAGE = "IMAGE";
    public static final String TYPE_AUDIO = "AUDIO";//not supported
    public static final String TYPE_VIDEO = "VIDEO";//not supported
    public static final String TYPE_FILE = "FILE";//not supported
    public static final String TYPE_RUBRO = "RUBRO";
    public static final String TYPE_TEXT_OFFICIAL = "TEXT_OFFICIAL";


    @ForeignKey(stubbedRelationship = true)
    public Contact emisor;


    @ForeignKey(stubbedRelationship = true)
    public Contact receptor;

    @Column
    public String messageText;
    @Column
    public int messageStatus;
    @Column
    public String messageType;

    @Column
    public long timestamp;

    @PrimaryKey
    @Exclude
    public String keyMessage;

    @Column
    public String chatKey; //Foreign key xd

    @Column
    public String messageUri;

    @ForeignKey(stubbedRelationship = true)
    public MediaFile mediaFile;

    @Exclude
    @Column
    public int stateChatMessage;// VISIBLE, DELETED, ETC.

    @ForeignKey(stubbedRelationship = true)
    public OfficialMessage officialMessage;

    @Column
    public long writeTimestamp;
    @Column
    public long sendTimestamp;
    @Column
    public long deliverTimestamp;
    @Column
    public long readTimestamp;


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

    public ChatMessage(Contact emisor, Contact receptor, String messageText, int messageStatus, String messageType, long timestamp, String keyMessage, String chatKey, String messageUri, MediaFile mediaFile, int stateChatMessage, OfficialMessage officialMessage) {
        this.emisor = emisor;
        this.receptor = receptor;
        this.messageText = messageText;
        this.messageStatus = messageStatus;
        this.messageType = messageType;
        this.timestamp = timestamp;
        this.keyMessage = keyMessage;
        this.chatKey = chatKey;
        this.messageUri = messageUri;
        this.mediaFile = mediaFile;
        this.stateChatMessage = stateChatMessage;
        this.officialMessage = officialMessage;
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
        if (this.keyMessage.equals(message.getKeyMessage())) {
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

    public MediaFile getMediaFile() {
        return mediaFile;
    }

    public void setMediaFile(MediaFile mediaFile) {
        this.mediaFile = mediaFile;
    }

    public OfficialMessage getOfficialMessage() {
        return officialMessage;
    }

    public void setOfficialMessage(OfficialMessage officialMessage) {
        this.officialMessage = officialMessage;
    }

    public long getWriteTimestamp() {
        return writeTimestamp;
    }

    public void setWriteTimestamp(long writeTimestamp) {
        this.writeTimestamp = writeTimestamp;
    }

    public long getSendTimestamp() {
        return sendTimestamp;
    }

    public void setSendTimestamp(long sendTimestamp) {
        this.sendTimestamp = sendTimestamp;
    }

    public long getDeliverTimestamp() {
        return deliverTimestamp;
    }

    public void setDeliverTimestamp(long deliverTimestamp) {
        this.deliverTimestamp = deliverTimestamp;
    }

    public long getReadTimestamp() {
        return readTimestamp;
    }

    public void setReadTimestamp(long readTimestamp) {
        this.readTimestamp = readTimestamp;
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
        if (mediaFile != null) {
            result.put("mediaFile", mediaFile.toMap());
        }
        if (officialMessage != null) {
            result.put("officialMessage", officialMessage.toMap());
        }
        result.put("writeTimestamp", timestamp);
        result.put("sendTimestamp", timestamp);
        /*
        switch (messageStatus) {
            default:
                result.put("writeTimestamp", writeTimestamp);
                break;
            case STATUS_SEND:
                result.put("sendTimestamp", sendTimestamp);
                break;
            case STATUS_DELIVERED:
                result.put("deliverTimestamp", deliverTimestamp);
                break;
            case STATUS_READED:
                result.put("readTimestamp", readTimestamp);
                break;

        }*/
        return result;
    }

    @Override
    public String toString() {
        String strMF = null;
        if (mediaFile != null) {
            strMF = mediaFile.toString();
        }
        return "emisor: " + emisor.getPhoneNumber() + "\n" +
                "receptor: " + receptor.getPhoneNumber() + "\n" +
                "messageText: " + messageText + "\n" +
                "messageStatus: " + messageStatus + "\n" +
                "messageType: " + messageType + "\n" +
                "timestamp: " + timestamp + "\n" +
                "keyMessage: " + keyMessage + "\n" +
                "chatKey: " + chatKey + "\n" +
                "messageUri: " + messageUri + "\n" +
                "mediaFile: " + strMF;
    }

    public String getId() {
        return getKeyMessage();
    }

    private boolean selected;

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public String getKey() {
        return keyMessage;
    }
}
