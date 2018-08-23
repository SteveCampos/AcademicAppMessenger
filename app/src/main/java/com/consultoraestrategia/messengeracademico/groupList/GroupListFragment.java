package com.consultoraestrategia.messengeracademico.groupList;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.base.adapter.BaseAdapter;
import com.consultoraestrategia.messengeracademico.domain.FirebaseGroup;
import com.consultoraestrategia.messengeracademico.domain.FirebaseHelper;
import com.consultoraestrategia.messengeracademico.group.GroupActivity;
import com.consultoraestrategia.messengeracademico.groupList.adapter.GroupAdapter;
import com.consultoraestrategia.messengeracademico.importGroups.entities.ui.Grupo;
import com.consultoraestrategia.messengeracademico.utils.PhonenumberUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class GroupListFragment extends Fragment {

    private static final String TAG = GroupListFragment.class.getSimpleName();
    @BindView(R.id.rcv_groups)
    RecyclerView rcvGroups;
    Unbinder unbinder;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.text_message)
    TextView textMessage;
    @BindView(R.id.bttn_refresh)
    AppCompatButton bttnRefresh;

    public static GroupListFragment newInstance() {
        return new GroupListFragment();
    }

    public GroupListFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecycler();
        getGroups();
    }

    private void hideProgress() {
        if (progressBar == null) return;
        progressBar.setVisibility(View.GONE);
    }

    private void showErrorMessage(String message) {
        if (!TextUtils.isEmpty(message) && textMessage != null) {
            textMessage.setVisibility(View.VISIBLE);
            textMessage.setText(message);
            showBttnRefresh();
        }
    }

    private void hideTextMessage() {
        if (textMessage == null) return;
        textMessage.setVisibility(View.GONE);
    }

    private void hideBttnRefresh() {
        if (bttnRefresh == null) return;
        bttnRefresh.setVisibility(View.GONE);
    }

    private void showBttnRefresh() {
        if (bttnRefresh == null) return;
        bttnRefresh.setVisibility(View.VISIBLE);
    }

    private void getGroups() {
        Log.d(TAG, "getGroups: ");
        FirebaseGroup firebaseGroup = FirebaseGroup.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


        if (currentUser != null) {
            String phoneNumberFormatted = PhonenumberUtils.formatPhonenumber("PE", currentUser.getPhoneNumber());
            firebaseGroup.getGroups(phoneNumberFormatted, new FirebaseHelper.CompletionListener<List<Grupo>>() {
                @Override
                public void onSuccess(List<Grupo> data) {
                    Log.d(TAG, "getGroups onSuccess: ");
                    hideProgress();

                    if (data.isEmpty()) {
                        addGroupList(data);
                        showErrorMessage("No tienes grupos.");
                        return;
                    }

                    addGroupList(data);
                }

                @Override
                public void onFailure(Exception ex) {
                    Log.d(TAG, "onFailure: " + ex);
                    hideProgress();
                    showErrorMessage(ex.getMessage());
                }
            });
            return;
        }

        Log.d(TAG, "current user is null!!!");
    }

    private GroupAdapter adapter;

    private void setupRecycler() {
        if (rcvGroups == null) return;
        adapter = new GroupAdapter(new ArrayList<Grupo>());
        adapter.setListener(new BaseAdapter.Listener<Grupo>() {
            @Override
            public void onItemSelected(Grupo item) {
                GroupActivity.launchGroupActivity(getActivity(), item.getUid(), item.getName());
            }

            @Override
            public void onItemLongSelected(Grupo item) {
                showDialogToDeleteGroup(item, "Eliminar Grupo", "¿Estás seguro de eliminar el grupo: " + item.getName() + "?");
            }
        });
        rcvGroups.setAdapter(adapter);
        rcvGroups.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public void showDialogToDeleteGroup(final Grupo grupo, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if (!TextUtils.isEmpty(title)) builder.setTitle(title);

        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                deleteGroupFromUser(grupo);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    public void deleteGroupFromUser(Grupo grupo) {
        FirebaseGroup firebaseGroup = FirebaseGroup.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseGroup.deleteGroupFromUser(currentUser.getUid(), grupo.getUid(), new FirebaseHelper.CompletionListener<Grupo>() {
            @Override
            public void onSuccess(Grupo item) {
                removeGroup(item);
                Snackbar.make(rcvGroups, R.string.global_message_success, Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Exception ex) {
                showErrorMessage(ex.getMessage());
            }
        });
    }

    private void addGroup(Grupo group) {
        adapter.addOrUpdate(group);
    }

    private void addGroupList(List<Grupo> groupList) {
        if (adapter == null) return;
        hideTextMessage();
        hideBttnRefresh();
        adapter.addItems(groupList);
    }

    private void removeGroup(Grupo grupo) {
        adapter.removeItem(grupo);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.bttn_refresh, R.id.fab})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bttn_refresh:
                getGroups();
                break;
            case R.id.fab:
                getGroups();
                break;
        }
    }
}
