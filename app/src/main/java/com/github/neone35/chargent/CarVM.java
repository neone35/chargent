package com.github.neone35.chargent;

import com.github.neone35.chargent.model.Car;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;

class CarVM {

    // service actions (overriden in implementation)
    private CarInteractor carInteractor;
    // main thread scheduler
    private Scheduler scheduler;
    int BATTERY_FILTER_START = 0;
    int BATTERY_FILTER_END = 100;
    int PLATE_FILTER_START;
    int PLATE_FILTER_END;

    CarVM(CarInteractor carInteractor, Scheduler scheduler) {
        this.carInteractor = carInteractor;
        this.scheduler = scheduler;
    }

    // subscribed to & observed by activity/fragment
    Single<List<Car>> fetch() {
        // make sure observation is done on right thread (main)
        return carInteractor
                .fetch()
                .toObservable()
                // convert List<Car> into Car
                .flatMapIterable(cars -> cars)
                // set batteries filter
                .filter(car -> car.getBatteryPercentage() > BATTERY_FILTER_START
                        && car.getBatteryPercentage() < BATTERY_FILTER_END)
                // convert Car into List<Car>
                .toSortedList()
                .observeOn(scheduler);
    }
}
