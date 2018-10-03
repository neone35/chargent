package com.github.neone35.chargent;

import com.github.neone35.chargent.model.Car;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.GET;

public interface CarService {

    @GET("api/mobile/public/availablecars")
    Single<List<Car>> getAvailableCars();
}
