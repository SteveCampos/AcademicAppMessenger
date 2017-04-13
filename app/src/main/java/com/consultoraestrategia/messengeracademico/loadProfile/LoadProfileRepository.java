package com.consultoraestrategia.messengeracademico.loadProfile;

import android.net.Uri;

/**
 * Created by kike on 24/02/2017.
 */

public interface LoadProfileRepository {
    /*void registerNewImage(String mImage);
    void checkImage();
    void CheckForImageUser();
    */
    //void initProfileUserProcess(String mImage);
    void updateProfile(Uri uri, String mName, String mPhoneNumber);
    void initNewProfileVerificated(String phoneNumber);
}
