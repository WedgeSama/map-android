package fr.adrec.map;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private MapboxMap mapboxMap;
    private LocationComponent locationComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(getApplicationContext(), getString(R.string.mapboxApiToken));
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                MainActivity.this.requestLocation();
            }
        });

        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        locationComponent = mapboxMap.getLocationComponent();
        this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(Style.TRAFFIC_NIGHT, new Style.OnStyleLoaded(){
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                locationComponent.activateLocationComponent(
                        LocationComponentActivationOptions.builder(MainActivity.this, style)
                                .build()
                );
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestLocation() {
        String[] perms = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        requestPermissions(perms, 42);

        if (PermissionChecker.PERMISSION_GRANTED == checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.COMPASS);

            Location lastLocation = locationComponent.getLastKnownLocation();

            if (null != lastLocation) {
                mapboxMap.setCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(lastLocation))
                        .zoom(13)
                        .build()
                );
            }
        } else {
            Toast.makeText(MainActivity.this, R.string.location_refused,
                    Toast.LENGTH_LONG).show();
        }
    }
}
