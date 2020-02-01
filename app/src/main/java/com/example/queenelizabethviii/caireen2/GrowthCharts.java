package com.example.queenelizabethviii.caireen2;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

public class GrowthCharts extends AppCompatActivity {

    private static final String TAG ="GrowthCharts";
    private SectionsPageAdapter mSectionPageAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_growth_charts);
        Log.d (TAG, "onCreate: Starting.");

        mSectionPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        //Ser up the ViewPager with the sections adapter
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs2);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMeasurements();
            }
        });

    }

    private void setupViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new growthchart_weight(), "Weight");
        adapter.addFragment(new growthchart_height(), "Height");
        adapter.addFragment(new growthcharts_headsize(), "Headsize");
        adapter.addFragment(new growthchart_bmi(), "BMI");
        viewPager.setAdapter(adapter);
    }

    public void addMeasurements(){
        Intent intent = new Intent(this, AddMeasurements.class);
        startActivity(intent);
    }

}
