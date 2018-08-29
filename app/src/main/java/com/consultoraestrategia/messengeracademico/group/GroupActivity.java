package com.consultoraestrategia.messengeracademico.group;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.UseCaseHandler;
import com.consultoraestrategia.messengeracademico.UseCaseThreadPoolScheduler;
import com.consultoraestrategia.messengeracademico.base.BaseActivity;
import com.consultoraestrategia.messengeracademico.base.BaseView;
import com.consultoraestrategia.messengeracademico.base.adapter.BaseAdapter;
import com.consultoraestrategia.messengeracademico.chat.ui.ChatActivity;
import com.consultoraestrategia.messengeracademico.group.adapter.IntegranteAdapter;
import com.consultoraestrategia.messengeracademico.importGroups.entities.ui.CrmeUser;
import com.consultoraestrategia.messengeracademico.lib.GreenRobotEventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupActivity extends BaseActivity<GroupView, GroupPresenter> implements GroupView {

    public static final String TAG = "GroupActivity";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.txt_member_counter)
    AppCompatTextView txtMemberCounter;
    @BindView(R.id.rcv_members)
    RecyclerView rcvMembers;
    @BindView(R.id.progress_bar)
    ProgressBar progress;
    SearchView searchView;


    public static final String EXTRA_GROUP_ID = "EXTRA_GROUP_ID";
    public static final String EXTRA_GROUP_NAME = "EXTRA_GROUP_NAME";

    public static void launchGroupActivity(Context context, String groupId, String groupName) {
        Intent intent = new Intent(context, GroupActivity.class);
        //intent.putExtra(EXTRA_GROUP_ID, groupId);
        //intent.putExtra(EXTRA_GROUP_NAME, groupName);
        Bundle args = new Bundle();
        args.putString(EXTRA_GROUP_ID, groupId);
        args.putString(EXTRA_GROUP_NAME, groupName);
        intent.putExtras(args);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    protected AppCompatActivity getActivity() {
        return this;
    }

    @Override
    protected GroupPresenter getPresenter() {
        return new GroupPresenterImpl(
                new UseCaseHandler(new UseCaseThreadPoolScheduler()),
                getResources(),
                GreenRobotEventBus.getInstance()
        );
    }

    @Override
    protected GroupView getBaseView() {
        return this;
    }

    @Override
    protected Bundle getExtrasInf() {
        return getIntent().getExtras();
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_group);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
    }

    @Override
    protected ViewGroup getRootLayout() {
        return rcvMembers;
    }

    @Override
    protected ProgressBar getProgressBar() {
        return progress;
    }

    @Override
    public void showGroupName(String groupName) {
        if (!TextUtils.isEmpty(groupName))
            setTitle(groupName);
    }

    @Override
    public void showMemberCount(int count) {
        String formattedCount = String.format(getString(R.string.group_mssg_count_members), count);
        txtMemberCounter.setText(formattedCount);
    }

    IntegranteAdapter adapter;

    @Override
    public void showIntegrantes(List<CrmeUser> crmeUsers) {
        adapter = new IntegranteAdapter(new ArrayList<CrmeUser>());
        adapter.setListener(new BaseAdapter.Listener<CrmeUser>() {
            @Override
            public void onItemSelected(CrmeUser item) {
                presenter.onCrmeUserSelected(item);
            }

            @Override
            public void onItemLongSelected(CrmeUser item) {
                presenter.onCrmeUserLongClicked(item);
            }
        });

        rcvMembers.setAdapter(adapter);
        rcvMembers.setLayoutManager(new LinearLayoutManager(this));
        adapter.setItems(crmeUsers);
    }

    @Override
    public void launchChat(CrmeUser item) {
        Log.d(TAG, "launchChat: ");
        ChatActivity.startChatActivity(this, item.getPhoneNumber());
    }

    @Override
    public void onTextSubmit(String text, int requestCode) {
        presenter.onPhoneNumberSubmitted(text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_group, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                adapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_search:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }
}
