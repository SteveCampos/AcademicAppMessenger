package com.consultoraestrategia.messengeracademico.entities;

import com.consultoraestrategia.messengeracademico.db.MessengerAcademicoDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by @stevecampos on 16/08/2017.
 */
@Table(database = MessengerAcademicoDatabase.class)
@Parcel(analyze = {OfficialMessage.class})
public class OfficialMessage extends BaseModel {
    public static final String TAG = OfficialMessage.class.getSimpleName();

    @PrimaryKey
    public String id;
    @Column
    public String subject;
    @Column
    public String title;
    @Column
    public String reference1;
    @Column
    public String reference2;
    @Column
    public String reference3;
    @Column
    public String reference4;
    @Column
    public String importantReference;
    @Column
    public String officialEmisorName;
    @Column
    public String actionType;
    @Column
    public int state;

    public static final String ACTION_TYPE_CONFIRM = "ACTION_CONFIRM";
    public static final int STATE_WAITING = 100;
    public static final int STATE_CONFIRM = 101;
    public static final int STATE_DENY = 102;

    public OfficialMessage() {
    }

    public OfficialMessage(String id, String subject, String title, String reference1, String reference2, String reference3, String reference4, String importantReference, String officialEmisorName, String actionType, int state) {
        this.id = id;
        this.subject = subject;
        this.title = title;
        this.reference1 = reference1;
        this.reference2 = reference2;
        this.reference3 = reference3;
        this.reference4 = reference4;
        this.importantReference = importantReference;
        this.officialEmisorName = officialEmisorName;
        this.actionType = actionType;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReference1() {
        return reference1;
    }

    public void setReference1(String reference1) {
        this.reference1 = reference1;
    }

    public String getReference2() {
        return reference2;
    }

    public void setReference2(String reference2) {
        this.reference2 = reference2;
    }

    public String getReference3() {
        return reference3;
    }

    public void setReference3(String reference3) {
        this.reference3 = reference3;
    }

    public String getReference4() {
        return reference4;
    }

    public void setReference4(String reference4) {
        this.reference4 = reference4;
    }

    public String getImportantReference() {
        return importantReference;
    }

    public void setImportantReference(String importantReference) {
        this.importantReference = importantReference;
    }

    public String getOfficialEmisorName() {
        return officialEmisorName;
    }

    public void setOfficialEmisorName(String officialEmisorName) {
        this.officialEmisorName = officialEmisorName;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("subject", subject);
        result.put("title", title);
        result.put("reference1", reference1);
        result.put("reference2", reference2);
        result.put("reference3", reference3);
        result.put("reference4", reference4);
        result.put("importantReference", importantReference);
        result.put("officialEmisorName", officialEmisorName);
        result.put("actionType", actionType);
        result.put("state", state);
        return result;
    }
}
