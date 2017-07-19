package com.consultoraestrategia.messengeracademico.profileEditName.listener;

import com.consultoraestrategia.messengeracademico.entities.Profile;

/**
 * Created by kike on 18/07/2017.
 */

public interface ProfileEditListener {
    void onSucess(Profile profile);
    void onError(String error);
}
