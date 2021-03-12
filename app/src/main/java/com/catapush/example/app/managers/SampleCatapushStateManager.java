package com.catapush.example.app.managers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.catapush.library.Catapush;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

public class SampleCatapushStateManager {

    public static final SampleCatapushStateManager INSTANCE = new SampleCatapushStateManager();

    private CatapushStarter mStarter;
    private final BehaviorSubject<Status> mCurrentStatus = BehaviorSubject.create();
    private final PublishSubject<Boolean> mReconnectRequest = PublishSubject.create();
    private CompositeDisposable mSubscriptions = new CompositeDisposable();

    private SampleCatapushStateManager() {}

    public void init(@Nullable CatapushStarter starter) {
        this.mStarter = starter;
        if (mSubscriptions.isDisposed()) {
            mSubscriptions = new CompositeDisposable();
        }

        // Execute first connection.
        Disposable connectionExecutor = mCurrentStatus
                .filter(status -> status == Status.NONE)
                .subscribe(initialStatus -> start());
        mSubscriptions.add(connectionExecutor);

        // Decide whether to reconnect or not.
        Disposable reconnectRequester = mCurrentStatus
                .filter(status -> status == Status.DISCONNECTED || status == Status.API_ERROR)
                .subscribe(retryStatus -> mReconnectRequest.onNext(true));
        mSubscriptions.add(reconnectRequester);

        // On reconnection request retry to start Catapush.
        // Delay is customized based on the current status.
        Disposable reconnectionExecutor = mReconnectRequest
                .debounce((Function<Boolean, Observable<Boolean>>) request -> {
                    if (mCurrentStatus.getValue() == Status.API_ERROR) {
                        return Observable.<Boolean>empty().delay(5, TimeUnit.MINUTES);
                    } else {
                        return Observable.<Boolean>empty().delay(30, TimeUnit.SECONDS);
                    }
                })
                .subscribe(timeToRetry -> start());
        mSubscriptions.add(reconnectionExecutor);

        // Set initial status
        if (Catapush.getInstance().isRunning()) {
            processStatus(Status.CONNECTED);
        } else if (Catapush.getInstance().isConnecting()) {
            processStatus(Status.CONNECTING);
        } else {
            processStatus(Status.NONE);
        }
    }

    public void dispose() {
        mStarter = null;
        mSubscriptions.dispose();
        processStatus(Status.NONE);
    }

    private void start() {
        if (mStarter != null) {
            mStarter.startCatapush();
        }
    }

    public void processStatus(@NonNull Status newStatus) {
        Status currentStatus = mCurrentStatus.getValue();
        if (currentStatus == newStatus) {
            return; // Do not process the same status twice
        }
        if (currentStatus == Status.AUTH_FAILED && newStatus == Status.DISCONNECTED) {
            return; // Do not process a disconnect after an auth failed
        }
        mCurrentStatus.onNext(newStatus);
    }

    public interface CatapushStarter {
        void startCatapush();
    }

    public enum Status {
        NONE, CONNECTING, CONNECTED, DISCONNECTED, API_ERROR, AUTH_FAILED
    }

}
