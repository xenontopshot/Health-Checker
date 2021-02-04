package mmu.edu.my.healthchecker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class CheckLocation extends AppCompatActivity implements LocationListener {
    LocationManager locationManager;
    String checkAdd;
    String work;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkAdd = getIntent().getStringExtra("verifyAddress");
        Log.d("LocationSet1",checkAdd);
        checkLocationPermission();
        getLocation();
        if(checkAdd.equals(work)){
            Log.d("same?:","same");
            Intent i = new Intent(CheckLocation.this, MainActivity.class);
            startActivity(i);
        }else{
            Log.d("same?:","NOTsame");
            Toast.makeText(this, "Location Different", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(CheckLocation.this, MainActivity.class);
            i.putExtra("result", "NOTSAME");
            startActivity(i);
        }



    }

    private void getLocation() {
        locationManager= (LocationManager) this.getSystemService(LOCATION_SERVICE);
        checkLocationPermission();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000,100, this);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Geocoder geocoder = new Geocoder(this,Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String address = addresses.get(0).getAddressLine(0);
            work = address;
            Log.i("LocationSet1",address);
            //Log.i("LocationNow1",checkAdd);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},100);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        Geocoder geocoder = new Geocoder(this,Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String address = addresses.get(0).getAddressLine(0);
            work = address;
            Log.i("LocationSet2",address);
            //Log.i("LocationNow2",checkAdd);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        Log.i("Location","Provider Enable");

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i("Location","Provider Enable");
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Log.i("Location","Provider Disable");

    }
}