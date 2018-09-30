package com.github.neone35.chargent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;
import butterknife.BindString;
import butterknife.ButterKnife;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import com.blankj.utilcode.util.ToastUtils;
import com.facebook.stetho.Stetho;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

public class MainMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @BindString(R.string.app_name)
    String appName;
    @BindString(R.string.activity_maps_title)
    String mapsActivityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDebugConfig();
        setContentView(R.layout.activity_main_maps);
        ButterKnife.bind(this);
        setActionBar();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.frag_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            ToastUtils.showShort("Map initialization failed");
        }
    }

    private void setDebugConfig() {
        Stetho.initializeWithDefaults(this);
        Logger.addLogAdapter(new AndroidLogAdapter());
    }

    private void setActionBar() {
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(appName);
            ab.setSubtitle(mapsActivityTitle);
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
