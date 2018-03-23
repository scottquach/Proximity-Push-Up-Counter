package com.awesome.scottquach.proximitypush_upcounter.features.Statistics;

import android.content.Context;
import android.content.SharedPreferences;

import com.awesome.scottquach.proximitypush_upcounter.BaseApplication;
import com.awesome.scottquach.proximitypush_upcounter.database.SessionEntity;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
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

    public void requestGraphData() {
        Single.fromCallable(new Callable<SessionEntity[]>() {
            @Override
            public SessionEntity[] call() throws Exception {
                return BaseApplication.getInstance().database.sessionDOA().querySessions();
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
                        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

                        int x = 1;
                        for (SessionEntity entity : sessionEntities) {
                            try {
                                Date date = df.parse(entity.date);
                                DataPoint point = new DataPoint(x, entity.numberOfPushups);
                                series.appendData(point, true, 200, true);
                                x++;
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }
                        if (listener != null) listener.graphDataLoaded(series);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Error loading session entities");
                    }
                });
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
                return BaseApplication.getInstance().database.sessionDOA().queryDaySessionTotals();
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

    public void requestTotalDayPushups() {
        Single.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                String formattedDate = df.format(c.getTime());
                return BaseApplication.getInstance().database.sessionDOA().queryTodaySessionTotal(formattedDate);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Integer i) {
                        if (listener != null) listener.totalDayPushupsLoaded(i);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Error loading day total");
                    }
                });
    }

    public interface StatisticsDatabaseCallback {
        void totalPushupsLoaded(int total);
        void highScoreLoaded(int highscore);
        void dayHighScoreLoaded(int highscore);
        void timesGoalReached(int num);
        void timesGoalFailed(int num);
        void totalDayPushupsLoaded(int total);
        void graphDataLoaded(LineGraphSeries<DataPoint> series);
    }
}
