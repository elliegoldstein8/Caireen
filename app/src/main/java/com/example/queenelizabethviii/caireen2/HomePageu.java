package com.example.queenelizabethviii.caireen2;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

public class HomePageu extends AppCompatActivity {
    private static final String TAG = "HomePageu";
    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    private FirebaseAuth firebaseAuth;
    @Override
    //syntax for creating tabs
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_pageu);
        firebaseAuth = FirebaseAuth.getInstance();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Caireen");
        setSupportActionBar(toolbar);
        Log.d(TAG,"onCreate: Starting.");

        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    //for the 3 fragments of homepage: features, home and profile tabs
    private void setupViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new homepage_features(), "Features");
        adapter.addFragment(new homepage_home(), "Home");
        adapter.addFragment(new homepage_profile(), "Profile");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.commonmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //for logout (TO BE EDITED)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout){
            firebaseAuth.signOut();
            android.content.Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
       else{
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }
/**
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
**/

}
