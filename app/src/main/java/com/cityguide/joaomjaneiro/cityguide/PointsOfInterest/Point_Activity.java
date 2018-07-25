package com.cityguide.joaomjaneiro.cityguide.PointsOfInterest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cityguide.joaomjaneiro.cityguide.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class Point_Activity extends AppCompatActivity {

    private FirebaseDatabase database;
    private FirebaseAuth firebaseAuth;

    private DatabaseReference db;

    String user_id;

    boolean canAdd = true;



    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point);

        db = FirebaseDatabase.getInstance().getReference();


        TextView setTitle= (TextView)findViewById(R.id.title);
        TextView setDescription= (TextView)findViewById(R.id.description);
        ImageView imageView = findViewById(R.id.imageView);
        Button saveBtn = (Button)findViewById(R.id.saveBtn);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        String image = intent.getStringExtra("image");
        setTitle.setText(title);
        setDescription.setText(description);
        Picasso.get().load(image).resize(getResources().getDisplayMetrics().widthPixels, 700).into(imageView);


        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){ //If user is signed in
            user_id = firebaseAuth.getCurrentUser().getUid().toString();


            db.child("Users").child("user_id").child("locations").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot information : dataSnapshot.getChildren()) {
                        String place = information.getKey().toString();
                        Toast.makeText(Point_Activity.this, "Teste", Toast.LENGTH_SHORT).show();
                        String name = information.child(place).getValue().toString();

                        if(("Location name:" + title).equals(name)){
                            canAdd = false;
                            break;
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            if(canAdd){
                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        db.child("Users").child(user_id).child("locations").push().setValue("Location name:" + title);
                    }
                });
            }

        }else{
            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(Point_Activity.this, "You are not signed in", Toast.LENGTH_SHORT).show();
                }
            });
        }












    }
}
