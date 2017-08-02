package com.consultoraestrategia.messengeracademico.verification;

import android.util.Log;

import com.consultoraestrategia.messengeracademico.utils.StringUtils;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.lib.GreenRobotEventBus;
import com.consultoraestrategia.messengeracademico.verification.events.VerificationEvent;
import com.consultoraestrategia.messengeracademico.verification.ui.VerificationView;
import com.lamudi.phonefield.PhoneInputLayout;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Steve on 17/02/2017.
 */

public class VerificationPresenterImpl implements VerificationPresenter {

    private static final String TAG = VerificationPresenterImpl.class.getSimpleName();
    private VerificationView view;
    private VerificationInteractor interactor;
    private EventBus eventBus;

    public VerificationPresenterImpl(VerificationView view) {
        this.view = view;
        this.interactor = new VerificationInteractorImpl();
        this.eventBus = GreenRobotEventBus.getInstance();
    }

    @Override
    public void onCreate() {
        eventBus.register(this);
        initVerificationViews();
    }

    @Override
    public void initVerificationViews() {
        if (view != null) {
            view.showVerificationViews();
            view.hideWatingViews();
            view.checkGooglePlayServicesAvailable();
        }
    }

    @Override
    public void onDestroy() {
        view = null;
        eventBus.unregister(this);
    }

    @Override
    public void onResume() {
        if (view!=null){
            view.checkGooglePlayServicesAvailable();
        }
    }


    @Override
    public void validatePhoneNumber(PhoneInputLayout phoneInputLayout) {
        if (view != null) {
            if (phoneInputLayout.isValid()) {
                view.setPhoneNumber(phoneInputLayout.getPhoneNumber());
                view.showDialog(phoneInputLayout.getPhoneNumber());
            } else {
                view.onInvalidPhoneNumber();
            }
        }
    }


    @Override
    public void verificatePhoneNumber(String phoneNumber) {
        Log.d(TAG, "verificatePhoneNumber");
        if (view != null) {
            view.showProgress();
        }
        //interactor.executeInitVerificationProcess(phoneNumber);
        onPhoneNumberSended(phoneNumber);
    }


    @Subscribe
    @Override
    public void onEventMainThread(VerificationEvent event) {
        switch (event.getType()) {
            case VerificationEvent.OnPhoneNumberSended:
                onPhoneNumberSended(event.getPhoneNumberVerified().getPhoneNumber());
                break;
            case VerificationEvent.OnPhoneNumberSendedError:
                onPhoneNumberSenderError(event.getError());
                break;
            case VerificationEvent.OnPhoneNumberVerificated:
                OnPhoneNumberVerificated(event);
                break;
            case VerificationEvent.OnPhoneNumberFailedToVerificated:
                OnPhoneNumberFailedToVerificated(event);
                break;
            case VerificationEvent.OnInvalidCode:
                onInvalidCode(event);
                break;
        }

    }

    private void onInvalidCode(VerificationEvent event){
        if (view != null) {
            view.hideProgress();
            view.onInvalidCode();
        }
    }

    private void OnPhoneNumberFailedToVerificated(VerificationEvent event) {
        if (view != null) {
            view.hideProgress();
            view.onPhoneNumberFailedToVerificated(event.getError());
        }

    }

    private void OnPhoneNumberVerificated(VerificationEvent event) {
        if (view != null) {
            view.hideProgress();
            view.onPhoneNumberVerificated(event.getPhoneNumberVerified().getPhoneNumber());
        }
    }

    @Override
    public void onMessageReceived(String phoneNumber, String messageText) {
        if (view != null) {
            if (getCodeFromText(messageText).length() == 6) {
                view.setCode(getCodeFromText(messageText));
            } else {
                onPhoneNumberSenderError("Error reading code");
            }
        }
    }


    @Override
    public void validateCode(String phoneNumber, String code) {
        if (view != null) {
            view.showProgress();
        }
        interactor.executeValidateCode(phoneNumber, code);
    }

    private String getCodeFromText(String message) {
        String str = StringUtils.getCodeBetweenStrings(message, "code", ".");
        Log.d(TAG, "getCodeFromText code: " + str);
        return str;
    }

    private void onPhoneNumberSenderError(String error) {
        if (view != null) {
            view.hideProgress();
            view.OnPhoneNumberSendedError(error);
        }
    }

    private void onPhoneNumberSended(String phoneNumber) {
        if (view != null) {
            view.hideProgress();
            view.showWatingViews(phoneNumber);
            view.hideVerificationViews();
            view.listenMessages();
            view.setTextCodeChangeListener();
        }
    }

}
