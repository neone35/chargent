package com.github.neone35.chargent.viewmodel;

import com.github.neone35.chargent.CarInteractor;
import com.github.neone35.chargent.model.Car;
import com.google.android.gms.maps.model.LatLng;
import com.orhanobut.logger.Logger;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;

public class CarVM {

    // service actions (overriden in implementation)
    private CarInteractor carInteractor;
    // main thread scheduler
    private Scheduler scheduler;
    // subject can be both Observable & Observer
    // emits most recent and all further items to observer (through getState)
    private BehaviorSubject<CarState> mCarsState = BehaviorSubject.create();
    private CompositeDisposable mDisps = new CompositeDisposable();

    public CarVM(CarInteractor carInteractor, Scheduler scheduler) {
        this.carInteractor = carInteractor;
        this.scheduler = scheduler;
        //set default value
        mCarsState.onNext(new CarState());
        fetch();
    }

    // must be called when activity is destroyed.
    // when using ViewModel from Architecture components, this is done automatically
    public void onCleared() {
        mDisps.dispose();
    }

    // called every time when subject needs to be subscribed to
    public Observable<CarState> getState() {
        // hides the identity (Subject) of this Observable and its disposables (no now)
        // converts BehaviourSubject into Observable (renamed from asObservable() in rx1)
        return mCarsState.hide().observeOn(scheduler);
    }

    // fetched once (MainActivity) & set into CarState subject (Observer & Observable)
    private void fetch() {
        Disposable carsDisposable = carInteractor
                // get Single<List<Car>>
                .fetch()
                // make sure observation is done on right thread (main)
                .observeOn(scheduler)
                // onSuccess, onError shorter
                .subscribe(this::carsReceived, throwable -> Logger.d(throwable.getMessage()));
        mDisps.add(carsDisposable);
    }

    // called after onSuccess in fetch()
    private void carsReceived(List<Car> cars) {
        CarState currentState = mCarsState.getValue();
        currentState.setCars(cars);
        mCarsState.onNext(currentState);
    }

    // Notify VM when UI has to be updated (from menu selection in MainActivity)
    public void setBatteryFilter(int start, int end, boolean isEnabled) {
        CarState currentState = mCarsState.getValue();
        currentState.setBatteryFilterValues(start, end, isEnabled);
        mCarsState.onNext(currentState);
    }

    public void setPlateFilter(int start, int end, boolean isEnabled) {
        CarState currentState = mCarsState.getValue();
        currentState.setPlateFilterValues(start, end, isEnabled);
        mCarsState.onNext(currentState);
    }

    public void setSortingByDistance() {
        // get currently observed object (CarState) values
        CarState currentState = mCarsState.getValue();
        // set new field value
        currentState.isSortByDistance = !currentState.isSortByDistance;
        // create new event which will be received by observer
        mCarsState.onNext(currentState);
    }

}
