package com.consultoraestrategia.messengeracademico.test;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.UseCaseHandler;
import com.consultoraestrategia.messengeracademico.UseCaseThreadPoolScheduler;
import com.consultoraestrategia.messengeracademico.entities.User;
import com.consultoraestrategia.messengeracademico.test.firebase.UsersViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestActivity extends AppCompatActivity {

    private static final String TAG = "MyTestActivity";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.text)
    TextView text;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        UsersViewModel viewModel = ViewModelProviders.of(
                this,
                new ViewModelFactory(new UseCaseHandler(new UseCaseThreadPoolScheduler()))).get(UsersViewModel.class);

        LiveData<User> liveData = viewModel.getDataSnapshotLiveData();
        liveData.observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user == null || user.getDisplayName() == null) return;
                text.append("\n" + user.getDisplayName());
            }
        });
    }

}
