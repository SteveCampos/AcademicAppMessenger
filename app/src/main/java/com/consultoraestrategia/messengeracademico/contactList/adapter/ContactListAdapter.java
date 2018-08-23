package com.consultoraestrategia.messengeracademico.contactList.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.contactList.adapter.holder.AcademicContactHolder;
import com.consultoraestrategia.messengeracademico.contactList.adapter.holder.PhoneContactHolder;
import com.consultoraestrategia.messengeracademico.contactList.listeners.ContactListener;
import com.consultoraestrategia.messengeracademico.entities.Contact;

import java.util.List;

/**
 * Created by Steve on 7/03/2017.
 */

public class ContactListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_FROM_PHONE = 1;
    private static final int VIEW_TYPE_FROM_ACADEMIC = 2;

    private Context context;
    private List<Contact> contacts;
    private ContactListener listener;

    public ContactListAdapter(Context context, List<Contact> contacts, ContactListener listener) {
        this.context = context;
        this.contacts = contacts;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_FROM_PHONE;
    }

    public void setContacts(List<Contact> contacts) {
        if (!contacts.isEmpty()) {
            this.contacts = contacts;
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            default:
                View v1 = inflater.inflate(R.layout.contactlist_item_phonecontact, parent, false);
                viewHolder = new PhoneContactHolder(v1);
                break;
            case VIEW_TYPE_FROM_ACADEMIC:
                View v2 = inflater.inflate(R.layout.contactlist_item_academiccontact, parent, false);
                viewHolder = new AcademicContactHolder(v2);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        switch (holder.getItemViewType()) {
            default:
                PhoneContactHolder vh1 = (PhoneContactHolder) holder;
                vh1.bind(contact, context, listener);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

}
