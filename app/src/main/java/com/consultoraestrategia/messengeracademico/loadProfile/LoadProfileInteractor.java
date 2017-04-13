package com.consultoraestrategia.messengeracademico.loadProfile;

import android.net.Uri;

/**
 * Created by kike on 24/02/2017.
 */

public interface LoadProfileInteractor {
    void executeUpdateProfile(Uri mUri, String mName, String mPhoneNumber);
    void executeVerificatedProfile(String phoneNumber);
}
