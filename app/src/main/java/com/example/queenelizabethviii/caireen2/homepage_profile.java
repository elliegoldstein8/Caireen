package com.example.queenelizabethviii.caireen2;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.Weeks;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Queen Elizabeth VIII on 4/18/2018.
 */

public class homepage_profile extends Fragment {
    private static final String TAG = "homepageprofile";
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(); //para maidentify kinsa na user sa firebase
    StorageReference mStorageRef;
    FirebaseDatabase database = FirebaseDatabase.getInstance();  //for storing data to firebase
    FirebaseStorage storage = FirebaseStorage.getInstance(); //for storing files sa firebase
    DatabaseReference myRef = database.getReference("Users").child(firebaseAuth.getCurrentUser().getUid()).child("baby"); //reference
    String babyid, babybday;
    TextView babyagetxt;
    DatabaseReference lastlastref;
    int years, months, days;
    CircleImageView imageview;
    String message;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.homepage_profile, container, false);

       // FloatingActionButton button = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
        final TextView babynametxt = (TextView) view.findViewById(R.id.babynamestring);
        final TextView babybdaytxt = (TextView) view.findViewById(R.id.babybdaystring);
        babyagetxt = (TextView) view.findViewById(R.id.babyagestring);
        final TextView babygendertxt = (TextView) view.findViewById(R.id.babygenderstring);
        imageview = (CircleImageView) view.findViewById(R.id.imageButton2);


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                // Log.d(TAG, "Value is: " + value);
                for (final DataSnapshot child : dataSnapshot.getChildren()) {
                    // Log.i("TAG", "child key = " + child.getKey());
                    String key = child.getKey();
                    //String babyname = dataSnapshot.getValue(String.class);
                    final DatabaseReference newRef = myRef.child(key);
                    final ValueEventListener valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String babyname = child.child("baby_name").getValue(String.class);
                            babybday = child.child("baby_bday").getValue(String.class);
                            String babygender = child.child("baby_gender").getValue(String.class);
                            message = child.child("img_path").getValue(String.class);
                            //  final Context ctx;
                            babyid = child.child("baby_id").getValue(String.class);
                            myRef.keepSynced(true);

                            babynametxt.setText(babyname);
                            babybdaytxt.setText(babybday);
                            babygendertxt.setText(babygender);
                            if (babybday != null) {
                                calAge();
                            }
                            if (babyid != null) {
                                lastlastref = myRef.child(babyid);
                                //                        calMonths();
                                lastlastref.keepSynced(true);
                            }
                            if (message != null) {
                                Picasso.get()
                                        .load(message)
                                        .into(imageview, new Callback() {
                                            @Override
                                            public void onSuccess() {

                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                Picasso.get()
                                                        .load(message)
                                                        .networkPolicy(NetworkPolicy.OFFLINE)
                                                        .into(imageview);
                                            }
                                        });

                            }
                            else{
                                //do nothing
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    };
                    newRef.addListenerForSingleValueEvent(valueEventListener);
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });




        return view;

    }



    public void calAge(){
        Date date1 = null;
        try {
            date1 = new SimpleDateFormat("M/dd/yyyy").parse(babybday);
        } catch (ParseException e) {
            e.printStackTrace();
        }

      LocalDate birthdate = new LocalDate(date1);
      LocalDate now = new LocalDate();

      Period period = new Period(birthdate, now, PeriodType.yearMonthDay());

      years = period.getYears();
      months = period.getMonths();
      days = period.getDays();

      String age = years + "yrs, " + months + "mos & " + days + "days";

      babyagetxt.setText(age);

    }






}
