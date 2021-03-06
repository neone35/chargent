package com.github.neone35.chargent.map;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import android.os.Handler;

import com.blankj.utilcode.util.ToastUtils;
import com.github.neone35.chargent.MainActivity;
import com.github.neone35.chargent.R;
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
import com.patloew.rxlocation.RxLocation;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MapFragment extends SupportMapFragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    // keep all disposables in one variable to easily unsubscribe
    private CompositeDisposable mDisps = new CompositeDisposable();
    public static LatLng mUserLatLng;
    private Context mCtx;
    private RxLocation rxLocation;
    private RxPermissions rxPermissions;
    private List<Marker> mCarMarkerList = new ArrayList<>();

    public MapFragment() {
        // Required empty public constructor}
    }

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rxLocation = new RxLocation(Objects.requireNonNull(this.getActivity()));
        rxPermissions = new RxPermissions(this);
        rxPermissions.setLogging(true);
        mCtx = this.getActivity();
        // get notified when the map is ready to be used.
        getMapAsync(this);
        // save map state on config change
        setRetainInstance(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        // dont wait for onMapReady callback by onCreate which handles permissions
        // because cars are available before maps and require no dangerous permissions
        getMapAsync(googleMap -> {
            Disposable disposable = MainActivity.carVM.getState()
                .subscribe(carsState -> {
                    if (!carsState.getCars().isEmpty()) {
                        addCarMarkers(carsState.getCars());
                    } else {
                        ToastUtils.showShort("No cars found. Change filters");
                    }
                });
            mDisps.add(disposable);
        });
    }

    @Override
    public void onStop() {
        // clear all, but allow new
        mDisps.clear();
        super.onStop();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                Objects.requireNonNull(this.getActivity()), R.raw.style_map_aubergine));

        // add user marker after location permission granted
        Disposable permissionDisp = rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {
                        addUserMarker();
                    } else {
                        ToastUtils.showShort("Could not find your location");
                    }
                });
        mDisps.add(permissionDisp);
    }

    private void addUserMarker() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // ask for location permission only on M
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mCtx.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Disposable locationDisp = rxLocation.location().updates(locationRequest)
                        .flatMap(location -> rxLocation.geocoding().fromLocation(location).toObservable())
                        .subscribe(address -> {
                            // Add a marker of user location & zoom to it
                            mUserLatLng = new LatLng(address.getLatitude(), address.getLongitude());
                            Marker userMarker = mMap.addMarker(
                                    MapUtils.generateMarker(mCtx, mUserLatLng, R.drawable.ic_android_24dp));
                            zoomToAllSeconds(userMarker);
                        });
                mDisps.add(locationDisp);
            }
        }
    }

    private void addCarMarkers(List<Car> cars) {
        mCarMarkerList = new ArrayList<>();
        for (int i = 0; i < cars.size(); i++) {
            Car car = cars.get(i);
            double latitude = car.getLocation().getLatitude();
            double longitude = car.getLocation().getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
            Marker carMarker = mMap.addMarker(
                    MapUtils.generateMarker(this.getActivity(), latLng, R.drawable.ic_car_24dp));
            mCarMarkerList.add(carMarker);
        }
        LatLngBounds latLngBounds = MapUtils.getMarkerBounds(mCarMarkerList);
        int padding = 100; // offset from edges of the map in pixels
        int width = Objects.requireNonNull(this.getView()).getWidth(); //map fragment(view) width;
        int height = Objects.requireNonNull(this.getView()).getHeight(); //map fragment(view) height;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(latLngBounds, width, height, padding);
        // zoom to found cars bounds
        mMap.animateCamera(cu);
    }

    private void zoomToAllSeconds(Marker userMarker) {
        mCarMarkerList.add(userMarker);
        List<Marker> userAndCarMarkers = new ArrayList<>(mCarMarkerList);
        LatLngBounds userAndCarsBounds = MapUtils.getMarkerBounds(userAndCarMarkers);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(userAndCarsBounds, 100);
        // zoom to user location when idle
        mMap.setOnCameraIdleListener(() -> mMap.animateCamera(cu));
        // remove idle listener after seconds
//        final Handler handler = new Handler();
//        handler.postDelayed(() -> mMap.setOnCameraIdleListener(null), 3 * 1000);
    }
}
