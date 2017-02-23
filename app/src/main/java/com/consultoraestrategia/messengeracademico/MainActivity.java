package com.consultoraestrategia.messengeracademico;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.consultoraestrategia.messengeracademico.loadProfile.LoadProfileActivity;
import com.consultoraestrategia.messengeracademico.verification.ui.VerificationActivity;

public class MainActivity extends AppCompatActivity {
    public static final String PREF_STEP = "PREF_STEP";
    public static final String PREF_STEP_COMPLETED = "PREF_STEP_COMPLETED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        forwardToStep();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    public void forwardToStep(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String step = preferences.getString(PREF_STEP, VerificationActivity.PREF_STEP_VERIFICATION);
        Class stepClass = null;
        boolean completed = false;
        switch (step){
            case VerificationActivity.PREF_STEP_VERIFICATION:
                stepClass = VerificationActivity.class;
                break;
            case LoadProfileActivity.PREF_STEP_LOAD_PROFILE:
                stepClass = LoadProfileActivity.class;
                break;
            case PREF_STEP_COMPLETED:
                completed = true;
                break;
        }

        if (!completed){
            forwardToClass(stepClass);
        }
    }


    public void forwardToClass(Class clase) {
        Intent intent = new Intent(this, clase);
        intent
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
