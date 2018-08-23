package com.consultoraestrategia.messengeracademico.profileEditImage;

import android.content.Intent;
import android.net.Uri;

import com.consultoraestrategia.messengeracademico.base.BasePresenter;
import com.consultoraestrategia.messengeracademico.edittext.EditextDialogListener;
import com.consultoraestrategia.messengeracademico.profile.ProfilePresenter;
import com.consultoraestrategia.messengeracademico.profileEditImage.ui.ProfileEditImageView;
import com.consultoraestrategia.messengeracademico.profileEditName.event.ProfileEditEvent;

/**
 * Created by kike on 19/07/2017.
 */

public interface ProfileEditImagePresenter extends BasePresenter<ProfileEditImageView>, EditextDialogListener {

    void checkCurrentUser();

    void updateUserPhoto(Uri photoUri);

    void onEventMainThread(ProfileEditEvent loadProfileEvent);

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onBtnEditNameClicked();
}
