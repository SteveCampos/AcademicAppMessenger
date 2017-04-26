package com.consultoraestrategia.messengeracademico.loadProfile.listener;

import com.consultoraestrategia.messengeracademico.entities.Profile;

/**
 * Created by kike on 13/04/2017.
 */

public interface UploadProfileListener {
    void onSucess(Profile profile);
    void onError(String error);
}
