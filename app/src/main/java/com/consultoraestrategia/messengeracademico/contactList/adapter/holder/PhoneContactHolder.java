package com.consultoraestrategia.messengeracademico.contactList.adapter.holder;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
    TextView txtName;
    @BindView(R.id.txt_status)
    TextView txtStatus;
    @BindView(R.id.txt_time)
    AppCompatTextView txtFrom;
    @BindView(R.id.layout)
    RelativeLayout layout;

    public PhoneContactHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bind(final Contact contact, Context context, final ContactListener listener) {
        String name = contact.getName();
        String phoneNumber = contact.getPhoneNumber();
        if (phoneNumber != null) {
            txtStatus.setText(phoneNumber);
        }

        txtName.setText(name != null ? name : phoneNumber);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onContactSelected(contact);
            }
        });

    }
}
