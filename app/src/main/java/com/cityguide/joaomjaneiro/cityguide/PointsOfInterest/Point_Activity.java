package com.cityguide.joaomjaneiro.cityguide.PointsOfInterest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.cityguide.joaomjaneiro.cityguide.R;

public class Point_Activity extends AppCompatActivity {

    String title;
    String description;

    public Point_Activity(String title, String description) {
        this.title = title;
        this.description = description;
    }
    public Point_Activity(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point);

        TextView setTitle= (TextView)findViewById(R.id.title);
        TextView setDescription= (TextView)findViewById(R.id.description);
        setTitle.setText(title);
        setDescription.setText(description);

    }
}
