package com.example.queenelizabethviii.caireen2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Created by Queen Elizabeth VIII on 4/18/2018.
 */

public class homepage_features extends Fragment {
    private ImageButton bmiicon, ciaicon, articleicon,forumicon,locatoricon,aboutusicon, aboutus2icon, immunizationicon;
    private static final String TAG = "homepagefeature";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.homepage_features, container,false);
        bmiicon = (ImageButton) view.findViewById(R.id.bmiicon);
        aboutusicon = (ImageButton) view.findViewById(R.id.aboutusicon);
        aboutus2icon = (ImageButton) view.findViewById(R.id.aboutus2icon);
        ciaicon = (ImageButton) view.findViewById(R.id.ciaicon);
        articleicon = (ImageButton) view.findViewById(R.id.articleicon);
        forumicon = (ImageButton) view.findViewById(R.id.forumicon);
        locatoricon = (ImageButton) view.findViewById(R.id.locatoricon);
        immunizationicon = (ImageButton) view.findViewById(R.id.immunizationicon);

        bmiicon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                openDiary();
            }
        });
        aboutusicon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                openVitamin();
            }
        });
        ciaicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(getActivity(), "Feature not yet available.", Toast.LENGTH_SHORT).show();
                openGrowthCharts();
            }
        });
        articleicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(), "Feature not yet available.", Toast.LENGTH_SHORT).show();
                openArticles();
            }
        });
        forumicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(), "Feature not yet available.", Toast.LENGTH_SHORT).show();
                openBodyTemp();
            }
        });
        locatoricon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPoop();
                //Toast.makeText(getActivity(), "Feature not yet available.", Toast.LENGTH_SHORT).show();
            }
        });
        aboutus2icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAboutUs();
                //Toast.makeText(getActivity(), "Feature not yet available.", Toast.LENGTH_SHORT).show();
            }
        });
        immunizationicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImmunization();
                //Toast.makeText(getActivity(), "Feature not yet available.", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

        public void openDiary(){
        Intent intent = new Intent(getActivity(), GrowthDiary.class);
        startActivity(intent);
    }
    public void openVitamin(){
    Intent intent = new Intent (getActivity(), AddVitReminder.class);
    startActivity(intent);
    }
    public void openArticles(){
        Intent intent = new Intent(getActivity(), Articless.class);
        startActivity(intent);
    }
    public void openPoop(){
        Intent intent = new Intent(getActivity(), Poop.class);
        startActivity(intent);
    }
    public void openBodyTemp(){
        Intent intent = new Intent(getActivity(), BodyTemp.class);
        startActivity(intent);
    }
    public void openGrowthCharts(){
        Intent intent = new Intent(getActivity(), GrowthCharts.class);
        startActivity(intent);
    }
    public void openAboutUs(){
        Intent intent = new Intent(getActivity(), AboutUs.class);
        startActivity(intent);
    }
    public void openImmunization(){
        Intent intent = new Intent(getActivity(), Immunization.class);
        startActivity(intent);
    }
}
