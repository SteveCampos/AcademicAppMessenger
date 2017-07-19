package com.consultoraestrategia.messengeracademico.profileEditName.ui;

import com.consultoraestrategia.messengeracademico.entities.Profile;

/**
 * Created by kike on 18/07/2017.
 */

public interface ProfileEditNameView {

    void onProfileEditError(String message);
    void forwardProfileData(Profile profile);
    void initView();
    void onProfileEditNameUploadError();


}
