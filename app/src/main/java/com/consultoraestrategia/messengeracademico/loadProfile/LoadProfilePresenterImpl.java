package com.consultoraestrategia.messengeracademico.loadProfile;


import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.entities.Profile;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.lib.GreenRobotEventBus;
import com.consultoraestrategia.messengeracademico.loadProfile.event.LoadProfileEvent;
import com.consultoraestrategia.messengeracademico.loadProfile.ui.LoadProfileView;

import org.greenrobot.eventbus.Subscribe;


/**
 * Created by kike on 24/02/2017.
 */

public class LoadProfilePresenterImpl implements LoadProfilePresenter {
    private static final String TAG = LoadProfilePresenterImpl.class.getSimpleName();
    private LoadProfileView view;
    private LoadProfileInteractor interactor;
    private EventBus eventBus;
    private Resources resources;


    public LoadProfilePresenterImpl(LoadProfileView view, Context context) {
        this.view = view;
        this.interactor = new LoadProfileInteractorImpl();
        this.eventBus = new GreenRobotEventBus();
        this.resources = context.getResources();
    }

    @Override
    public void onDestroy() {
        view = null;
        eventBus.unregister(this);
    }

    @Override
    public void onCreate() {
        eventBus.register(this);
        initInputsView();
    }

    @Override
    public void initInputsView() {
        if (view != null) {
            view.showLoadProfileViews();
        }
    }

    @Override
    public void updateProfile(Uri uriPhotoProfile, String mName, String mPhoneNumber) {
        if (view != null) {
            view.showProgress();
        }
        validateData(uriPhotoProfile, mName, mPhoneNumber);
    }

    private boolean isValideName(String name) {
        return   name != null && !name.isEmpty()  && !name.trim().isEmpty();
    }


    private void validateData(Uri uri, String name, String mPhoneNumber) {
        if (!isValideName(name)) {
            showFieldsError();
            return;
        }
        interactor.executeUpdateProfile(uri, name, mPhoneNumber);
    }

    private void showFieldsError(){
        view.onRegisterNewProfileError(resources.getString(R.string.load_profile_error_fields));
        view.hideProgress();
    }


    @Override
    public void profileVerificated(String phoneNumber) {
        if (view != null) {
            Log.d(TAG, "profileVerificated :");
            view.showProgress();
        }
        interactor.executeVerificatedProfile(phoneNumber);

    }


    @Subscribe
    @Override
    public void onEventMainThread(LoadProfileEvent loadProfileEvent) {
        /*Consumen metodos de la Vista!!!!!!*/
        switch (loadProfileEvent.getType()) {
            case LoadProfileEvent.OnProfileUploadSuccess:
                onProfileUploadSuccess(loadProfileEvent.getProfile());
                break;
            case LoadProfileEvent.OnProfileUploadError:
                onProfileUploadError(loadProfileEvent.getError());
                break;
        }

    }

    private void onProfileUploadSuccess(Profile profile) {
        Log.d(TAG, "onProfileUploadSuccess");
        if (view != null) {
            view.hideProgress();
            view.forwardToImportData(profile);
        }
    }

    private void onProfileUploadError(String error) {
        Log.d(TAG, "onProfileUploadError");
        if (view != null) {
            view.onUpladProfileError(error);
            view.hideLoadProfileViews();
        }
    }



}
