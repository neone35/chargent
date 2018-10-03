package com.github.neone35.chargent;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.github.neone35.chargent.model.Car;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class CarInteractorImpl implements CarInteractor {

    private CarService carService;

    public CarInteractorImpl() {
        OkHttpClient okClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
        String SPARK_BASE_URL = "https://development.espark.lt";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SPARK_BASE_URL)
                // use Gson
                .addConverterFactory(GsonConverterFactory.create())
                // use RX
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okClient)
                .build();
        carService = retrofit.create(CarService.class);
    }


    @Override
    public Single<List<Car>> fetch() {
        // subscribe to receive next events
        // perform network call on separate (IO) thread
        return carService.getAvailableCars().subscribeOn(Schedulers.io());
    }
}
