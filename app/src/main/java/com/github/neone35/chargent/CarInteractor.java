package com.github.neone35.chargent;

import com.github.neone35.chargent.model.Car;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;

public interface CarInteractor {
    Single<List<Car>> fetch();
}
