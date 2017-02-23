package com.consultoraestrategia.messengeracademico.verification;

/**
 * Created by Steve on 17/02/2017.
 */

//ValidatePhoneNumberInteractor
public interface VerificationInteractor {
    void executeInitVerificationProcess(String phoneNumber);
    void executeValidateCode(String phoneNumber, String code);
}
