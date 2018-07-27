package com.cityguide.joaomjaneiro.cityguide;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cityguide.joaomjaneiro.cityguide.EasterEgg.Tecnico;
import com.cityguide.joaomjaneiro.cityguide.PointsOfInterest.Point_Activity;
import com.cityguide.joaomjaneiro.cityguide.User.AccountFragment;
import com.cityguide.joaomjaneiro.cityguide.User.HomeFragment;
import com.cityguide.joaomjaneiro.cityguide.User.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationListener locationListener;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference dbReference;

    private AccountFragment accountFragment;
    private HomeFragment homeFragment;


    ArrayList<String> pointInfo = new ArrayList<>();

    String address;

    double lat = 0;
    double longi = 0;

    boolean addressFound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //If there's no Internet connection throw a popup
        if(!isConnected(MainActivity.this)) {
            buildDialog(MainActivity.this).show();
            setContentView(R.layout.no_internet);
        }else {


            //Checks if user is logged in
            firebaseAuth = FirebaseAuth.getInstance();

            dbReference = FirebaseDatabase.getInstance().getReference();

            accountFragment = new AccountFragment();
            homeFragment = new HomeFragment();



            ImageButton userBtn = findViewById(R.id.userBtn);
            userBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(firebaseAuth.getCurrentUser() != null){
                        setFragment(accountFragment);
                    }else{
                        openUserActivity();
                    }
                }
            });

            ImageButton mapBtn = findViewById(R.id.mapBtn);
            mapBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openMapActivity();
                }
            });


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

                    //Displaying the information on screen
                    //------------------------------
                    address = displayCoord(lat, longi);
                    //------------------------------

                    ImageButton availableLocation = findViewById(R.id.availableLocation);
                    ImageButton upNextBtn = findViewById(R.id.upNextBtn);
                    ImageButton homeBtn = findViewById(R.id.mainActivityBtn);




                    homeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setFragment(homeFragment);
                        }
                    });

                    loadPoints();
                    if(addressFound){
                        Picasso.get().load(pointInfo.get(2)).resize(getResources().getDisplayMetrics().widthPixels, 700).into(availableLocation);
                        availableLocation.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(pointInfo.get(0).equals("Av. Rovisco Pais")){
                                    openTecnico();
                                }else{
                                    openActivity();
                                }

                            }
                        });
                        addressFound = false;
                        pointInfo.clear();
                    }

                }

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


    //Corrdinates to address handling:

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


    //Reduce the address to the interesting part and display the address in a TextView
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

    //Internet checks and dialogs:

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

    //Gets all the points from the database
    public void loadPoints() {
        dbReference.child("Places").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot information : dataSnapshot.getChildren()) {
                    String placeUid = information.getKey().toString();
                    String description = information.child("description").getValue().toString();
                    String name = information.child("name").getValue().toString();
                    String image = information.child("image").getValue().toString();
                    String audio = information.child("audio").getValue().toString();

                    if(address.equals(name)){
                        pointInfo.add(name);
                        pointInfo.add(description);
                        pointInfo.add(image);
                        pointInfo.add(placeUid);
                        pointInfo.add(audio);
                        addressFound = true;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void openActivity(){
        Intent myIntent = new Intent(this, Point_Activity.class);
        myIntent.putExtra("title", pointInfo.get(0));
        myIntent.putExtra("description", pointInfo.get(1));
        myIntent.putExtra("image", pointInfo.get(2));
        myIntent.putExtra("placeId", pointInfo.get(3));
        myIntent.putExtra("audio", pointInfo.get(4));
        startActivity(myIntent);
    }

    public void openTecnico(){
        Intent myIntent = new Intent(this, Tecnico.class);
        myIntent.putExtra("title", pointInfo.get(0));
        myIntent.putExtra("description", pointInfo.get(1));
        myIntent.putExtra("image", pointInfo.get(2));
        myIntent.putExtra("placeId", pointInfo.get(3));
        myIntent.putExtra("audio", pointInfo.get(4));
        startActivity(myIntent);
    }

    public void openUserActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void openMapActivity(){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }


    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.menuFrame, fragment);
        fragmentTransaction.commit();
    }

    public void openUserFragment(Fragment fragment){
        setFragment(fragment);
    }

}
