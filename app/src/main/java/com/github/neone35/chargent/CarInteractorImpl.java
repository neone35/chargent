package com.github.neone35.chargent;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.github.neone35.chargent.model.Car;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.schedulers.Schedulers;

public class CarInteractorImpl implements CarInteractor {

    private CarService carService;

    CarInteractorImpl() {
        OkHttpClient okClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
        String SPARK_BASE_URL = "https://development.espark.lt";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SPARK_BASE_URL)
                // use Gson
                .addConverterFactory(GsonConverterFactory.create())
                // use RX
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okClient)
                .build();
        carService = retrofit.create(CarService.class);
    }


    @Override
    public Observable<List<Car>> fetch() {
        // subscribe to receive next events
        // perform network call on separate (IO) thread
        return carService.getAvailableCars().subscribeOn(Schedulers.io());
    }
}
