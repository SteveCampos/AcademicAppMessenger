package com.consultoraestrategia.messengeracademico.group;

import com.consultoraestrategia.messengeracademico.base.BasePresenter;
import com.consultoraestrategia.messengeracademico.importGroups.entities.ui.CrmeUser;

public interface GroupPresenter extends BasePresenter<GroupView>{

    void onPhoneNumberSubmitted(String phoneNumber);

    void onCrmeUserLongClicked(CrmeUser item);

    void onCrmeUserSelected(CrmeUser item);
}
