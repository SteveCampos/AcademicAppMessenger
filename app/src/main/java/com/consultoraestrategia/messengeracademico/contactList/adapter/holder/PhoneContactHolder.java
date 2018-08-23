package com.consultoraestrategia.messengeracademico.contactList.adapter.holder;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.contactList.listeners.ContactListener;
import com.consultoraestrategia.messengeracademico.entities.Contact;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Steve on 7/03/2017.
 */
public class PhoneContactHolder extends RecyclerView.ViewHolder {


    @BindView(R.id.img_profile)
    CircleImageView imgProfile;
    @BindView(R.id.txt_name)
    AppCompatTextView txtName;
    @BindView(R.id.txt_time)
    AppCompatTextView txtTime;
    @BindView(R.id.img_status_message)
    AppCompatImageView imgStatusMessage;
    @BindView(R.id.txt_status)
    AppCompatTextView txtStatus;

    public PhoneContactHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bind(final Contact contact, Context context, final ContactListener listener) {

        String phoneNumber = null;
        String title = null;
        String uri = null;
        String verified = null;
        if (contact != null) {
            String name = contact.getName();
            phoneNumber = contact.getPhoneNumber();
            String uriProfile = contact.getPhotoUrl();

            title = !TextUtils.isEmpty(name) ? name : phoneNumber;
            uri = !TextUtils.isEmpty(uriProfile) ? uriProfile : "";
            verified = contact.getInfoVerified();
        }


        txtStatus.setText(phoneNumber);
        txtName.setText(title);
        if (!TextUtils.isEmpty(verified)) {
            txtName.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_verify,
                    0
            );
            String rolVerificado = String.format(itemView.getResources().getString(R.string.global_rol_verified), verified);
            txtStatus.setText(rolVerificado);
        }
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onContactSelected(contact);
            }
        });

        Glide.with(context)
                .load(uri)
                .centerCrop()
                .error(R.drawable.ic_users)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onImageClickdListener(contact);
            }
        });
    }
}
