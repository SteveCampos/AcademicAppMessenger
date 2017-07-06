package com.consultoraestrategia.messengeracademico.profile;

import android.content.ContentResolver;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.lib.GreenRobotEventBus;
import com.consultoraestrategia.messengeracademico.profile.event.ProfileEvent;
import com.consultoraestrategia.messengeracademico.profile.ui.ProfileView;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by kike on 3/06/2017.
 */

public class ProfilePresenterImpl implements ProfilePresenter {

    private static String TAG = ProfilePresenterImpl.class.getSimpleName();

    //Comunica con la view
    private ProfileView view;
    private ProfileInteractor interactor;
    private EventBus eventBus;


    public ProfilePresenterImpl(ProfileView view, ContentResolver resolver) {
        Log.d(TAG,"ProfilePresenterImpl");
        this.view = view;
        this.interactor = new ProfileInteractorImpl(resolver);
        this.eventBus = new GreenRobotEventBus();


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
        if(view !=null){
            view.setToolbar();
            Log.d(TAG,"initInputsView");
        }
    }

    @Override
    public void verificatedProfileEdit(String phoneNumber) {
        Log.d(TAG,"verificatedProfileEdit : "+ phoneNumber);
        interactor.executeProfileEdit(phoneNumber);
    }
    @Override
    public void verificatedProfileInformation(String phoneNumber){
        interactor.executeProfileInformation(phoneNumber);
    }

    @Subscribe
    @Override
    public void onEventMainThread(ProfileEvent profileEvent) {
         /*Consumen metodos de la Vista!!!!!!*/
        switch (profileEvent.getType()) {
            case ProfileEvent.OnProfileEdit:
                onProfileEditSuccess(profileEvent.getIdPhone());
                break;
            case ProfileEvent.OnProfileInformation:
                onProfileInformationSuccess(profileEvent.getIdPhone());
                break;
        }
    }


    private void onProfileEditSuccess(long idphone) {
        if (view != null) {
            Log.d(TAG,"onProfileOptionsSuccess");
            view.forwardToEditData(idphone);
        }

    }

    private void onProfileOptionsError(String error) {
        if (view != null) {
            view.onProfileError(error);
        }
    }

    private void onProfileInformationSuccess(long idphone) {
        if (view != null) {
            Log.d(TAG,"onProfileOptionsSuccess");
            view.forwardToShowData(idphone);
        }
    }

}
