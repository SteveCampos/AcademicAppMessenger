package com.consultoraestrategia.messengeracademico.verification;

/**
 * Created by Steve on 17/02/2017.
 */
public class VerificationInteractorImpl implements VerificationInteractor {

    private VerificationRepository repository;

    public VerificationInteractorImpl() {
        this.repository = new VerificationRepositoryImpl();
    }

    @Override
    public void executeInitVerificationProcess(String phoneNumber) {
        repository.initVerificationProcess(phoneNumber);
    }

    @Override
    public void executeValidateCode(String phoneNumber, String code) {
        repository.validateCode(phoneNumber, code);
    }
}
