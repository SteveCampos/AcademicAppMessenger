package com.consultoraestrategia.messengeracademico.profile;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;

import com.consultoraestrategia.messengeracademico.UseCaseHandler;
import com.consultoraestrategia.messengeracademico.base.BasePresenter;
import com.consultoraestrategia.messengeracademico.base.BasePresenterImpl;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.entities.Contact_Table;
import com.consultoraestrategia.messengeracademico.importData.ui.ImportDataActivity;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.profile.event.ProfileEvent;
import com.consultoraestrategia.messengeracademico.profile.ui.ProfileView;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by kike on 3/06/2017.
 */

public class ProfilePresenterImpl extends BasePresenterImpl<ProfileView> implements ProfilePresenter {

    private static String TAG = ProfilePresenterImpl.class.getSimpleName();


    public ProfilePresenterImpl(UseCaseHandler handler, Resources res, EventBus eventBus) {
        super(handler, res, eventBus);
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    protected BasePresenter<ProfileView> getPresenterImpl() {
        return this;
    }

    @Override
    public void setExtras(Bundle extras) {
        super.setExtras(extras);
        manageExtras(extras);
    }

    private void manageExtras(Bundle extras) {
        String phonenumber = extras.getString(ImportDataActivity.EXTRA_PHONENUMBER);
        Contact contact =
                SQLite.select()
                        .from(Contact.class)
                        .where(Contact_Table.phoneNumber.eq(phonenumber))
                        .querySingle();
        if (contact != null) {
            loadContact(contact);
        }
    }

    private Contact contact;

    private void loadContact(Contact contact) {
        this.contact = contact;
        String imageUrl = contact.getPhotoUrl();
        String name = contact.getName();
        String phoneNumber = contact.getPhoneNumber();
        String verified = contact.getInfoVerified();
        managePhoto(imageUrl);
        showPhonenumber(phoneNumber);
        showName(name);
        manageTextVerified(verified);
    }

    private void manageTextVerified(String verified) {
        if (TextUtils.isEmpty(verified)) {
            showNotVerified();
        } else {
            showTextVerified(verified);
        }
    }

    private void showTextVerified(String verified) {
        if (view != null) {
            view.showTextVerified(verified);
        }
    }

    private void showNotVerified() {
        if (view != null) {
            view.showRegularUserWithoutVerification();
        }
    }

    private void showEmptyPhoto() {
        if (view != null) {
            view.showEmptyPhoto();
        }
    }

    private void showName(String name) {
        if (view != null && !TextUtils.isEmpty(name)) {
            view.showName(name);
        }
    }

    private void showPhonenumber(String phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber) && view != null) {
            view.showPhonenumber(phoneNumber);
        }
    }

    private void managePhoto(String url) {
        if (TextUtils.isEmpty(url)) {
            showEmptyPhoto();
        } else {
            showPhoto(url);
        }
    }

    private void showPhoto(String url) {
        if (view != null) {
            view.showPhoto(url);
        }
    }


    @Subscribe
    public void onEventMainThread(ProfileEvent profileEvent) {
        switch (profileEvent.getType()) {
            case ProfileEvent.OnProfileEdit:
                //onProfileEditSuccess(profileEvent.getIdPhone());
                break;
            case ProfileEvent.OnProfileInformation:
                //onProfileInformationSuccess(profileEvent.getIdPhone());
                break;
        }
    }

    @Override
    public void onPhoneNumberMenuClicked() {
        String phonenumberTo = contact.getPhoneNumber();
        if (TextUtils.isEmpty(phonenumberTo)) return;

        if (view != null) {
            view.launchChatActivity(phonenumberTo);
        }
    }

    @Override
    public void actionEditClicked() {
        String phonenumber = contact.getPhoneNumber();
        if (!TextUtils.isEmpty(phonenumber)) {
            if (view != null) view.editContactInPhone(phonenumber);
        }
    }

    @Override
    public void actionViewContactClicked() {
        String phonenumber = contact.getPhoneNumber();
        if (!TextUtils.isEmpty(phonenumber)) {
            if (view != null) view.showContactInPhone(phonenumber);
        }
    }
}
