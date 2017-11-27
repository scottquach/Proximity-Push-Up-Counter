package com.awesome.scottquach.proximitypush_upcounter.features.Statistics;

import android.content.Context;
import android.content.SharedPreferences;

import com.awesome.scottquach.proximitypush_upcounter.BaseApplication;
import com.awesome.scottquach.proximitypush_upcounter.database.SessionEntity;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Scott Quach on 11/24/2017.
 */

public class StatisticsDatabase {

    private Context context;
    private StatisticsDatabaseCallback listener = null;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public StatisticsDatabase(Context context, Object caller) {
        this.context = context;
        if (caller instanceof StatisticsDatabaseCallback) {
            listener = (StatisticsDatabaseCallback) caller;
        }
    }

    public void disposeObservables() {
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    public void requestTimesGoalReached() {
        Single.fromCallable(new Callable<SessionEntity[]>() {
            @Override
            public SessionEntity[] call() throws Exception {
                return BaseApplication.getInstance().database.sessionDOA().queryWhenGoalReached();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<SessionEntity[]>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(SessionEntity[] sessionEntities) {
                        Timber.d("Total times goal reached is " + sessionEntities.length);
                        if (listener != null) listener.timesGoalReached(sessionEntities.length);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    public void requestTimesGoalFailed() {
        Single.fromCallable(new Callable<SessionEntity[]>() {
            @Override
            public SessionEntity[] call() throws Exception {
                return BaseApplication.getInstance().database.sessionDOA().queryWhenGoalFailed();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<SessionEntity[]>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(SessionEntity[] sessionEntities) {
                        Timber.d("Total times goal faild is " + sessionEntities.length);
                        if (listener != null) listener.timesGoalFailed(sessionEntities.length);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    public void requestHighScore() {
        SharedPreferences sharedPref = context.getSharedPreferences("savedPushUpsFile1", MODE_PRIVATE);
        if (listener != null) listener.highScoreLoaded(sharedPref.getInt("highscore", 0));
    }

    public void requestDayHighScore() {
        Single.fromCallable(new Callable<int[]>() {
            @Override
            public int[] call() throws Exception {
                return BaseApplication.getInstance().database.sessionDOA().queryDaySessionTotal();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<int[]>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(int[] sessionEntities) {
                        int max = 0;
                        for (int value : sessionEntities) {
                            if (value > max) max = value;
                        }
                        if (listener != null) listener.dayHighScoreLoaded(max);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    public void requestTotalPushups() {
        Single.fromCallable(new Callable<int[]>() {
            @Override
            public int[] call() throws Exception {
                return BaseApplication.getInstance().database.sessionDOA().querySessionsPushups();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<int[]>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(int[] ints) {
                        int total = 0;
                        for (int value : ints) {
                            total += value;
                        }
                        Timber.d("Total pushups is " + String.valueOf(total));
                        if (listener != null) {
                            listener.totalPushupsLoaded(total);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    interface StatisticsDatabaseCallback {
        void totalPushupsLoaded(int total);
        void highScoreLoaded(int highscore);
        void dayHighScoreLoaded(int highscore);
        void timesGoalReached(int num);
        void timesGoalFailed(int num);
    }
}
