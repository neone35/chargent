package com.github.neone35.chargent.viewmodel;

import com.github.neone35.chargent.model.Car;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


//this would be so much nicer in Kotlin
public class CarsState {
  //init with default values
  private List<Car> mCars = new ArrayList<>();
  private int mBatteryFilterStart = 0;
  private int mBatteryFilterEnd = 100;
  public boolean isSortByDistance = false;
  public int batteryMin = 0;
  public int batteryMax = 0;

  public void setBatteryFilterValues(int start, int end) {
    mBatteryFilterStart = start;
    mBatteryFilterEnd = end;
  }

  public void setCars(List<Car> cars) {
    mCars = cars;


    ArrayList<Integer> remainingBatteries = new ArrayList<>();
    for (int i = 0; i < cars.size(); i++) {
      remainingBatteries.add(cars.get(i).getBatteryPercentage());
    }
    // set min and max values of current car batteries
    batteryMin = Collections.min(remainingBatteries);
    batteryMax = Collections.max(remainingBatteries);

  }

  public List<Car> getCars() {
    //this would be one line in Kotlin
    List<Car> filtered = new ArrayList<>();
    for (int i = 0; i < mCars.size(); i++) {
      Car car = mCars.get(i);
      int remainingBattery = car.getBatteryPercentage();
      if (remainingBattery > mBatteryFilterStart && remainingBattery < mBatteryFilterEnd) {
        filtered.add(car);
      }
    }
    return filtered;
  }
}
