package com.consultoraestrategia.messengeracademico.group;

import com.consultoraestrategia.messengeracademico.base.BaseView;
import com.consultoraestrategia.messengeracademico.importGroups.entities.ui.CrmeUser;

import java.util.List;

public interface GroupView extends BaseView<GroupPresenter> {

    void showGroupName(String groupName);
    void showMemberCount(int count);
    void showIntegrantes(List<CrmeUser> crmeUsers);

    void launchChat(CrmeUser item);
}
