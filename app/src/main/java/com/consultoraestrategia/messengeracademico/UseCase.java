package com.consultoraestrategia.messengeracademico;

/**
 * Use cases are the entry points to the domain layer.
 *
 * @param <Q> the request type
 * @param <P> the response type
 */
public abstract class UseCase<Q extends UseCase.RequestValues, P extends UseCase.ResponseValue> {

    private Q requestValues;

    private UseCaseCallback<P> useCaseCallback;

    public Q getRequestValues() {
        return requestValues;
    }

    public void setRequestValues(Q requestValues) {
        this.requestValues = requestValues;
    }

    public UseCaseCallback<P> getUseCaseCallback() {
        return useCaseCallback;
    }

    public void setUseCaseCallback(UseCaseCallback<P> useCaseCallback) {
        this.useCaseCallback = useCaseCallback;
    }

    void run() {
        executeUseCase(requestValues);
    }

    protected abstract void executeUseCase(Q requestValues);

    /**
     * Data passed to a request.
     */
    public interface RequestValues {
    }

    /**
     * Data received from a request.
     */
    public interface ResponseValue {
    }

    public interface UseCaseCallback<R> {
        void onSuccess(R response);
        void onError();
    }
}
