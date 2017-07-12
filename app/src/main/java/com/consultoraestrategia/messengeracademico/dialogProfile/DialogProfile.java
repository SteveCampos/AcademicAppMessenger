package com.consultoraestrategia.messengeracademico.dialogProfile;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.chat.ui.ChatActivity;
import com.consultoraestrategia.messengeracademico.dialogProfile.ui.DialogProfileView;
import com.consultoraestrategia.messengeracademico.importData.ui.ImportDataActivity;
import com.consultoraestrategia.messengeracademico.profile.ui.ProfileActivity;

import butterknife.OnClick;

/**
 * Created by kike on 7/07/2017.
 */

public class DialogProfile extends DialogFragment implements DialogProfileView {

    public static String TAG = DialogProfile.class.getSimpleName();
    private ImageView imageViewDialog, imageStartChat, imageStartInfo, ImageNull;
    private TextView nameDialog;
    private DialogProfilePresenter presenter;
    private RelativeLayout layout;


    public DialogProfile() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }


    public static DialogProfile newInstance(String title) {
        DialogProfile frag = new DialogProfile();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    String imageUri, nameContact, phoneNumber;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        Bundle mArgs = getArguments();
        imageUri = mArgs.getString("imageUri");
        nameContact = mArgs.getString("nameContact");
        phoneNumber = mArgs.getString("phoneNumber");
        Log.d(TAG, "IMAGEURI" + imageUri);
        View viewDialog = inflater.inflate(R.layout.dialog_fragment_profile, container);


        presenter = new DialogProfilePresenterImpl(this);
        presenter.onCreate();
        return viewDialog;
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        if (getDialog() == null) {
            return;
        }

        int dialogWidth = getResources().getDimensionPixelSize(R.dimen.dialog_profile_width);
        int dialogHeight = getResources().getDimensionPixelSize(R.dimen.dialog_profile_height);
        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
        Log.d(TAG, "dialogHeight :" + dialogHeight + ",  dialogWidth " + dialogWidth);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get field from viewDialog
        imageViewDialog = (ImageView) view.findViewById(R.id.imageViewDialog);
        // ImageNull = (ImageView) view.findViewById(R.id.imageNull);
        imageStartChat = (ImageView) view.findViewById(R.id.image_start_chat);
        imageStartInfo = (ImageView) view.findViewById(R.id.image_start_info);
        nameDialog = (TextView) view.findViewById(R.id.textview_name);
        layout = (RelativeLayout) view.findViewById(R.id.relative_second);
        // Fetch arguments from bundle and set title
        onVerificatedProfileDialog(nameContact, phoneNumber);
        presenter.validImageUri(imageUri);
        startIntent();

      /*  Glide.with(this)
                .load(imageUri)
                .centerCrop()
                .error(getResources().getDrawable(R.drawable.ic_users))
                .into(imageViewDialog);*/
    }


    @Override
    public void onVerificatedProfileDialog(String name, String phoneNumber) {
        presenter.verificatedDialogProfile(name, phoneNumber);
    }

    @Override
    public void onImageUri(String imageUri) {

        Glide.with(this)
                .load(imageUri)
                .centerCrop()
                .into(imageViewDialog);
        // layout.setVisibility(View.GONE);
    }

    @Override
    public void onImageUriNull() {
        //   layout.setVisibility(View.VISIBLE);
        imageViewDialog.setImageDrawable(getResources().getDrawable(R.drawable.ic_users));

        Log.d(TAG, "PROFILE NULL: ");
    }

    @Override
    public void onVerificatedProfileDialogError(String error) {
        Log.d(TAG, error);
    }

    @Override
    public void initView() {
        nameDialog.setText(nameContact);
    }

    public void startChat() {
        imageStartChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "CLICKED STARTCHAT");
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra(ChatActivity.EXTRA_RECEPTOR_PHONENUMBER, phoneNumber);
                startActivity(intent);
                dissmissDialog();
            }
        });

    }


    public void startProfileInfo() {
        imageStartInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtra(ImportDataActivity.EXTRA_PHOTO_URI, imageUri);
                intent.putExtra(ImportDataActivity.EXTRA_NAME, nameContact);
                intent.putExtra(ImportDataActivity.EXTRA_PHONENUMBER, phoneNumber);
                startActivity(intent);
                dissmissDialog();
            }
        });
    }

    private void dissmissDialog() {
        getDialog().dismiss();
    }

    private void startIntent() {
        startChat();
        startProfileInfo();
    }


}
