package com.consultoraestrategia.messengeracademico.utils;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.importData.jobDispatcher.ImportDataJobService;
import com.consultoraestrategia.messengeracademico.notification.jobService.NewMessageJobService;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

public class JobServiceUtils {

    private static final String TAG = JobServiceUtils.class.getSimpleName();

    public static void scheduleNewMessageJob(Context context, String keyMessage) {
        Log.d(TAG, "scheduleNewMessageJob: " + keyMessage);
        // Create a new dispatcher using the Google Play driver.
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

        Bundle extras = new Bundle();
        extras.putString(NewMessageJobService.EXTRA_KEY_MESSAGE, keyMessage);

        Job myJob = dispatcher.newJobBuilder()
                // the JobService that will be called
                .setService(NewMessageJobService.class)
                // uniquely identifies the job
                .setTag(keyMessage)
                // one-off job
                .setRecurring(false)
                // don't persist past a device reboot
                .setLifetime(Lifetime.FOREVER)
                // start between 0 and 60 seconds from now
                .setTrigger(Trigger.NOW)
                // don't overwrite an existing job with the same tag
                .setReplaceCurrent(false)
                // retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                // constraints that need to be satisfied for the job to run
                .setConstraints(
                        // only run on an unmetered network
                        Constraint.ON_ANY_NETWORK
                )
                .setExtras(extras)
                .build();

        dispatcher.mustSchedule(myJob);
    }

    public static void scheduleImportDataJobService(Context context) {
        Log.d(TAG, "scheduleImportDataJobService: ");
        // Create a new dispatcher using the Google Play driver.
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

        Bundle extras = new Bundle();

        Job myJob = dispatcher.newJobBuilder()
                // the JobService that will be called
                .setService(ImportDataJobService.class)
                // uniquely identifies the job
                .setTag("importDataJobServiceTag")
                // one-off job
                .setRecurring(false)
                .setTrigger(Trigger.NOW)
                // don't overwrite an existing job with the same tag
                .setReplaceCurrent(false)
                // retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                // constraints that need to be satisfied for the job to run
                .setConstraints(
                        // only run on an unmetered network
                        Constraint.ON_ANY_NETWORK
                )
                .setExtras(extras)
                .build();

        dispatcher.mustSchedule(myJob);
    }

}
