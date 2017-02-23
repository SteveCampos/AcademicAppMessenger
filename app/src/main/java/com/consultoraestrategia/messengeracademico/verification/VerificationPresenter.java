package com.consultoraestrategia.messengeracademico.verification;

import com.consultoraestrategia.messengeracademico.verification.events.VerificationEvent;
import com.lamudi.phonefield.PhoneInputLayout;

/**
 * Created by Steve on 17/02/2017.
 */

public interface VerificationPresenter {
    void onCreate();
    void onDestroy();

    void initVerificationViews();
    void validatePhoneNumber(PhoneInputLayout phoneInputLayout);
    void verificatePhoneNumber(String phoneNumber);
    void validateCode(String phoneNumber, String code);
    void onEventMainThread(VerificationEvent event);

    void onMessageReceived(String phoneNumber, String messateText);

}
