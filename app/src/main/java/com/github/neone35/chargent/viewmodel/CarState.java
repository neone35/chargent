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
    public boolean isSortByDistance = false;
    public boolean mBatteryFilterEnabled = false;
    public int mBatteryFilterStart = 0;
    public int mBatteryFilterEnd = 100;
    public int mBatteryMin = 0;
    public int mBatteryMax = 0;
    public boolean mPlateFilterEnabled = false;
    public int mPlateFilterStart = 0;
    public int mPlateFilterEnd = 100;
    public int mPlateMin = 0;
    public int mPlateMax = 0;
    private LatLng mUserLatLng = null;

    void setBatteryFilterValues(int start, int end, boolean isEnabled) {
        mBatteryFilterStart = start;
        mBatteryFilterEnd = end;
        mBatteryFilterEnabled = isEnabled;
    }
    void setPlateFilterValues(int start, int end, boolean isEnabled) {
        mPlateFilterStart = start;
        mPlateFilterEnd = end;
        mPlateFilterEnabled = isEnabled;
    }

    void setCars(List<Car> cars) {
        mCars = cars;

        ArrayList<Integer> remainingBatteries = new ArrayList<>();
        ArrayList<Integer> plateNumbers = new ArrayList<>();
        for (int i = 0; i < cars.size(); i++) {
            remainingBatteries.add(cars.get(i).getBatteryPercentage());
            plateNumbers.add(Integer.parseInt(
                    cars.get(i).getPlateNumber()
                            .replaceAll("[^0-9]", "")));
        }
        // set min/max values of filters as soon as cars are received
        mBatteryMin = Collections.min(remainingBatteries);
        mBatteryMax = Collections.max(remainingBatteries);
        mPlateMin = Collections.min(plateNumbers);
        mPlateMax = Collections.max(plateNumbers);

    }

    // called from Fragments to get filtered cars object from subject
    public List<Car> getCars() {
        // this would be one line in Kotlin
        List<Car> filteredAndOrSorted = new ArrayList<>();
        for (int i = 0; i < mCars.size(); i++) {
            Car car = mCars.get(i);
            int remainingBattery = car.getBatteryPercentage();
            int plateNumber = Integer.parseInt(car.getPlateNumber()
                    .replaceAll("[^0-9]", ""));
            if (mBatteryFilterEnabled && mPlateFilterEnabled) {
                if (remainingBattery > mBatteryFilterStart && remainingBattery < mBatteryFilterEnd &&
                        plateNumber > mPlateFilterStart && plateNumber < mPlateFilterEnd) {
                    filteredAndOrSorted.add(car);
                }
            } else if (mBatteryFilterEnabled && !mPlateFilterEnabled) {
                if (remainingBattery > mBatteryFilterStart && remainingBattery < mBatteryFilterEnd) {
                    filteredAndOrSorted.add(car);
                }
            } else if (!mBatteryFilterEnabled && mPlateFilterEnabled) {
                if (plateNumber > mPlateFilterStart && plateNumber < mPlateFilterEnd) {
                    filteredAndOrSorted.add(car);
                }
            } else if (!mBatteryFilterEnabled && !mPlateFilterEnabled){
                // no filters applied
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
