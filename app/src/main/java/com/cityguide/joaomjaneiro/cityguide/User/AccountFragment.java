package com.cityguide.joaomjaneiro.cityguide.User;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cityguide.joaomjaneiro.cityguide.MainActivity;
import com.cityguide.joaomjaneiro.cityguide.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class AccountFragment extends Fragment implements View.OnClickListener{
    private TextView tvPoints, tvLogout, tvUsername, tvChangeName, tvSettings, tvHelp, tvTerms, userLocations;
    private CircleImageView profileImg;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference dbReference;

    public AccountFragment() {
        //Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        //If user is not logged in
        if(firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "Can't access account - LogIn First!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getContext(), MainActivity.class));
        }

        tvPoints = v.findViewById(R.id.tvPoints);
        tvLogout = v.findViewById(R.id.textViewLogout);
        tvUsername = v.findViewById(R.id.textViewUsername);
        tvChangeName = v.findViewById(R.id.username);
        tvSettings = v.findViewById(R.id.textViewSettings);
        tvHelp = v.findViewById(R.id.textViewHelp);
        tvTerms = v.findViewById(R.id.textViewTerms);
        profileImg = v.findViewById(R.id.cImgViewAccount);
        userLocations = v.findViewById(R.id.usrLocations);

        if(firebaseAuth.getCurrentUser() != null) {
            String user_id = firebaseAuth.getCurrentUser().getUid().toString();
            dbReference = FirebaseDatabase.getInstance().getReference().child("Users");
            dbReference.child(user_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String username = dataSnapshot.child("username").getValue().toString();
                    String points = dataSnapshot.child("points").getValue().toString();

                    if(dataSnapshot.child("image").exists()) {
                        String url = dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(url).into(profileImg);
                    }

                    tvPoints.setText(points);
                    tvChangeName.setText(username);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        tvLogout.setOnClickListener(this);
        tvUsername.setOnClickListener(this);
        tvSettings.setOnClickListener(this);
        tvHelp.setOnClickListener(this);
        tvTerms.setOnClickListener(this);
        userLocations.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.textViewLogout:
                if(firebaseAuth.getCurrentUser() != null) {
                    firebaseAuth.signOut();
                    getActivity().finish();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.usrLocations:
                Intent intent = new Intent(getActivity(), DisplayUserPlaces.class);
                startActivity(intent);
        }
    }
}
