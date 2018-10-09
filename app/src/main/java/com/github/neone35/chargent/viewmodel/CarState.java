package com.github.neone35.chargent.viewmodel;

import android.location.Location;

import com.github.neone35.chargent.model.Car;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//this would be so much nicer in Kotlin
public class CarState {
    //init with default values
    private List<Car> mCars = new ArrayList<>();
    private int mBatteryFilterStart = 0;
    private int mBatteryFilterEnd = 100;
    public boolean isSortByDistance = false;
    public int mBatteryMin = 0;
    public int mBatteryMax = 0;
    private LatLng mUserLatLng = null;

    void setBatteryFilterValues(int start, int end) {
        mBatteryFilterStart = start;
        mBatteryFilterEnd = end;
    }

    void setCars(List<Car> cars) {
        mCars = cars;

        ArrayList<Integer> remainingBatteries = new ArrayList<>();
        for (int i = 0; i < cars.size(); i++) {
            remainingBatteries.add(cars.get(i).getBatteryPercentage());
        }
        // set min and max values of current car batteries
        mBatteryMin = Collections.min(remainingBatteries);
        mBatteryMax = Collections.max(remainingBatteries);

    }

    public List<Car> getCars() {
        //this would be one line in Kotlin
        List<Car> filteredAndOrSorted = new ArrayList<>();
        for (int i = 0; i < mCars.size(); i++) {
            Car car = mCars.get(i);
            int remainingBattery = car.getBatteryPercentage();
            if (remainingBattery > mBatteryFilterStart && remainingBattery < mBatteryFilterEnd) {
                filteredAndOrSorted.add(car);
            }
        }
        if (isSortByDistance) {
            sortByDistanceToUser(filteredAndOrSorted);
        } else {
            Collections.shuffle(filteredAndOrSorted);
        }
        return filteredAndOrSorted;
    }

    private void sortByDistanceToUser(List<Car> unsortedCars) {
        Collections.sort(unsortedCars, (car1, car2) -> {
            float car1DistToUser = car1.getDistanceFromUser();
            float car2DistToUser = car2.getDistanceFromUser();
            if (car1DistToUser == car2DistToUser)
                return 0;
            return car1DistToUser < car2DistToUser ? -1 : 1;
        });
    }

    private Location getLocation(LatLng latLng) {
        Location userLoc = new Location("");
        userLoc.setLatitude(latLng.latitude);
        userLoc.setLongitude(latLng.longitude);
        return userLoc;
    }

    private float calcCarDistanceFromUser(Car car) {
        double carLat = car.getLocation().getLatitude();
        double carLon = car.getLocation().getLongitude();
        Location carLoc = getLocation(new LatLng(carLat, carLon));
        Location userLoc = getLocation(mUserLatLng);
        return userLoc.distanceTo(carLoc);
    }

    public void setCarsDistanceFromUser(List<Car> carList, LatLng userLatLng) {
        mUserLatLng = userLatLng;
        for (int i = 0; i < carList.size(); i++) {
            Car car = carList.get(i);
            // only perform Car property assigment if hasn't been set
            if (car.getDistanceFromUser() == 0) {
                float carDistanceFromUser = calcCarDistanceFromUser(car);
                car.setDistanceFromUser(carDistanceFromUser);
            }
        }
    }

}
