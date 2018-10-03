package com.github.neone35.chargent.map;

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
import com.github.neone35.chargent.CarInteractorImpl;
import com.github.neone35.chargent.CarVM;
import com.github.neone35.chargent.R;
import com.github.neone35.chargent.list.CarListAdapter;
import com.github.neone35.chargent.list.CarListFragment;
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
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import rx.subscriptions.CompositeSubscription;

public class MainMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    // keep all disposables in one variable to easily unsubscribe
    private CompositeDisposable disps = new CompositeDisposable();
    private CarVM carVM;
    private RxLocation rxLocation;
    private RxPermissions rxPermissions;
    private LatLng mUserLatLng;
    private FragmentManager mFragmentManager;
    private SupportMapFragment mMapFragment;
    private CarListFragment mListFragment;
    public static Single<List<Car>> mCachedCarsResponse = null;
    private final String KEY_CURRENT_SUBTITLE = "current-subtitle";

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
        rxLocation = new RxLocation(this);
        rxPermissions = new RxPermissions(this);
        mFragmentManager = getSupportFragmentManager();
        ButterKnife.bind(this);
        setDebugConfig();

        // create cars viewmodel instance
        // interactor subscribesOn, viewmodel observesOn
        carVM = new CarVM(new CarInteractorImpl(), AndroidSchedulers.mainThread());
        // cache fetched response
        mCachedCarsResponse = carVM.fetch().cache();

        // only create fragment if there was no configuration change
        if (savedInstanceState == null) {
            inflateMapFragment();
        }

        listenBnv();
    }

    private void inflateMapFragment() {
        setActionBar(appName, mapTitle);
        if (!mFragmentManager.getFragments().contains(mMapFragment)) {
            // obtain the SupportMapFragment
            mMapFragment = new SupportMapFragment();
            mFragmentManager.beginTransaction()
                    .replace(R.id.frag_main, mMapFragment)
                    .commit();
            // get notified when the map is ready to be used.
            mMapFragment.getMapAsync(this);
            mMapFragment.setRetainInstance(true);
        }
    }

    private void inflateListFragment() {
        setActionBar(appName, listTitle);
        if (!mFragmentManager.getFragments().contains(mListFragment)) {
            mListFragment = CarListFragment.newInstance(1, mUserLatLng);
            mFragmentManager.beginTransaction()
                    .replace(R.id.frag_main, mListFragment)
                    .commit();
        } else {
            ToastUtils.showShort("No cars to add into list");
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String savedSubtitle = savedInstanceState.getString(KEY_CURRENT_SUBTITLE);
        setActionBar(appName, savedSubtitle);
    }

    @Override
    protected void onDestroy() {
        disps.clear();
        super.onDestroy();
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
                    inflateMapFragment();
                    break;
                case R.id.bnv_action_list:
                    inflateListFragment();
                    break;
            }
            return true;
        });
    }

    private void addCarMarkers(List<Car> cars) {
        List<Marker> carMarkerList = new ArrayList<>();
        for (int i = 0; i < cars.size(); i++) {
            Car car = cars.get(i);
            double latitude = car.getLocation().getLatitude();
            double longitude = car.getLocation().getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
            Marker carMarker = mMap.addMarker(MapUtils.generateMarker(this, latLng, R.drawable.ic_car_24dp));
            carMarkerList.add(carMarker);
        }
        LatLngBounds latLngBounds = MapUtils.getMarkerBounds(carMarkerList);
        int padding = 10; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(latLngBounds, padding);
        // zoom to found cars bounds
        mMap.animateCamera(cu);
    }

    private void zoomToUserSeconds(int seconds) {
        // zoom to user location when idle
        mMap.setOnCameraIdleListener(() -> mMap.animateCamera(CameraUpdateFactory.newLatLng(mUserLatLng)));
        // remove idle listener after seconds
        final Handler handler = new Handler();
        handler.postDelayed(() -> mMap.setOnCameraIdleListener(null), seconds * 1000);
    }

    private void setDebugConfig() {
        Stetho.initializeWithDefaults(this);
        Logger.addLogAdapter(new AndroidLogAdapter());
        rxPermissions.setLogging(true);
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
        menu.getItem(1).setEnabled(false);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menu_appbar_sort_distance:
//                Logger.d("Sort menu item selected!");
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private boolean internetExists() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_map_aubergine));

        // Add car markers and zoom to them
        if (internetExists()) {
            Disposable carsMapDisp = mCachedCarsResponse.subscribe(this::addCarMarkers);
            disps.add(carsMapDisp);
        } else {
            ToastUtils.showShort(stringNoInternet);
        }

        // display user marker after location permission granted
        Disposable permissionDisp = rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {
                        addUserMarker();
                    } else {
                        ToastUtils.showShort("Could not find your location");
                    }
                });
        disps.add(permissionDisp);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void addUserMarker() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Disposable locationDisp = rxLocation.location().updates(locationRequest)
                    .flatMap(location -> rxLocation.geocoding().fromLocation(location).toObservable())
                    .subscribe(address -> {
                        // Add a marker of user location & zoom to it
                        mUserLatLng = new LatLng(address.getLatitude(), address.getLongitude());
                        mMap.addMarker(MapUtils.generateMarker(this, mUserLatLng, R.drawable.ic_android_24dp));
                        zoomToUserSeconds(3);
                    });
            disps.add(locationDisp);
        }
    }
}