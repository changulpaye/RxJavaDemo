package com.parm.rxjavademo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAINLOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*************************************
         *   Below code work on single thread.
         * ************************************************/

        Observable<String> stringObservable = Observable.just("Hello"); // Emits Hello

        Observer<String> stringObserver = new Observer<String>() {
            @Override
            public void onCompleted() {
                Log.d(TAG,"Rx Oncomplete : No more data to emit.");

            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "Rx onError : An error has been encountered" +
                        " while trying to emit data.");
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG,"Rx String : " + s);
            }
        };

        /** Assign observer to observable following subsribe method help to make bond between
         * this observer and observable.
         * As soon as observer add to observable it emits data.
         **/
        Subscription subscription1 = stringObservable.subscribe(stringObserver);

        /**
         * Alternate for Observer class to reduce unnecesory methods onComplete and onNext()
         * Call call method when observable emits data.
         */
        Subscription subscription2 =  stringObservable.subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.d(TAG, "Rx Result Anonymous Action class : " + s);
            }
        });

        /*************************************************************************************/
                                      /* operators */
        /*************************************************************************************/
        /**
         * from() operator emits one item at a time from the array or list of objects.
         */
        Observable<String> myArrayStringObservable1 =   Observable.from(new String[]{"Hello", "Gracious",
                "Parmeshwar", "Good bye", "Salam"});

        myArrayStringObservable1.subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.d(TAG,"From operator : " + s);
            }
        });

        Observable<Integer> integerObservable = Observable.from(new Integer[]{1,2,3,4,5,6});

        // Modify data from the observable - without lambda.
        integerObservable.map(new Func1<Integer, Integer>() {
            @Override
            public Integer call(Integer integer) {
                return integer*integer;
            }
        })
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer % 2 == 0;
                    }
                }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.d(TAG, "Integer : " + integer);
            }
        });

        /*************************************************************************************/
                                      /* Asynchronous Jobs */
        /*************************************************************************************/
        // subscribeOn - Background Thread.
        // observeOn - MainThread.
        // It needs to create custom observable.

        Observable <String> fetchFromGoogle = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    Log.d(TAG, "Fetching data from server...");
                    String data = fetchData("https://www.google.com");
                    subscriber.onNext(data);
                    subscriber.onCompleted();
                }catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });

        fetchFromGoogle.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d(TAG, "Data Available + " + s);
                        Toast.makeText(MainActivity.this, "Length : " + s, Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private String fetchData(String url) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
            String inputLine;
            String result = "";
            while ((inputLine = bufferedReader.readLine()) != null) {
                result += inputLine;
            }
            return result.length() + "";

        } catch (IOException e) {
            e.printStackTrace();
        }
       return "";
    }

    private void usingOperator() {



    }
}
