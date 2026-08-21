package com.techfix.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.R;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

import java.util.Locale;

public class MapPickerActivity extends AppCompatActivity {

    private MapView mapView;
    private TextView txtSelectedLocation;
    private Button btnConfirmLocation;

    private Marker selectedMarker;

    private double selectedLatitude;
    private double selectedLongitude;

    private boolean locationSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(
                getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(
                        getApplicationContext()
                )
        );

        Configuration.getInstance().setUserAgentValue(
                getPackageName()
        );

        setContentView(
                R.layout.activity_map_picker
        );

        mapView =
                findViewById(
                        R.id.mapView
                );

        txtSelectedLocation =
                findViewById(
                        R.id.txtSelectedLocation
                );

        btnConfirmLocation =
                findViewById(
                        R.id.btnConfirmLocation
                );

        setupMap();

        btnConfirmLocation.setOnClickListener(
                view -> returnSelectedLocation()
        );
    }

    private void setupMap() {

        mapView.setTileSource(
                TileSourceFactory.MAPNIK
        );

        mapView.setMultiTouchControls(
                true
        );

        mapView.getController()
                .setZoom(
                        8.0
                );

        GeoPoint sriLanka =
                new GeoPoint(
                        7.8731,
                        80.7718
                );

        mapView.getController()
                .setCenter(
                        sriLanka
                );

        MapEventsReceiver receiver =
                new MapEventsReceiver() {

                    @Override
                    public boolean singleTapConfirmedHelper(
                            GeoPoint point
                    ) {

                        selectLocation(
                                point
                        );

                        return true;
                    }

                    @Override
                    public boolean longPressHelper(
                            GeoPoint point
                    ) {

                        selectLocation(
                                point
                        );

                        return true;
                    }
                };

        MapEventsOverlay mapEventsOverlay =
                new MapEventsOverlay(
                        receiver
                );

        mapView.getOverlays()
                .add(
                        mapEventsOverlay
                );
    }

    private void selectLocation(
            GeoPoint point
    ) {

        selectedLatitude =
                point.getLatitude();

        selectedLongitude =
                point.getLongitude();

        locationSelected = true;

        if (selectedMarker != null) {

            mapView.getOverlays()
                    .remove(
                            selectedMarker
                    );
        }

        selectedMarker =
                new Marker(
                        mapView
                );

        selectedMarker.setPosition(
                point
        );

        selectedMarker.setAnchor(
                Marker.ANCHOR_CENTER,
                Marker.ANCHOR_BOTTOM
        );

        selectedMarker.setTitle(
                "Selected Branch Location"
        );

        mapView.getOverlays()
                .add(
                        selectedMarker
                );

        mapView.invalidate();

        String locationText =
                String.format(
                        Locale.getDefault(),
                        "Latitude: %.6f\nLongitude: %.6f",
                        selectedLatitude,
                        selectedLongitude
                );

        txtSelectedLocation.setText(
                locationText
        );

        btnConfirmLocation.setEnabled(
                true
        );
    }

    private void returnSelectedLocation() {

        if (!locationSelected) {
            return;
        }

        Intent resultIntent =
                new Intent();

        resultIntent.putExtra(
                "selected_latitude",
                selectedLatitude
        );

        resultIntent.putExtra(
                "selected_longitude",
                selectedLongitude
        );

        setResult(
                RESULT_OK,
                resultIntent
        );

        finish();
    }

    @Override
    protected void onResume() {

        super.onResume();

        mapView.onResume();
    }

    @Override
    protected void onPause() {

        super.onPause();

        mapView.onPause();
    }
}