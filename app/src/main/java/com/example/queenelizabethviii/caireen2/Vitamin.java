package com.example.queenelizabethviii.caireen2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Vitamin extends AppCompatActivity {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(); //para maidentify kinsa na user sa firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();  //for storing data to firebase
    DatabaseReference myRef = database.getReference("Users").child(firebaseAuth.getCurrentUser().getUid()).child("baby"); //reference
    DatabaseReference lastlastref, lastref;
    ListView listView;
    ArrayList<String> arrayList;
    ArrayAdapter<String> adapter;
    String babyid, time, med, dosage, usage;
    String timeu, medu,dosageu,usageu;
    ArrayList<String> keysList = new ArrayList<>();
    AddVitamin vit = new AddVitamin();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vitamin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        arrayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, R.layout.rowlayout_vita, R.id.labelvitamin, arrayList);
        listView = (ListView) findViewById(R.id.list_vitamin);
        listView.setAdapter(adapter);


        populateList();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public  boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder adb = new AlertDialog.Builder(Vitamin.this);
                adb.setTitle("Delete this record?");
                adb.setIcon(R.drawable.ic_delete_black_24dp);
                adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String key = keysList.get(position);
                        lastlastref.child(key).removeValue();
                        Toast.makeText(Vitamin.this, "Record deleted.", Toast.LENGTH_SHORT).show();
                        cancelAlarm();
                    }
                });
                adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                adb.show();

                return true;
            }

        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                final String key = keysList.get(position);
                final DatabaseReference getRef = lastlastref.child(key);
                getRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        timeu = dataSnapshot.child("time").getValue(String.class);
                        usageu = dataSnapshot.child("usage").getValue(String.class);
                        dosageu = dataSnapshot.child("dosage").getValue(String.class);
                        medu = dataSnapshot.child("medicine").getValue(String.class);

                        getRef.keepSynced(true);

                        if (timeu != null && usageu != null && dosageu != null && medu != null) {

                            AlertDialog.Builder adb = new AlertDialog.Builder(Vitamin.this);
                            adb.setTitle(timeu);
                            adb.setIcon(R.drawable.vita);
                            adb.setMessage("Medicine: "+ medu + "\nDosage: " + dosageu + "\nUsage: " + usageu);
                            adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            adb.show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabvita);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVita();
            }
        });
    }
    public void addVita(){
        Intent intent = new Intent(this, AddVitamin.class);
        startActivity(intent);
    }

    public void populateList(){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot child : dataSnapshot.getChildren()){
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

                            lastlastref.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                    time = dataSnapshot.child("time").getValue(String.class);
                                    med = dataSnapshot.child("medicine").getValue(String.class);
                                    usage = dataSnapshot.child("usage").getValue(String.class);
                                    dosage = dataSnapshot.child("dosage").getValue(String.class);

                                    lastlastref.keepSynced(true);
                                    arrayList.add(time + "    ON"); //tanggala ang usage
                                    keysList.add(dataSnapshot.getKey());
                                    adapter.notifyDataSetChanged();

                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                }

                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {
                                    arrayList.remove(time); //tanggala ang usage
                                    keysList.remove(dataSnapshot.getKey());
                                    adapter.notifyDataSetChanged();
                                    lastlastref.keepSynced(true);
                                }

                                @Override
                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

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
