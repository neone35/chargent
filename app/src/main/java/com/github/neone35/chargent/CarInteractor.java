package com.github.neone35.chargent;

import com.github.neone35.chargent.model.Car;

import java.util.List;

import rx.Observable;

public interface CarInteractor {

    Observable<List<Car>> fetch();
}
