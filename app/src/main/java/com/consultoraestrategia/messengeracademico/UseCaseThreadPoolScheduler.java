package com.consultoraestrategia.messengeracademico;

import android.os.Handler;
import android.util.Log;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Executes asynchronous tasks using a {@link ThreadPoolExecutor}.
 * <p>
 * See also {@link Executors} for a list of factory methods to create common
 * {@link java.util.concurrent.ExecutorService}s for different scenarios.
 */

public class UseCaseThreadPoolScheduler implements UseCaseScheduler {

    private static final String TAG = UseCaseThreadPoolScheduler.class.getSimpleName();
    private final Handler mHandler = new Handler();

    public static final int POOL_SIZE = (Runtime.getRuntime().availableProcessors() - 1 > 0) ? Runtime.getRuntime().availableProcessors() - 1 : 1;

    public static final int MAX_POOL_SIZE = 128;

    public static final int TIMEOUT = 120;


    ThreadPoolExecutor mThreadPoolExecutor;

    public UseCaseThreadPoolScheduler() {
        Log.d(TAG, "POOL_SIZE: " + POOL_SIZE);
        mThreadPoolExecutor = new ThreadPoolExecutor(POOL_SIZE, MAX_POOL_SIZE, TIMEOUT,
                TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(POOL_SIZE));
    }

    @Override
    public void execute(Runnable runnable) {
        mThreadPoolExecutor.execute(runnable);
    }

    @Override
    public <V extends UseCase.ResponseValue> void notifyResponse(final V response, final UseCase.UseCaseCallback<V> useCaseCallback) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                useCaseCallback.onSuccess(response);
            }
        });
    }

    @Override
    public <V extends UseCase.ResponseValue> void onError(final UseCase.UseCaseCallback<V> useCaseCallback) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                useCaseCallback.onError();
            }
        });
    }
}
