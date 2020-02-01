package com.example.queenelizabethviii.caireen2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class AboutUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /** Snackbar.make(view, "Add us on Facebook~! LOL", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();**/
              openFacebookPage();
            }
        });
    }

    public void openFacebookPage(){
        Uri uri = Uri.parse("https://www.facebook.com/Caireen-Baby-Monitoring-System-174433816691239/?ref=br_rs"); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
