package com.cityguide.joaomjaneiro.cityguide.PointsOfInterest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cityguide.joaomjaneiro.cityguide.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

public class Point_Activity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference db;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point);

        TextView setTitle= (TextView)findViewById(R.id.title);
        TextView setDescription= (TextView)findViewById(R.id.description);
        ImageView imageView = findViewById(R.id.imageView);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        String image = intent.getStringExtra("image");
        setTitle.setText(title);
        setDescription.setText(description);
        Picasso.get().load(image).resize(getResources().getDisplayMetrics().widthPixels, 700).into(imageView);

        Button saveBtn = (Button)findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_id = firebaseAuth.getCurrentUser().getUid();
                db.child("Users").child(user_id).child("username").child("locations").setValue("location name" + title);
            }
        });

    }
}
