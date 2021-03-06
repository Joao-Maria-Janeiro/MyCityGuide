package com.cityguide.joaomjaneiro.cityguide.EasterEgg;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cityguide.joaomjaneiro.cityguide.R;
import com.cityguide.joaomjaneiro.cityguide.User.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class Tecnico extends AppCompatActivity {

    private FirebaseDatabase database;
    private FirebaseAuth firebaseAuth;

    private DatabaseReference db;

    String user_id;

    boolean canAdd = true;



    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tecnico);

        db = FirebaseDatabase.getInstance().getReference();


        TextView setTitle= (TextView)findViewById(R.id.title);
        TextView setDescription= (TextView)findViewById(R.id.description);
        ImageView imageView = findViewById(R.id.imageView);
        Button saveBtn = (Button)findViewById(R.id.saveBtn);
        Button creators = (Button) findViewById(R.id.creators);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        final String placeId = intent.getStringExtra("placeId");
        String description = intent.getStringExtra("description");
        String image = intent.getStringExtra("image");
        setTitle.setText(title);
        setDescription.setText(description);
        Picasso.get().load(image).resize(getResources().getDisplayMetrics().widthPixels, 700).into(imageView);


        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){ //If user is signed in
            user_id = firebaseAuth.getCurrentUser().getUid().toString();

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("locations");


            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                        Log.i("Location_id:", userSnapshot.getKey().toString());
                        Log.i("Location_name:", userSnapshot.getValue().toString());
                        Log.i("title", title);

                        if(userSnapshot.getValue().toString().equals(title)) {
                            canAdd = false;
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("Error", "onCancelled", databaseError.toException());
                }
            });

            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(canAdd) {
                        db.child("Users").child(user_id).child("locations").child(placeId).setValue(title);
                    }else {
                        Toast.makeText(Tecnico.this, "That place is already saved", Toast.LENGTH_SHORT).show();

                    }
                }
            });


        }else{
            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(Tecnico.this, "You are not signed in", Toast.LENGTH_SHORT).show();
                }
            });
        }

        creators.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Tecnico.this, Creator.class);
                startActivity(intent);
            }
        });

    }
}
