package com.consultoraestrategia.messengeracademico.profile;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.lib.GreenRobotEventBus;
import com.consultoraestrategia.messengeracademico.profile.event.ProfileEvent;

/**
 * Created by kike on 3/06/2017.
 */

public class ProfileRepositoryImpl implements ProfileRepository {
    private static final String TAG = ProfileRepositoryImpl.class.getSimpleName();
    private ContentResolver resolver;

    public ProfileRepositoryImpl(ContentResolver resolver) {
        this.resolver = resolver;
    }


    @Override
    public void initProfileEdit(String phoneNumber) {
        Log.d(TAG, "ProfileRepositoryImplphoneNumber : " + phoneNumber);
        if (idPhone(phoneNumber) > 0) {
            post(ProfileEvent.OnProfileEdit,phoneNumber);
        } else {
            Log.d(TAG, "Error PhoneNumber");
        }
    }

    @Override
    public void initProfileInformation (String phoneNumber){
        if (idPhone(phoneNumber) > 0) {
            post(ProfileEvent.OnProfileInformation,phoneNumber);
        } else {
            Log.d(TAG, "Error PhoneNumber");
        }
    }


    public long idPhone(String phoneNumber) {
        long idPhone = 0;
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        // This query will return NAME and ID of conatct, associated with phone //number.
        Cursor mcursor = resolver.query(lookupUri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null, null, null);
        //Now retrive _ID from query result
        if (mcursor != null) {
            if (mcursor.moveToFirst()) {

                idPhone = Long.valueOf(mcursor.getString(mcursor.getColumnIndex(ContactsContract.PhoneLookup._ID)));

            }
        }
        return idPhone;
    }


    private void post(int type,String phoneNumber) {

        ProfileEvent event = new ProfileEvent();
        event.setType(type);
        event.setPhoneNumber(phoneNumber);
        event.setIdPhone(idPhone(phoneNumber));

        EventBus eventBus = GreenRobotEventBus.getInstance();
        eventBus.post(event);
    }

}
