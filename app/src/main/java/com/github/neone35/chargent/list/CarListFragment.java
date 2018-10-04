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
    private Disposable mCarListDisp;
    private boolean SORT_DISTANCE_ENABLED = false;
    private Context mCtx;

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

    private void setListAdapterData(View view) {
        mCarListDisp = MainActivity.mCachedCarsResponse.subscribe(cars -> {
            setCarsDistanceFromUser(cars);
            // change sort order
            if (SORT_DISTANCE_ENABLED) {
                sortByDistanceToUser(cars);
            } else {
                Collections.shuffle(cars);
            }
            // Set the adapter on recylerView
            if (view instanceof RecyclerView) {
                Context context = view.getContext();
                RecyclerView rv = (RecyclerView) view;
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
        mCarListDisp.dispose();
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
                if (!SORT_DISTANCE_ENABLED) {
                    SORT_DISTANCE_ENABLED = true;
                    item.setIcon(R.drawable.ic_distance_24dp);
                } else {
                    SORT_DISTANCE_ENABLED = false;
                    item.setIcon(R.drawable.ic_distance_stroke_24dp);
                }
                mCarListDisp.dispose();
                setListAdapterData(this.getView());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
