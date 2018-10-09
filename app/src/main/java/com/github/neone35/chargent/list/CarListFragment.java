package com.github.neone35.chargent.list;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.neone35.chargent.MainActivity;
import com.github.neone35.chargent.R;
import com.github.neone35.chargent.model.Car;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class CarListFragment extends Fragment {

    @BindView(R.id.rv_car_list)
    RecyclerView rvCarList;
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_USER_LAT_LNG = "user-latlng";
    private int mColumnCount = 0;
    private LatLng mUserLatLng = null;
    private Context mCtx;
    private CompositeDisposable mDisps = new CompositeDisposable();
    private MenuItem mSortMenuItem;

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

    @Override
    public void onStart() {
        super.onStart();
        // again, this should be access using some kind of Dependency Injection
        Disposable disp = MainActivity.carVM.getState()
                .subscribe(carsState -> {
                    List<Car> carList = carsState.getCars();
                    switchMenuIcon(carsState.isSortByDistance);
                    carsState.setCarsDistanceFromUser(carList, mUserLatLng);
                    displayCars(carList);
                });
        mDisps.add(disp);
    }

    private void switchMenuIcon(boolean isSortByDistance) {
        if (isSortByDistance) {
            mSortMenuItem.setIcon(R.drawable.ic_distance_24dp);
        } else {
            mSortMenuItem.setIcon(R.drawable.ic_distance_stroke_24dp);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // turn on 'sort' option
        menu.getItem(1).setEnabled(true);
        // assign global menu item to mirror current sort status
        mSortMenuItem = menu.getItem(1).getSubMenu().findItem(R.id.menu_appbar_sort_distance);
        super.onPrepareOptionsMenu(menu);
    }

    private void displayCars(List<Car> cars) {
        // Set the adapter on recylerView
        Context context = Objects.requireNonNull(getView()).getContext();
        if (mColumnCount <= 1) {
            rvCarList.setLayoutManager(new LinearLayoutManager(context));
        } else {
            rvCarList.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        // set or swap (if already set) adapter
        if (rvCarList.getAdapter() == null) {
            rvCarList.setAdapter(new CarListAdapter(cars, mCtx));
        } else {
            rvCarList.swapAdapter(new CarListAdapter(cars, mCtx), true);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        mDisps.clear();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_car_list, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_appbar_sort_distance:
                MainActivity.carVM.setSortingByDistance();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
