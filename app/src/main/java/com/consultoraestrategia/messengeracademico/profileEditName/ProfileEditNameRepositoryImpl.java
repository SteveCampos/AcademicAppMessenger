package com.consultoraestrategia.messengeracademico.profileEditName;


import android.util.Log;

import com.consultoraestrategia.messengeracademico.domain.FirebaseLoadProfile;
import com.consultoraestrategia.messengeracademico.entities.Photo;
import com.consultoraestrategia.messengeracademico.entities.Profile;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.lib.GreenRobotEventBus;
import com.consultoraestrategia.messengeracademico.profileEditName.event.ProfileEditEvent;
import com.consultoraestrategia.messengeracademico.profileEditName.listener.ProfileEditListener;

/**
 * Created by kike on 18/07/2017.
 */

public class ProfileEditNameRepositoryImpl implements ProfileEditNameRepository {


    private static String TAG = ProfileEditNameRepositoryImpl.class.getSimpleName();
    private FirebaseLoadProfile helper;


    public ProfileEditNameRepositoryImpl() {
        this.helper = new FirebaseLoadProfile();
        Log.d(TAG, "ProfileEditNameRepositoryImpl");

    }

    @Override
    public void editProfileOptionsName(final String name, String phoneNumber,String uriParse) {

        Profile profile = new Profile();

        //Photo photo = new Photo();

        profile.setPhoto(getPhotoProcess(uriParse));
        profile.setmName(name);
        profile.setmPhoneNumber(phoneNumber);
        /*
        Profile profile = new Profile();
        profile.setmName(name);
        profile.setmPhoneNumber(phoneNumber);*/
        firebaseUploadProfile(profile);

    }

    private void firebaseUploadProfile(final Profile profile) {
        Log.d(TAG,"firebaseUploadProfile ");
        helper.editProfileName(profile,new ProfileEditListener() {
            @Override
            public void onSucess(Profile profile) {
                Log.d(TAG, "onSucess");
                post(ProfileEditEvent.OnProfileEditSuccess,profile, null);
            }

            @Override
            public void onError(String error) {
                Log.d(TAG, "error");
                post(ProfileEditEvent.OnProfileEditdError,profile, error);
            }
        });
    }

    private void post(int type,Profile profile,  String errorMessage) {
        Log.d(TAG, "onSucess");

        ProfileEditEvent event = new ProfileEditEvent();
        event.setType(type);
        event.setmName(profile.getmName());
        profile.setmName(profile.getmName());
        event.setPhoto(profile.getPhoto());
        event.setProfile(profile);

        if (errorMessage != null) {
            event.setError(errorMessage);
        }
        EventBus eventBus = GreenRobotEventBus.getInstance();
        eventBus.post(event);

    }

    private Photo getPhotoProcess(String uri) {
        Photo photo = new Photo();
        validateUri(photo, uri);
        //photo.setUrl(uri.toString());
        return photo;
    }

    private void validateUri(Photo photo, String uri) {
        if (uri == null) {
            photo.setUrl("");
        } else {
            photo.setUrl(uri.toString());
        }
    }

}
