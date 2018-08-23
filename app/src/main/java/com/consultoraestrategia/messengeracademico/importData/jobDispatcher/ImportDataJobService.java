package com.consultoraestrategia.messengeracademico.importData.jobDispatcher;

import android.util.Log;

import com.consultoraestrategia.messengeracademico.importData.ImportDataInteractor;
import com.consultoraestrategia.messengeracademico.importData.ImportDataInteractorImpl;
import com.consultoraestrategia.messengeracademico.importData.events.ImportDataEvent;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.lib.GreenRobotEventBus;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import org.greenrobot.eventbus.Subscribe;

public class ImportDataJobService extends JobService {

    public static final String TAG = ImportDataJobService.class.getSimpleName();
    private ImportDataInteractor interactor;
    private JobParameters job;
    private EventBus eventBus;

    @Override
    public boolean onStartJob(JobParameters job) {
        Log.d(TAG, "onStartJob: " + job);
        this.job = job;
        eventBus = GreenRobotEventBus.getInstance();
        eventBus.register(this);
        interactor = new ImportDataInteractorImpl(this);
        interactor.executeImportData();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        Log.d(TAG, "onStopJob: " + job);
        if (eventBus != null) {
            eventBus.unregister(this);
        }
        if (interactor != null) {
            interactor = null;
        }
        return false;
    }

    @Subscribe
    public void onEventMainThread(ImportDataEvent event) {
        switch (event.getType()) {
            case ImportDataEvent.OnImportFinish:
                finishImport();
                break;
            case ImportDataEvent.OnImportError:
                break;
        }
    }

    private void finishImport() {
        jobFinished(job, false);
    }


}
