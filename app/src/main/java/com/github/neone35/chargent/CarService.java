package com.github.neone35.chargent;

import com.github.neone35.chargent.model.Car;

import java.util.List;

import retrofit2.http.GET;
import rx.Observable;

public interface CarService {

    @GET("api/mobile/public/availablecars")
    Observable<List<Car>> getAvailableCars();
}
