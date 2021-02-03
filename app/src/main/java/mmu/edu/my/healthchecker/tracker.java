package mmu.edu.my.healthchecker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class tracker extends AppCompatActivity {

    private FusedLocationProviderClient client;
    private SupportMapFragment mapFragment;
    FirebaseFirestore fstore;
    FirebaseAuth fAuth;
    TextView userLocation;
    Button setAddress;
    String UserID;
    public String address = null;
    private int REQUEST_CODE = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Tracker");
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        fstore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
        client = LocationServices.getFusedLocationProviderClient(tracker.this);
        userLocation = findViewById(R.id.textViewUserLoc);
        setAddress = findViewById(R.id.setViewUserLoc);



        setAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                DocumentReference documentReference = fstore.collection("users").document(UserID);
                Map<String,Object> user  = new HashMap<>();
                user.put("Quarantine Location", address);
                documentReference.set(user).addOnSuccessListener(aVoid -> {Log.d("Onsucces","saved");}).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("onSuccess",e.toString());
                    }
                });
                Intent mainIntent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(mainIntent);
            }
        });


        Dexter.withContext(getApplicationContext())
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                    getmylocation();
                }

                @Override
                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                }

                @Override
                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
        }).check();
    }

    public void getmylocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                try{
                    Geocoder geo = new Geocoder(tracker.this, Locale.getDefault());
                    List<Address> addresses = geo.getFromLocation(location.getLatitude(),location.getLongitude(), 1);
                    if (addresses.isEmpty()) {
                        Log.d("locat","Waiting for Location");
                    }
                    else {
                        //String address = (addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
                        String address1 = addresses.get(0).getAddressLine(0);
                        Log.d("locati12",address1);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Here");

                        googleMap.addMarker(markerOptions);
                        googleMap.animateCamera((CameraUpdateFactory.newLatLngZoom(latLng,10)));

                        try{
                        Geocoder geo = new Geocoder(tracker.this, Locale.getDefault());
                        List<Address> addresses = geo.getFromLocation(location.getLatitude(),location.getLongitude(), 1);
                        if (addresses.isEmpty()) {
                            Log.d("locat","Waiting for Location");
                        }
                        else {
                            address = addresses.get(0).getAddressLine(0);
                            userLocation.setText(address);
                            Log.d("locati13",address);
                        }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }


}