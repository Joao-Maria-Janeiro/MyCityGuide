package com.cityguide.joaomjaneiro.cityguide;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountFragment extends Fragment implements View.OnClickListener{
    private TextView tvPoints, tvLogout, tvUsername, tvChangeName, tvSettings, tvHelp, tvTerms;
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

        tvPoints = v.findViewById(R.id.tvPoints);
        tvLogout = v.findViewById(R.id.textViewLogout);
        tvUsername = v.findViewById(R.id.textViewUsername);
        tvChangeName = v.findViewById(R.id.username);
        tvSettings = v.findViewById(R.id.textViewSettings);
        tvHelp = v.findViewById(R.id.textViewHelp);
        tvTerms = v.findViewById(R.id.textViewTerms);
        profileImg = v.findViewById(R.id.cImgViewAccount);

        tvLogout.setOnClickListener(this);
        tvUsername.setOnClickListener(this);
        tvSettings.setOnClickListener(this);
        tvHelp.setOnClickListener(this);
        tvTerms.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {

    }
}
