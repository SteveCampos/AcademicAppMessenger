package com.consultoraestrategia.messengeracademico.group.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.base.adapter.BaseAdapter;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.importGroups.entities.ui.CrmeUser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class IntegranteAdapter extends BaseAdapter<CrmeUser, IntegranteAdapter.ViewHolder> {
    private static final String TAG = IntegranteAdapter.class.getSimpleName();

    public IntegranteAdapter(List<CrmeUser> items) {
        super(items);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ((ViewHolder) holder).bind(items.get(position), listener);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img_profile)
        CircleImageView imgProfile;
        @BindView(R.id.txt_name)
        AppCompatTextView txtName;
        @BindView(R.id.txt_reference)
        AppCompatTextView txtReference;
        @BindView(R.id.txt_phonenumber)
        AppCompatTextView txtPhonenumber;
        @BindView(R.id.txt_admin)
        AppCompatTextView txtAdmin;
        @BindView(R.id.img_status_messenger)
        AppCompatImageView imgStatusMessenger;
        @BindView(R.id.border_bottom)
        View borderBottom;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final CrmeUser user, final Listener<CrmeUser> listener) {
            String displayName = user.getDisplayName();
            String phoneNumber = user.getPhoneNumber();
            boolean messengerInstalled = user.isMessengerInstalled();
            if (messengerInstalled) {
                Contact contact = user.getContact();
                String urlProfile = contact.getPhotoUrl();
                Glide
                        .with(itemView.getContext())
                        .load(urlProfile)
                        .into(imgProfile);
                imgStatusMessenger.setVisibility(View.VISIBLE);

            } else {
                imgStatusMessenger.setVisibility(View.GONE);
                Glide
                        .with(itemView.getContext())
                        .load("https://cdn1.iconfinder.com/data/icons/basic-ui-elements-color-round/3/33-128.png")
                        .into(imgProfile);
            }

            txtReference.setText(displayName);

            boolean admin = user.isAdmin();

            if (admin) {
                txtAdmin.setVisibility(View.VISIBLE);
            } else {
                txtAdmin.setVisibility(View.GONE);
            }

            String name = user.getName();
            if (!TextUtils.isEmpty(name))
                txtName.setText(name);

            String phoneNumberText = phoneNumber;
            if (TextUtils.isEmpty(phoneNumberText)) {
                phoneNumberText = "Nro sin registrar";
            }

            if (!messengerInstalled) {
                txtPhonenumber.setText(phoneNumberText + " (No tiene messenger)");
            } else {
                txtPhonenumber.setText(phoneNumberText + " (Si tiene messenger)");
            }


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) listener.onItemSelected(user);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (listener != null) listener.onItemLongSelected(user);
                    return false;
                }
            });
        }
    }
}
