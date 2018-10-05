package com.github.neone35.chargent.viewmodel;

import com.github.neone35.chargent.CarInteractor;
import com.github.neone35.chargent.model.Car;

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

  //create subject
  private BehaviorSubject<CarsState> mCarsState = BehaviorSubject.create();

  private CompositeDisposable mDisposable = new CompositeDisposable();

  public CarVM(CarInteractor carInteractor, Scheduler scheduler) {
    this.carInteractor = carInteractor;
    this.scheduler = scheduler;
    //set default value
    mCarsState.onNext(new CarsState());
    fetch();
  }

  //must be called when activity is destroyed. When using ViewModel from Architecture components, this is done automatically
  public void onCleared() {
    mDisposable.dispose();
  }


  //Notify vm when something happened to ui
  public void setBatteryFilter(int start, int end) {
    CarsState currentState = mCarsState.getValue();
    currentState.setBatteryFilterValues(start, end);
    mCarsState.onNext(currentState);
  }


  // subscribed to & observed by activity/fragment
  private void fetch() {
    // make sure observation is done on right thread (main)
    //todo handle error in some way
    Disposable carsDisposable = carInteractor
        .fetch()
        .observeOn(scheduler)
        .subscribe(this::carsReceived, Throwable::printStackTrace);
    mDisposable.add(carsDisposable);
  }

  private void carsReceived(List<Car> cars) {
    CarsState currentState = mCarsState.getValue();
    currentState.setCars(cars);
    mCarsState.onNext(currentState);
  }

  public Observable<CarsState> getState() {
    return mCarsState.hide();
  }

  public void sortByDistanceClicked() {
    CarsState currentState = mCarsState.getValue();
    currentState.isSortByDistance = !currentState.isSortByDistance;
    mCarsState.onNext(currentState);
  }

}
