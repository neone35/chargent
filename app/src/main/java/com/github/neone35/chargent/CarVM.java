package com.github.neone35.chargent;

import com.github.neone35.chargent.model.Car;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;

public class CarVM {

    // service actions (overriden in implementation)
    private CarInteractor carInteractor;
    // main thread scheduler
    private Scheduler scheduler;

    public CarVM(CarInteractor carInteractor, Scheduler scheduler) {
        this.carInteractor = carInteractor;
        this.scheduler = scheduler;
    }

    // subscribed to & observed by activity/fragment
    public Single<List<Car>> fetch() {
        // make sure observation is done on right thread (main)
        return carInteractor.fetch().observeOn(scheduler);
    }
}
