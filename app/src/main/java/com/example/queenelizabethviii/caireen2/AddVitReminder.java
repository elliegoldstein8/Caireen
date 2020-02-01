package com.example.queenelizabethviii.caireen2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class AddVitReminder extends AppCompatActivity {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(); //para maidentify kinsa na user sa firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();  //for storing data to firebase
    DatabaseReference myRef = database.getReference("Users").child(firebaseAuth.getCurrentUser().getUid()).child("baby"); //reference
    DatabaseReference lastlastref, lastref;
    String babyid, time, med, dosage, usage, repeatu;
    String timeu, medu,dosageu,usageu;
    FloatingActionButton fab, fabdelete;
    Uri notif;
    Ringtone r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vit_reminder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        displayData();

        fab = (FloatingActionButton) findViewById(R.id.fabaddvitrem);
        fabdelete = (FloatingActionButton) findViewById(R.id.fabdelete);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVita();
            }
        });


        fabdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder adb = new AlertDialog.Builder(AddVitReminder.this);
                adb.setTitle("Delete this record?");
                adb.setIcon(R.drawable.ic_delete_black_24dp);
                adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(AddVitReminder.this, "Record deleted.", Toast.LENGTH_SHORT).show();
                        cancelAlarm();
                        deleteData();
                        //stopAlarm();
                    }
                });
                adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                adb.show();
            }
        });
    }

    public void addVita(){
        Intent intent = new Intent(this, AddVitamin.class);
        startActivity(intent);
    }

    public void displayData(){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot child : dataSnapshot.getChildren()) {
                    String key = child.getKey();
                    final DatabaseReference newRef = myRef.child(key);
                    final ValueEventListener valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            babyid = child.child("baby_id").getValue(String.class);
                            if (babyid != null) {
                                lastref = myRef.child(babyid);
                            }

                            lastlastref = lastref.child("baby_features").child("med_reminder");
                            lastlastref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    time = dataSnapshot.child("time").getValue(String.class);
                                    med = dataSnapshot.child("medicine").getValue(String.class);
                                    usage = dataSnapshot.child("usage").getValue(String.class);
                                    dosage = dataSnapshot.child("dosage").getValue(String.class);
                                    repeatu = dataSnapshot.child("repeat").getValue(String.class);

                                    lastlastref.keepSynced(true);


                                    TextView timestring = (TextView) findViewById(R.id.timedisplay);
                                    TextView medstring = (TextView) findViewById(R.id.medicinedisplay);
                                    TextView usagestring = (TextView) findViewById(R.id.usagedisplay);
                                    TextView dosagestring = (TextView) findViewById(R.id.dosagedisplay);
                                    TextView repeat = (TextView) findViewById(R.id.repeatdaily);

                                    if (time == null && med == null && usage == null && dosage == null ) {
                                        //do nothing
                                        timestring.setText("No reminder set.");
                                        timestring.setVisibility(VISIBLE);
                                        medstring.setVisibility(GONE);
                                        usagestring.setVisibility(GONE);
                                        dosagestring.setVisibility(GONE);
                                        fabdelete.setVisibility(GONE);
                                        repeat.setVisibility(GONE);
                                        fab.setVisibility(VISIBLE);

                                    }
                                    else{
                                        timestring.setText(time);
                                        medstring.setText("Medicine: "+ med);
                                        usagestring.setText("Usage: " + usage);
                                        dosagestring.setText("Dosage: " + dosage);
                                        if (repeatu == "Repeat Daily"){
                                            repeat.setText(repeatu);
                                        }
                                        else if (repeatu == "No Repeat"){
                                            repeat.setText(repeatu);
                                        }
                                        else{
                                            repeat.setText("Repeat every " + repeatu + " hour(s)" );
                                        }
                                        fab.setVisibility(GONE);
                                        fabdelete.setVisibility(VISIBLE);
                                        timestring.setVisibility(VISIBLE);
                                        medstring.setVisibility(VISIBLE);
                                        usagestring.setVisibility(VISIBLE);
                                        dosagestring.setVisibility(VISIBLE);
                                        repeat.setVisibility(VISIBLE);

                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                    newRef.addListenerForSingleValueEvent(valueEventListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void deleteData(){
        lastlastref.removeValue();
    }

    public void cancelAlarm(){
        // alarmManager.cancel(pendingIntent);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), 1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);
    }



}
