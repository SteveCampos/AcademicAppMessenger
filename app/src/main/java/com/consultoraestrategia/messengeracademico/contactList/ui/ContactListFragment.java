package com.consultoraestrategia.messengeracademico.contactList.ui;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.contactList.ContactListPresenter;
import com.consultoraestrategia.messengeracademico.contactList.ContactListPresenterImpl;
import com.consultoraestrategia.messengeracademico.contactList.ContactListView;
import com.consultoraestrategia.messengeracademico.contactList.adapter.ContactListAdapter;
import com.consultoraestrategia.messengeracademico.contactList.listeners.ContactListener;
import com.consultoraestrategia.messengeracademico.dialogProfile.DialogProfile;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.entities.Contact_Table;
import com.consultoraestrategia.messengeracademico.verification.ui.VerificationActivity;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactListFragment extends Fragment implements ContactListView, ContactListener {
    private static final String TAG = ContactListFragment.class.getSimpleName();
    @BindView(R.id.my_recycler_view)
    RecyclerView myRecyclerView;
    Unbinder unbinder;

    private ContactListPresenter presenter;
    private ContactListAdapter adapter;

    /*
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;*/

    private Contact me;


    public ContactListFragment() {
        // Required empty public constructor
    }

    /*
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactListFragment newInstance(/*String param1, String param2*/) {
        ContactListFragment fragment = new ContactListFragment();
        /*Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
        presenter = new ContactListPresenterImpl(this);
        presenter.onCreate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        setRetainInstance(true);
        init();
        initContact();
        return view;
    }

    private void initContact() {
        String phoneNumber = getPhoneNumberFromPreferences();
        Log.d(TAG, "phoneNumber: " + phoneNumber);
        if (phoneNumber != null) {
            me = getContact(phoneNumber);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.getContacts();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    private void init() {
        setupRecycler();
    }

    private void setupRecycler() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        myRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new ContactListAdapter(getActivity(), new ArrayList<Contact>(), this);
        myRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void showContacts(List<Contact> contacts) {
        adapter.setContacts(contacts);
    }

    @Override
    public void onContactSelected(Contact contact) {
        Log.d(TAG, "onContactSelected: " + contact.getUserKey());

        presenter.onContactSelected(contact);/*
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_EMISOR_PHONENUMBER, me.getUserKey());
        intent.putExtra(ChatActivity.EXTRA_RECEPTOR_PHONENUMBER, contact.getUserKey());
        getActivity().startActivity(intent);*/
    }

    @Override
    public void onImageClickdListener(Contact contact) {
        Bundle args = new Bundle();
        args.putString("imageUri", contact.getPhotoUri());
        args.putString("nameContact", contact.getName());
        args.putString("phoneNumber",contact.getPhoneNumber());
        DialogProfile newFragment = new DialogProfile();
        newFragment.setArguments(args);
        newFragment.show(getFragmentManager(), "TAG");
    }

    private String getPhoneNumberFromPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return preferences.getString(VerificationActivity.PREF_PHONENUMBER, "+51993061806");
    }

    private Contact getContact(String phoneNumber) {
        return SQLite.select()
                .from(Contact.class)
                .where(Contact_Table.phoneNumber.eq(phoneNumber))
                .querySingle();
    }
}
