package ricoapp.hikerswatch;

import android.Manifest;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;
    TextView textView;

    //result of permision
    @Override
    public void onRequestPermissionsResult(int requesrCode, @NonNull String[]permissions, @NonNull int[]grantResults){
        super.onRequestPermissionsResult(requesrCode,permissions,grantResults);

        if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==PackageManager.PERMISSION_GRANTED) {
                //if have a permission - can start listen user location
                //num 0,0 -check user non-stop
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        textView=(TextView)findViewById(R.id.location);

        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //log user location
                Log.i("Location",location.toString());
                String params="";
                String address="";
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                //Locale.getDefault - output format
                try {
                    List<Address> addressList = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

                    if (addressList!=null && addressList.size()>0){
                         Log.i("Placeinfo",addressList.get(0).toString());

                        if(addressList.get(0).getSubThoroughfare()!=null){
                            address+=addressList.get(0).getSubThoroughfare()+"\r\n ";
                        }
                        if (addressList.get(0).getThoroughfare()!=null){
                            address+=addressList.get(0).getThoroughfare()+"\r\n";
                        }
                        if (addressList.get(0).getLocality()!=null){
                            address+=addressList.get(0).getLocality()+"\r\n";
                        }
                        if (addressList.get(0).getPostalCode()!=null){
                            address+=addressList.get(0).getPostalCode()+"\r\n";
                        }
                        if (addressList.get(0).getCountryName()!=null){
                            address+=addressList.get(0).getCountryName();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                params="Latitude :"+location.getLatitude()+"\r\n"+"\r\n"+"Longtitude :"+location.getLongitude()+"\r\n"+"\r\n"+"Accuracy: "+location.getAccuracy()+"\r\n"+"\r\n"+"Altitude :"+location.getAltitude()+"\r\n"+"\r\n"+"Adress :"+"\r\n"+address;
                textView.setText(params);
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
        //if devise running sdk<23
        if (Build.VERSION.SDK_INT<23){

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
        }
        else {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                //Ask for permision
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                //we have permission
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }



    }
}
