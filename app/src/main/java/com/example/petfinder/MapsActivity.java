package com.example.petfinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.print.PrintAttributes;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION_PERMISSION = 1094;
    private static final int REQUEST_LOCATION_SETTING_RESOLUTION = 22;

    private FusedLocationProviderClient fusedClient;
    private LocationCallback locationCallback;

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private Location currentLocation;
    private MainBottomSheetBehavior bottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        LinearLayout bottomSheetLayout = findViewById(R.id.bottom_sheet_design);
        bottomSheetBehavior = new MainBottomSheetBehavior(getApplicationContext(), bottomSheetLayout);

        fusedClient = LocationServices.getFusedLocationProviderClient(this);

        enableMyLocation();


    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        bottomSheetBehavior.setMainMap(mMap);
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15));
    }


    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }


    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        fusedClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, new CancellationToken() {
            @Override
            public boolean isCancellationRequested() {
                return false;
            }

            @NonNull
            @Override
            public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                Log.d("CurrentLocation", "CancelRequest");
                createLocationSettingRequest();
                CancellationTokenSource cts = new CancellationTokenSource();
                return cts.getToken();
            }
        }).addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Log.d("CurrentLocation", location.toString());
                    currentLocation = location;
                    mapFragment.getMapAsync(MapsActivity.this);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        //fusedClient.removeLocationUpdates(locationCallback);
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null)
                    return;
                for (Location location : locationResult.getLocations()) {
                    Log.d("CallbackLocation", location.toString());
                }
            }
        };

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        fusedClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
        Toast.makeText(this, "updating location", Toast.LENGTH_SHORT).show();
    }

    private void createLocationSettingRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        Task<LocationSettingsResponse> task =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.d("SettingRequest", "Success");
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MapsActivity.this,
                                REQUEST_LOCATION_SETTING_RESOLUTION);
                    } catch (IntentSender.SendIntentException ignored) {
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOCATION_SETTING_RESOLUTION && resultCode == RESULT_OK) {
            getCurrentLocation();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocation();
                    break;
                }
        }
    }
}