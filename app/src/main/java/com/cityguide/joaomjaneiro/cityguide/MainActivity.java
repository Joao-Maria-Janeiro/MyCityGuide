package com.cityguide.joaomjaneiro.cityguide;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationListener locationListener;

    double lat = 0;
    double longi = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //If there's no Internet connection throw a popup
        if(!isConnected(MainActivity.this)) {
            buildDialog(MainActivity.this).show();
            setContentView(R.layout.no_internet);
        }else {

        }

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //If there's no Internet connection throw a popup
                if(!isConnected(MainActivity.this)) {
                    buildDialog(MainActivity.this).show();
                    setContentView(R.layout.no_internet);
                }else { //Only runs the app main code if there is an active internet connection
                    //Converting the GPS string to two integers for latitude and longitude
                    //-----------------------------------
                    String coords = location.toString();
                    String[] coord = coords.split(" ");
                    String[] buffer = coord[1].split(",");
                    lat = Double.parseDouble(buffer[0]);
                    longi = Double.parseDouble(buffer[1]);
                    //------------------------------------

                    //Displaying the coordenates on screen
                    //------------------------------
                    TextView lat_display = (TextView) findViewById(R.id.latitude);
                    lat_display.setText(lat + "");

                    TextView long_display = (TextView) findViewById(R.id.longDisplay);
                    long_display.setText(longi + "");

                    String address = displayCoord(lat, longi);
                    //------------------------------
                }


                TextView long_display = (TextView) findViewById(R.id.longDisplay);
                long_display.setText(longi + "");

                String address = displayCoord(lat, longi);

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        //If we don't have the permission to access GPS, we request it:
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);


        }else{
            //After we have the user's permission
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        }
    }

    //After the request is accepted get the coordinates
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED ) { //We could add "&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED" to the code
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }



    //Transform coordenates into a String
    public String getAddress(Context ctx, double lat, double lng){
        String FullAdd = null;
        try {
            Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if( addresses.size() > 0){
                Address address = addresses.get(0);
                FullAdd = address.getAddressLine(0);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return FullAdd;
    }


    //Display the address in a TextView
    public String displayCoord(double lat, double longi){
        String buffer = getAddress(this, lat, longi);
        String[] tmp = buffer.split(",");
        String address = tmp[0];
        address = address.replaceAll("\\d","");
        address = address.substring(0, address.length() - 1);
        TextView add = (TextView) findViewById(R.id.addressText);
        add.setText(address);
        return address;
    }

    //Check if the user's device is connected to the internet
    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
        else return false;
        } else
        return false;
    }

    //Dialog
    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have Mobile Data or WiFi always on to use this application. Press ok to Exit");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();
            }
        });

        return builder;
    }




}
