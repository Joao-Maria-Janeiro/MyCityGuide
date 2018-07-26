package com.cityguide.joaomjaneiro.cityguide.User;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.cityguide.joaomjaneiro.cityguide.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class DisplayUserPlaces extends AppCompatActivity {

    ListView listView;
    private UserPlacesAdapter userPlacesAdapter;
    FirebaseDatabase database;
    DatabaseReference db;
    DatabaseReference ref;
    ArrayList<String> placeNamesList;
    ArrayList<String> placeImgUrlList;
    ArrayAdapter<String> adapter;
    String user_id;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_user_places);

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid().toString();


        listView = findViewById(R.id.listView);
        database= FirebaseDatabase.getInstance();
        ref = database.getReference().child("Users").child(user_id).child("locations");
        db = FirebaseDatabase.getInstance().getReference().child("Places");

        placeNamesList = new ArrayList<String>();
        placeImgUrlList = new ArrayList<String>();

        //adapter = new ArrayAdapter<String>(this, R.layout.place_info, R.id.placeInfo, placeNamesList);


        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot == null) {
                    return;
                }
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    final String placeKey = ds.getKey();
                    Log.d("1234", placeKey);
                    db.child(placeKey).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot == null) {
                                return;
                            }
                            placeNamesList.add(dataSnapshot.child("name").getValue().toString());
                            placeImgUrlList.add(dataSnapshot.child("image").getValue().toString());
                            userPlacesAdapter = new UserPlacesAdapter(getApplicationContext(), placeNamesList, placeImgUrlList);
                            listView.setAdapter(userPlacesAdapter);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }
}
