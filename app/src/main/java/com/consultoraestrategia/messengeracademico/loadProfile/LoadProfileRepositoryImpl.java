package com.consultoraestrategia.messengeracademico.loadProfile;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.domain.FirebaseLoadProfile;
import com.consultoraestrategia.messengeracademico.entities.Photo;
import com.consultoraestrategia.messengeracademico.entities.Profile;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.lib.GreenRobotEventBus;
import com.consultoraestrategia.messengeracademico.loadProfile.event.LoadProfileEvent;
import com.consultoraestrategia.messengeracademico.loadProfile.listener.UploadProfileListener;
import com.consultoraestrategia.messengeracademico.main.ui.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static com.consultoraestrategia.messengeracademico.importData.ui.ImportDataActivity.PREF_STEP_IMPORT_DATA;

/**
 * Created by kike on 24/02/2017.
 */

public class LoadProfileRepositoryImpl implements LoadProfileRepository {


    private static String TAG = LoadProfilePresenterImpl.class.getSimpleName();
    private FirebaseLoadProfile helper;


    public LoadProfileRepositoryImpl() {
        this.helper = new FirebaseLoadProfile();

    }

    @Override
    public void updateProfile(final Uri mUri, final String mName, final String mPhoneNumber) {
        Log.d(TAG, "REpositoryuMyUri: " + mUri);

        Profile profile = new Profile();
        Photo photo = new Photo();
        photo.setUrl(mUri.toString());
        profile.setPhoto(photo);
        profile.setmName(mName);
        profile.setmPhoneNumber(mPhoneNumber);

        helper.uploadProfile(profile, new UploadProfileListener() {
            @Override
            public void onSucess(Profile profile) {
                post(LoadProfileEvent.OnProfileUploadSuccess, getProfileProcess(mUri,mName, mPhoneNumber), null);
            }

            @Override
            public void onError(String error) {
                post(LoadProfileEvent.OnProfileUploadError, getProfileProcess(mUri,mName, mPhoneNumber), error);
            }
        });
    }

    @Override
    public void initNewProfileVerificated(String phoneNumber) {
        helper.verificatedProfile(phoneNumber, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    Profile profile = dataSnapshot.getValue(Profile.class);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void post(int type, Profile loadProfile, String errorMessage) {

        LoadProfileEvent event = new LoadProfileEvent();
        event.setType(type);
        //event.setmPhotoUser(loadProfile.getmPhotoUrl());
        event.setmName(loadProfile.getmName());
        event.setmPhoneNumber(loadProfile.getmPhoneNumber());
        event.setPhoto(loadProfile.getPhoto());


        if (errorMessage != null) {
            event.setError(errorMessage);
        }

        EventBus eventBus = GreenRobotEventBus.getInstance();
        eventBus.post(event);
    }


    private Profile getProfileProcess(Uri uri, String mName, String mPhoneNumber) {
        Profile loadProfile = new Profile();
        loadProfile.setPhoto(getPhotoProcess(uri));
        loadProfile.setmName(mName);
        loadProfile.setmPhoneNumber(mPhoneNumber);
        return loadProfile;
    }

    private Photo getPhotoProcess(Uri uri) {
        Photo photo = new Photo();
        photo.setUrl(uri.toString());
        return photo;
    }






}
