package com.github.neone35.chargent;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.blankj.utilcode.util.ToastUtils;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.common.ArrayListAccumulator;
import com.github.neone35.chargent.CarInteractorImpl;
import com.github.neone35.chargent.CarVM;
import com.github.neone35.chargent.R;
import com.github.neone35.chargent.list.CarListAdapter;
import com.github.neone35.chargent.list.CarListFragment;
import com.github.neone35.chargent.map.MapFragment;
import com.github.neone35.chargent.model.Car;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.patloew.rxlocation.RxLocation;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.ListFragment;
import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity implements FilterDialogFragment.FilterDialogListener {

    private CarVM carVM;
    private FragmentManager mFragmentManager;
    public static Single<List<Car>> mCachedCarsResponse = null;
    private final String KEY_CURRENT_SUBTITLE = "current-subtitle";
    private MapFragment mMapFragment;
    private CarListFragment mListFragment;
    public static boolean IS_BATTERY_FILTER_ENABLED = false;
    public static boolean IS_PLATE_FILTER_ENABLED = false;
    public static ArrayList<Car> carsList = new ArrayList<>();
    Disposable carsDisp;
    private int MIN_REMAINING_BATTERY;
    private int MAX_REMAINING_BATTERY;
    private final int MIN_PLATE_NUMBER = 0;
    private final int MAX_PLATE_NUMBER = 0;


    @BindView(R.id.bnv_main)
    BottomNavigationView bnvMain;

    @BindString(R.string.app_name)
    String appName;
    @BindString(R.string.nav_map_label)
    String mapTitle;
    @BindString(R.string.nav_list_label)
    String listTitle;
    @BindString(R.string.no_internet)
    String stringNoInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);
        mFragmentManager = getSupportFragmentManager();
        ButterKnife.bind(this);
        setDebugConfig();

        // create cars viewmodel instance
        // interactor subscribesOn, viewmodel observesOn
        carVM = new CarVM(new CarInteractorImpl(), AndroidSchedulers.mainThread());
        if (internetExists()) {
            // cache response to avoid calls on every subscription
            mCachedCarsResponse = carVM.fetch().cache();
//            carsDisp = mCachedCarsResponse.subscribe(cars -> {
//                carsList.addAll(cars);
//                ArrayList<Integer> remainingBatteries = new ArrayList<>();
//                ArrayList<String> plateNumbers = new ArrayList<>();
//                for (int i = 0; i < cars.size(); i++) {
//                    Car car = cars.get(i);
//                    remainingBatteries.add(car.getBatteryPercentage());
//                    plateNumbers.add(car.getPlateNumber());
//                }
//                MIN_REMAINING_BATTERY = Collections.min(remainingBatteries);
//                MAX_REMAINING_BATTERY = Collections.max(remainingBatteries);
//            });
            // listen to bottom navigation clicks
            listenBnv();
            // only create fragment if there was no configuration change
            if (savedInstanceState == null) {
                mMapFragment = MapFragment.newInstance();
                inflateFragment(mMapFragment);
            }
        } else {
            ToastUtils.showShort(stringNoInternet);
        }
    }

    private void inflateFragment(Fragment fragment) {
        Fragment fragToInflate = new Fragment();
        if (fragment instanceof MapFragment) {
            fragToInflate = checkFragmentManager(fragment, mapTitle);
            if (fragToInflate == null) return;
        } else if (fragment instanceof CarListFragment) {
            fragToInflate = checkFragmentManager(fragment, listTitle);
            if (fragToInflate == null) return;
        }
        mFragmentManager.beginTransaction()
                .replace(R.id.frag_main, fragToInflate)
                .commit();
    }

    private Fragment checkFragmentManager(Fragment fragment, String subTitle) {
        if (!mFragmentManager.getFragments().contains(fragment)) {
            setActionBar(appName, subTitle);
            return fragment;
        } else { return null; }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String savedSubtitle = savedInstanceState.getString(KEY_CURRENT_SUBTITLE);
        setActionBar(appName, savedSubtitle);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mFragmentManager.getFragments().contains(mMapFragment)) {
            outState.putString(KEY_CURRENT_SUBTITLE, mapTitle);
        } else if (mFragmentManager.getFragments().contains(mListFragment)) {
            outState.putString(KEY_CURRENT_SUBTITLE, listTitle);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void listenBnv() {
        bnvMain.setOnNavigationItemSelectedListener(menuItem -> {
            int selectedItemId = menuItem.getItemId();
            switch (selectedItemId) {
                case R.id.bnv_action_map:
                    mMapFragment = MapFragment.newInstance();
                    inflateFragment(mMapFragment);
                    break;
                case R.id.bnv_action_list:
                    mListFragment = CarListFragment.newInstance(1, MapFragment.mUserLatLng);
                    inflateFragment(mListFragment);
                    break;
            }
            return true;
        });
    }

    private void setDebugConfig() {
        Stetho.initializeWithDefaults(this);
        Logger.addLogAdapter(new AndroidLogAdapter());
    }

    private void setActionBar(String title, String subTitle) {
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(title);
            ab.setSubtitle(subTitle);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_appbar, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // turn off 'sort' option
        menu.getItem(1).setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_appbar_filter_plate:
                showFilterDialog(R.string.set_plate_number_filter, true, 0, 0);
                return true;
            case R.id.menu_appbar_filter_battery:
                showFilterDialog(R.string.set_battery_filter, true,
                        MIN_REMAINING_BATTERY, MAX_REMAINING_BATTERY);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean internetExists() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }

    public void showFilterDialog(int titleID, boolean isEnabled, int tickStart, int tickEnd) {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = FilterDialogFragment.newInstance(titleID, isEnabled, tickStart, tickEnd);
        dialog.show(mFragmentManager, "FilterDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int titleID, boolean isEnabled) {
        switch (titleID) {
            case R.string.set_battery_filter:
                IS_BATTERY_FILTER_ENABLED = isEnabled;
            case R.string.set_plate_number_filter:
                IS_PLATE_FILTER_ENABLED = isEnabled;
        }
        mMapFragment = MapFragment.newInstance();
        inflateFragment(mMapFragment);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }
}
