package com.szlangpai.mypaintdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private myCircleView mCircleView;
    private int mCount = 0;
    private boolean mPlay = false;
    private Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCircleView = (myCircleView) findViewById(R.id.circleView);

        mCircleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mPlay) {
                    mPlay = true;
                    mCircleView.setBackGroundColor(getResources().getColor(R.color.red));
                    mCircleView.setProgressTextDefaultColor(getResources().getColor(R.color.white));
                    mSubscription = Observable.interval(1000, TimeUnit.MILLISECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<Long>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onNext(Long aLong) {
                                    Log.i(TAG, "onNext: " + mCount);
                                    if (mCount <= 60) {
                                        mCircleView.setProgress(mCount);
                                        mCount++;
                                    } else {
                                        mCount = 1;
                                        mCircleView.onReset();
                                    }
                                }
                            });
                } else {
                    mPlay = false;
                    if (mSubscription != null && !mSubscription.isUnsubscribed()) {
                        mSubscription.unsubscribe();
                        mSubscription = null;
                    }
                    mCircleView.resetDraw();
                    mCount = 0;
                }
            }
        });
    }
}
