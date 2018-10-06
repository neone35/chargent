package com.github.neone35.chargent.list;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.neone35.chargent.R;
import com.github.neone35.chargent.MainActivity;
import com.github.neone35.chargent.model.Car;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.Collections;
import java.util.List;

public class CarListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_USER_LAT_LNG = "user-latlng";
    private int mColumnCount = 0;
    private LatLng mUserLatLng = null;
    private Context mCtx;
    private CompositeDisposable disps = new CompositeDisposable();

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
        mCtx = this.getActivity();

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

    private float calcCarDistanceFromUser(Car car) {
        double carLat = car.getLocation().getLatitude();
        double carLon = car.getLocation().getLongitude();
        Location carLoc = getLocation(new LatLng(carLat, carLon));
        Location userLoc = getLocation(mUserLatLng);
        return userLoc.distanceTo(carLoc);
    }

    private void sortByDistanceToUser(List<Car> unsortedCars) {
        Collections.sort(unsortedCars, (car1, car2) -> {
            float car1DistToUser = car1.getDistanceFromUser();
            float car2DistToUser = car2.getDistanceFromUser();
            if(car1DistToUser == car2DistToUser)
                return 0;
            return car1DistToUser < car2DistToUser ? -1 : 1;
        });
    }

    private void setCarsDistanceFromUser(List<Car> carList) {
        for (int i = 0; i < carList.size(); i++) {
            Car car = carList.get(i);
            float carDistanceFromUser = calcCarDistanceFromUser(car);
            car.setDistanceFromUser(carDistanceFromUser);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        //again, this should be access using some kind of Dependency Injection

        Disposable disp = MainActivity.carVM.getState().observeOn(AndroidSchedulers.mainThread())
            .subscribe(carsState -> {
                syncMenu(carsState.isSortByDistance);
                displayCars(carsState.getCars(),carsState.isSortByDistance);
            });
        disps.add(disp);
    }

    private void syncMenu(boolean isSortByDistance) {
        //todo invalidate menu, since icon should be different
    }


    //ideally, we should receive already sorted data
    private void displayCars(List<Car> cars, boolean isSortByDistance) {
        //we can use getView.findviewbyid here because this method will only be called between onStart and onStop
        RecyclerView rv = getView().findViewById(R.id.car_list);
        setCarsDistanceFromUser(cars);
        // change sort order
        if (isSortByDistance) {
            sortByDistanceToUser(cars);
        } else {
            Collections.shuffle(cars);
        }
        // Set the adapter on recylerView
        Context context = getView().getContext();
        if (mColumnCount <= 1) {
            rv.setLayoutManager(new LinearLayoutManager(context));
        } else {
            rv.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        // set or swap adapter (if already set)
        if (rv.getAdapter() == null) {
            rv.setAdapter(new CarListAdapter(cars, mCtx));
        } else {
            rv.swapAdapter(new CarListAdapter(cars, mCtx), true);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        disps.dispose();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_car_list, container, false);
        return rootView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // turn on 'sort' option
        menu.getItem(1).setEnabled(true);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_appbar_sort_distance:
                //------all this -------
//                if (!SORT_DISTANCE_ENABLED) {
//                    SORT_DISTANCE_ENABLED = true;
//                    item.setIcon(R.drawable.ic_distance_24dp);
//                } else {
//                    SORT_DISTANCE_ENABLED = false;
//                    item.setIcon(R.drawable.ic_distance_stroke_24dp);
//                }
//                mCarListDisp.dispose();
//                setListAdapterData(this.getView());
                //---- becomes --------
                MainActivity.carVM.sortByDistanceClicked();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
