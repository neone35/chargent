package com.github.neone35.chargent.list;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.Disposable;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.neone35.chargent.R;
import com.github.neone35.chargent.map.MainMapActivity;
import com.github.neone35.chargent.model.Car;
import android.location.Location;
import com.google.android.gms.maps.model.LatLng;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CarListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_USER_LAT_LNG = "user-latlng";
    private int mColumnCount = 0;
    private LatLng mUserLatLng = null;
    private Disposable carListDisp;
    private boolean SORT_DISTANCE_ENABLED = false;
    private RecyclerView mCarListRV;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CarListFragment() {
    }

    public static CarListFragment newInstance(int columnCount, LatLng userLatLng) {
        CarListFragment fragment = new CarListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putParcelable(ARG_USER_LAT_LNG, userLatLng);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mUserLatLng = getArguments().getParcelable(ARG_USER_LAT_LNG);
        }
    }

    private Location getLocation(LatLng latLng) {
        Location userLoc = new Location("");
        userLoc.setLatitude(latLng.latitude);
        userLoc.setLongitude(latLng.longitude);
        return userLoc;
    }

    private float getCarDistanceFromUser (Car car) {
        double carLat = car.getLocation().getLatitude();
        double carLon = car.getLocation().getLongitude();
        Location carLoc = getLocation(new LatLng(carLat, carLon));
        Location userLoc = getLocation(mUserLatLng);
        return userLoc.distanceTo(carLoc);
    }

    private void sortByDistanceToUser(List<Car> unsortedCars) {
        Collections.sort(unsortedCars, (car1, car2) -> {
            float car1DistToUser = getCarDistanceFromUser(car1);
            float car2DistToUser = getCarDistanceFromUser(car2);
            if(car1DistToUser == car2DistToUser)
                return 0;
            return car1DistToUser < car2DistToUser ? -1 : 1;
        });
    }

    private void setListAdapterData(View view) {
        carListDisp = MainMapActivity.mCachedCarsResponse.subscribe(cars -> {
            ArrayList<Car> carList = new ArrayList<>(cars);
            if (SORT_DISTANCE_ENABLED) {
                sortByDistanceToUser(carList);
            }
            // Set the adapter
            if (view instanceof RecyclerView) {
                Context context = view.getContext();
                mCarListRV = (RecyclerView) view;
                if (mColumnCount <= 1) {
                    mCarListRV.setLayoutManager(new LinearLayoutManager(context));
                } else {
                    mCarListRV.setLayoutManager(new GridLayoutManager(context, mColumnCount));
                }
                mCarListRV.setAdapter(new CarListAdapter(carList, this.getActivity()));
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_car_list, container, false);
        setListAdapterData(rootView);
        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        carListDisp.dispose();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.getItem(1).setEnabled(true);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_appbar_sort_distance:
                SORT_DISTANCE_ENABLED = true;
                carListDisp.dispose();
                setListAdapterData(this.getView());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
