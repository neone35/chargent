package com.github.neone35.chargent;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;
import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.facebook.stetho.Stetho;
import com.github.neone35.chargent.model.Car;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.resources.TextAppearance;
import com.google.android.material.resources.TextAppearanceConfig;
import com.google.maps.android.ui.IconGenerator;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.patloew.rxlocation.RxLocation;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

public class MainMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    // keep all subscribtions to observables in one variable to easily unsubscribe
    private CompositeSubscription subs = new CompositeSubscription();
    private CompositeDisposable disps = new CompositeDisposable();
    private CarVM carVM;
    private RxLocation rxLocation;
    private RxPermissions rxPermissions;

    @BindView(R.id.bnv_main)
    BottomNavigationView bnvMain;

    @BindString(R.string.app_name)
    String stringAppName;
    @BindString(R.string.nav_map_label)
    String stringMapsTitle;
    @BindString(R.string.nav_list_label)
    String stringListTitle;
    @BindString(R.string.no_internet)
    String stringNoInternet;
    @BindColor(R.color.colorPrimaryDark)
    int colorPrimaryDark;
    @BindColor(R.color.colorAccent)
    int colorAccent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_maps);
        setDebugConfig();
        ButterKnife.bind(this);
        setActionBar();
        rxLocation = new RxLocation(this);
        rxPermissions = new RxPermissions(this);

        // create cars viewmodel instance
        // interactor subscribes, viewmodel observes
        carVM = new CarVM(new CarInteractorImpl(), AndroidSchedulers.mainThread());

        Disposable locationDisp = rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {
                        // obtain the SupportMapFragment and get notified when the map is ready to be used.
                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.frag_map);
                        if (mapFragment != null) {
                            mapFragment.getMapAsync(this);
                        } else {
                            ToastUtils.showShort("Map initialization failed");
                        }
                    }
                });
        disps.add(locationDisp);

        listenBnv();

        if (internetExists()) {
            performCarFetch();
        } else {
            ToastUtils.showShort(stringNoInternet);
        }
    }

    @Override
    protected void onDestroy() {
        disps.clear();
        subs.clear();
        super.onDestroy();
    }

    private void listenBnv() {
        bnvMain.setOnNavigationItemSelectedListener(menuItem -> {
            int selectedItemId = menuItem.getItemId();
            switch (selectedItemId) {
                case R.id.bnv_action_map:
                    Logger.d("Map selected!");
                    break;
                case R.id.bnv_action_list:
                    Logger.d("List selected!");
                    break;
            }
            return true;
        });
    }

    private void performCarFetch() {
        Observer<List<Car>> carsObserver = new Observer<List<Car>>() {
            @Override
            public void onCompleted() {

            }
            @Override
            public void onError(Throwable e) {
                Logger.e(e.getMessage());
            }
            @Override
            public void onNext(List<Car> cars) {
                //Add cars markers
                List<Marker> carMarkerList = new ArrayList<>();
                for (int i = 0; i < cars.size(); i++) {
                    Car car = cars.get(i);
                    double latitude = car.getLocation().getLatitude();
                    double longitude = car.getLocation().getLongitude();
                    String carTitle = car.getModel().getTitle();
                    LatLng latLng = new LatLng(latitude, longitude);
                    Marker carMarker = mMap.addMarker(generateMarker(latLng, carTitle, colorPrimaryDark, R.style.carMarkerIconText));
                    carMarkerList.add(carMarker);
                }
                LatLngBounds latLngBounds = getMarkerBounds(carMarkerList);
                int padding = 10; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(latLngBounds, padding);
                mMap.animateCamera(cu);
            }
        };

        Subscription carsSub = carVM.fetch().subscribe(carsObserver);
        subs.add(carsSub);
    }

    private boolean internetExists() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }

    private void setDebugConfig() {
        Stetho.initializeWithDefaults(this);
        Logger.addLogAdapter(new AndroidLogAdapter());
    }

    private void setActionBar() {
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(stringAppName);
            ab.setSubtitle(stringMapsTitle);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_appbar, menu);
        return true;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Disposable locDisp = rxLocation.location().updates(locationRequest)
                    .flatMap(location -> rxLocation.geocoding().fromLocation(location).toObservable())
                    .subscribe(address -> {
                        // Add a marker of user location and zoom to it
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        mMap.addMarker(generateMarker(latLng, "You", colorAccent, R.style.userMarkerIconText));
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    });
            disps.add(locDisp);
        }
    }

    private MarkerOptions generateMarker(LatLng latLng, String markerTitle, int bgColor, int textStyle) {
        // Build an icon with place name
        IconGenerator iconGenerator = new IconGenerator(MainMapsActivity.this);
        iconGenerator.setColor(bgColor);
        iconGenerator.setTextAppearance(textStyle);
        Bitmap bitmap = iconGenerator.makeIcon(markerTitle);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);
        // Build a marker
        return new MarkerOptions()
                .position(latLng)
                .icon(icon);
    }

    private LatLngBounds getMarkerBounds(List<Marker> markerList) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markerList) {
            builder.include(marker.getPosition());
        }
        return builder.build();
    }
}
