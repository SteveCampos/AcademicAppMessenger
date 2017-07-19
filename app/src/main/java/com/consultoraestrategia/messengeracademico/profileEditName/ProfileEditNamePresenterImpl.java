package com.consultoraestrategia.messengeracademico.profileEditName;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.entities.Profile;

import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.lib.GreenRobotEventBus;
import com.consultoraestrategia.messengeracademico.profileEditName.event.ProfileEditEvent;
import com.consultoraestrategia.messengeracademico.profileEditName.ui.ProfileEditNameView;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by kike on 18/07/2017.
 */

public class ProfileEditNamePresenterImpl implements ProfileEditNamePresenter {

    private static final String TAG = ProfileEditNamePresenterImpl.class.getSimpleName();
    private ProfileEditNameView view;
    private ProfileEditNameInteractor interactor;
    private EventBus eventBus;
    private Resources resources;


    public ProfileEditNamePresenterImpl(ProfileEditNameView view, Context context) {
        this.view = view;
        Log.d(TAG, "ProfileEditNamePresenterImpl");
        this.interactor = new ProfileEditNameInteractorImpl();
        this.eventBus = GreenRobotEventBus.getInstance();
        this.resources = context.getResources();

    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        view = null;
        eventBus.unregister(this);
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        eventBus.register(this);
        initInputsView();
    }

    @Override
    public void initInputsView() {
        if (view != null) {
            view.initView();
        }
    }

    @Override
    public void editProfileName(String name, String phoneNumber,String uriParse) {
        validateData(name, phoneNumber,uriParse);
    }

    private void validateData(String name, String mPhoneNumber,String uriParse) {
        if (!isValideName(name)) {
            showFieldsError();
            return;
        }
        interactor.executeEditProfileName(name, mPhoneNumber,uriParse);
    }

    private boolean isValideName(String name) {
        return name != null && !name.isEmpty() && !name.trim().isEmpty() && !(name.length() > 16);
    }

    private void showFieldsError() {
        view.onProfileEditError(resources.getString(R.string.load_profile_error_fields));
    }

    @Subscribe
    @Override
    public void onEventMainThread(ProfileEditEvent profileEditEvent) {
        Log.d(TAG, "onEventMainThread");
        switch (profileEditEvent.getType()) {
            case ProfileEditEvent.OnProfileEditSuccess:
                onProfileEditSuccess(profileEditEvent.getProfile());
                break;
            case ProfileEditEvent.OnProfileEditdError:
                onProfileEditError();
                break;
        }
    }

    private void onProfileEditError() {
        if (view != null) {
            view.onProfileEditNameUploadError();
            Log.d(TAG, "onProfileUploadError");
        }
    }

    private void onProfileEditSuccess(Profile profile) {
        Log.d(TAG, "onProfileUploadSuccess");
        if (view != null) {
            view.forwardProfileData(profile);
        }
    }


}
