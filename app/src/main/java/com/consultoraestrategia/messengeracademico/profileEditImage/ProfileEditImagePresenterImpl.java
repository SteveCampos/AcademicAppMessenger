package com.consultoraestrategia.messengeracademico.profileEditImage;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.entities.Profile;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.lib.GreenRobotEventBus;

import com.consultoraestrategia.messengeracademico.profileEditName.event.ProfileEditEvent;
import com.consultoraestrategia.messengeracademico.profileEditImage.ui.ProfileEditImageView;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by kike on 19/07/2017.
 */

public class ProfileEditImagePresenterImpl implements ProfileEditImagePresenter {

    private static final String TAG = ProfileEditImagePresenterImpl.class.getSimpleName();
    private ProfileEditImageView view;
    private ProfileEditImageInteractor interactor;
    private EventBus eventBus;


    public ProfileEditImagePresenterImpl(ProfileEditImageView view, Context context) {
        this.view = view;
        this.interactor = new ProfileEditImageInteractorImpl();
        this.eventBus = GreenRobotEventBus.getInstance();
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
            view.showProfileEditViews();
        }
    }

    @Override
    public void editProfileImage(String name, String phoneNumber, Uri uriParse) {
        if (view != null) {
            Log.d(TAG, "profileVerificated :");
            view.showProgressBar();
        }
        interactor.executeProfileEditImage(name, phoneNumber, uriParse);
    }

    @Subscribe
    @Override
    public void onEventMainThread(ProfileEditEvent profileEditEvent) {
        switch (profileEditEvent.getType()) {
            case ProfileEditEvent.OnProfileEditSuccess:
                onProfileUploadSuccess(profileEditEvent.getProfile());
                break;
            case ProfileEditEvent.OnProfileEditdError:
                onProfileUploadError(profileEditEvent.getError());
                break;
        }
    }

    private void onProfileUploadSuccess(Profile profile) {
        Log.d(TAG, "onProfileUploadSuccess");
        if (view != null) {
            view.hideProgressBar();
            view.forwardProfileData(profile);
        }
    }

    private void onProfileUploadError(String error) {
        Log.d(TAG, "onProfileUploadError");
        if (view != null) {
            view.onProfileEditImageError(error);
        }
    }
}
