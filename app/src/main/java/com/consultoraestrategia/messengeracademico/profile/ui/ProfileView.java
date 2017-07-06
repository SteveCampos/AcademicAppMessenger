package com.consultoraestrategia.messengeracademico.profile.ui;

/**
 * Created by kike on 3/06/2017.
 */

public interface ProfileView {

    void forwardToEditData(long idphone);
    void forwardToShowData(long idphone);
    void setToolbar();

    void onProfileError(String error);
}
