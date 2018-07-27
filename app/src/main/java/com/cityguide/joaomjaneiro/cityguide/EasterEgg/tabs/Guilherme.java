package com.cityguide.joaomjaneiro.cityguide.EasterEgg.tabs;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.cityguide.joaomjaneiro.cityguide.R;

public class Guilherme extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.guilherme_fragment, container, false);

        ImageButton linkedIn = (ImageButton)rootView.findViewById(R.id.linkedIn2);
        ImageButton gitHub = (ImageButton)rootView.findViewById(R.id.gitHub2);


        linkedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/joaomariajaneiro/"));
                startActivity(browser);
            }
        });

        gitHub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Guilherme-Viegas"));
                startActivity(browser);
            }
        });


        return rootView;

    }
}
