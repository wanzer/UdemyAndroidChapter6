package ricoapp.memorableplaces;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastKnownLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                LatLng lastLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(lastLocation).title("User position"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation,10));
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        mMap.setOnMapLongClickListener(this);
        Intent intent = getIntent();

       if(intent.getIntExtra("Position",0)==0){
           //zoom on a user location
           locationManager=(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
           locationListener=new LocationListener() {
               @Override
               public void onLocationChanged(Location location) {

                   LatLng userPosition = new LatLng(location.getLatitude(), location.getLongitude());
                   mMap.clear();
                   mMap.addMarker(new MarkerOptions().position(userPosition).title("User position"));
                   mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userPosition,10));

               }

               @Override
               public void onStatusChanged(String provider, int status, Bundle extras) {

               }

               @Override
               public void onProviderEnabled(String provider) {

               }

               @Override
               public void onProviderDisabled(String provider) {

               }
           };





       if (Build.VERSION.SDK_INT<23){

           locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
       }
       else{
          if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
               ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
           }
           else {
              locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
              Location lastKnownLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
              LatLng lastLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
              mMap.addMarker(new MarkerOptions().position(lastLocation).title("User position"));
              mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation,10));

          }
       }
       }
       //if tap on any added item in the list, we should go to that saved location
       else {
           mMap.clear();
           mMap.addMarker(new MarkerOptions().position(MainActivity.location.get(intent.getIntExtra("Position",0))).title(MainActivity.place.get(intent.getIntExtra("Position",0))));
           mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MainActivity.location.get(intent.getIntExtra("Position",0)),10));

       }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        String address="";
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            if(addressList!=null && addressList.size()>0){
                if (addressList.get(0).getThoroughfare()!=null)
                    address+=addressList.get(0).getThoroughfare()+" ";
                if(addressList.get(0).getSubThoroughfare()!=null)
                    address+=addressList.get(0).getSubThoroughfare();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        //if three are no Thoroughfare or SubThoroughfare, let's display date at least
        if (address.isEmpty()){
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm yyyy-MM-dd");
            address= sdf.format(new Date());
        }
        ArrayList<String> latitude = new ArrayList<>();
        ArrayList<String> longtitude = new ArrayList<>();
        for (LatLng coordinates:MainActivity.location){
            latitude.add(Double.toString(coordinates.latitude));
            longtitude.add(Double.toString(coordinates.longitude));
        }
        mMap.addMarker(new MarkerOptions().position(latLng).title(address));
        MainActivity.place.add(address);
        MainActivity.location.add(latLng);
        MainActivity.adapter.notifyDataSetChanged();
        SharedPreferences sharedPreferences = this.getSharedPreferences("ricoapp.memorableplaces",Context.MODE_PRIVATE);
        try {
            sharedPreferences.edit().putString("place",ObjectSerializer.serialize(MainActivity.place)).apply();
            sharedPreferences.edit().putString("latitude",ObjectSerializer.serialize(latitude)).apply();
            sharedPreferences.edit().putString("longtitude",ObjectSerializer.serialize(longtitude)).apply();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(getApplicationContext(),"Location added",Toast.LENGTH_LONG).show();
    }
}
