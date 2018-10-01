package com.github.neone35.chargent;

import com.github.neone35.chargent.model.Car;

import java.util.List;

import rx.Observable;
import rx.Scheduler;

class CarVM {

    // overrriden actions of service
    private CarInteractor carInteractor;
    // main thread scheduler
    private Scheduler scheduler;

    CarVM(CarInteractor carInteractor, Scheduler scheduler) {
        this.carInteractor = carInteractor;
        this.scheduler = scheduler;
    }

    Observable<List<Car>> fetch() {
        return carInteractor.fetch().observeOn(scheduler);
    }
}
