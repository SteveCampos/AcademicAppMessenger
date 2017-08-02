package com.consultoraestrategia.messengeracademico.chat.domain.usecase;

import android.util.Log;

import com.consultoraestrategia.messengeracademico.UseCase;
import com.consultoraestrategia.messengeracademico.data.ContactDataSource;
import com.consultoraestrategia.messengeracademico.data.ContactRepository;
import com.consultoraestrategia.messengeracademico.entities.Contact;

/**
 * Created by @stevecampos on 16/05/2017.
 */

public class GetContact extends UseCase<GetContact.RequestValues, GetContact.ResponseValue> {

    private static final String TAG = GetContact.class.getSimpleName();
    private ContactRepository contactRepository;

    public GetContact(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        String phoneNumber = requestValues.getPhoneNumber();
        contactRepository.getContact(phoneNumber, new ContactDataSource.GetContactCallback() {
            @Override
            public void onContactLoaded(Contact contact) {
                Log.d(TAG, "contact : " + contact.toString());
                GetContact.ResponseValue responseValue = new GetContact.ResponseValue(contact);
                getUseCaseCallback().onSuccess(responseValue);
            }

            @Override
            public void onDataNotAvailable() {
                getUseCaseCallback().onError();
            }
        });
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final String phoneNumber;

        public RequestValues(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final Contact contact;

        public ResponseValue(Contact contact) {
            this.contact = contact;
        }

        public Contact getContact() {
            return contact;
        }
    }
}
