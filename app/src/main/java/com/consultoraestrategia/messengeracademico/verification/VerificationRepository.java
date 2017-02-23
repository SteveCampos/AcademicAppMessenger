package com.consultoraestrategia.messengeracademico.verification;

/**
 * Created by Steve on 17/02/2017.
 */

public interface VerificationRepository {
    void initVerificationProcess(String phoneNumber);
    void validateCode(String phoneNumber, String code);
}
