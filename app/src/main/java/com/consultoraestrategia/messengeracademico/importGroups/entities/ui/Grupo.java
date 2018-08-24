package com.consultoraestrategia.messengeracademico.importGroups.entities.ui;

import android.text.TextUtils;
import android.util.ArrayMap;

import com.consultoraestrategia.messengeracademico.db.MessengerAcademicoDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Table(database = MessengerAcademicoDatabase.class)
public class Grupo {
    @PrimaryKey
    private String uid;
    @Column
    private String name;
    private List<CrmeUser> integrantes = new ArrayList<>();

    public Grupo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CrmeUser> getIntegrantes() {
        return integrantes;
    }

    public void setIntegrantes(List<CrmeUser> integrantes) {
        this.integrantes = integrantes;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public static Map<String, Object> toMap(List<Grupo> grupos) {
        Map<String, Object> map = new ArrayMap<>();
        for (Grupo grupo :
                grupos) {
            for (CrmeUser user :
                    grupo.getIntegrantes()) {

                String groupUid = grupo.getUid();

                map.put("/group/" + groupUid + "/name", grupo.getName());

                String userId = user.getId();
                String phonenumber = user.getPhoneNumber();
                String displayName = user.getDisplayName();
                String name = user.getName();
                boolean isTutor = user.isTutor();
                boolean isAdmin = user.isAdmin();

                map.put("/group-crme/" + groupUid + "/crme-member/" + userId + "/phoneNumber", phonenumber);
                map.put("/group-crme/" + groupUid + "/crme-member/" + userId + "/displayName", displayName);
                map.put("/group-crme/" + groupUid + "/crme-member/" + userId + "/admin", isAdmin);
                map.put("/group-crme/" + groupUid + "/crme-member/" + userId + "/uid", userId);
                map.put("/group-crme/" + groupUid + "/crme-member/" + userId + "/tutor", isTutor);
                map.put("/group-crme/" + groupUid + "/crme-member/" + userId + "/name", name);


                //Sólo para relacionar a los usuarios del crme al messenger académico. Se escuchará ese nodo, y luego se asociará.

                map.put("/group-crme-to-mssg/" + groupUid + "/crme-member/" + userId + "/phoneNumber", phonenumber);
                map.put("/group-crme-to-mssg/" + groupUid + "/crme-member/" + userId + "/displayName", displayName);
                map.put("/group-crme-to-mssg/" + groupUid + "/crme-member/" + userId + "/admin", isAdmin);
                map.put("/group-crme-to-mssg/" + groupUid + "/crme-member/" + userId + "/uid", userId);
                map.put("/group-crme-to-mssg/" + groupUid + "/crme-member/" + userId + "/tutor", isTutor);
                map.put("/group-crme-to-mssg/" + groupUid + "/crme-member/" + userId + "/name", name);

                if (!TextUtils.isEmpty(phonenumber)) {
                    map.put("/phonenumber-groups/" + phonenumber + "/groups/" + groupUid, true);
                }
            }
        }
        return map;
    }


    @Override
    public String toString() {
        return "Grupo{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", integrantes=" + integrantes +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        Grupo grupo = (Grupo) obj;
        if (this.uid.equals(grupo.getUid())) {
            equals = true;
        }
        return equals;
    }

    @Override
    public int hashCode() {
        return uid.hashCode();
    }
}
