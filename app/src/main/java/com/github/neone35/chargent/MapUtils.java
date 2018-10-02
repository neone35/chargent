package com.github.neone35.chargent;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import java.util.List;

class MapUtils {

    static MarkerOptions generateMarker(Context ctx, LatLng latLng, String markerTitle,
                                        int bgColor, int textStyle) {
        // Build an icon with place name
        IconGenerator iconGenerator = new IconGenerator(ctx);
        iconGenerator.setColor(bgColor);
        iconGenerator.setTextAppearance(textStyle);
        Bitmap bitmap = iconGenerator.makeIcon(markerTitle);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);
        // Build a marker
        return new MarkerOptions()
                .position(latLng)
                .icon(icon);
    }

    static LatLngBounds getMarkerBounds(List<Marker> markerList) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markerList) {
            builder.include(marker.getPosition());
        }
        return builder.build();
    }
}
