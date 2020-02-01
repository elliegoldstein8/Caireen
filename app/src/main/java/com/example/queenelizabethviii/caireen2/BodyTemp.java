package com.example.queenelizabethviii.caireen2;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BodyTemp extends AppCompatActivity {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(); //para maidentify kinsa na user sa firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();  //Aa to firebase
    DatabaseReference myRef = database.getReference("Users").child(firebaseAuth.getCurrentUser().getUid()).child("baby"); //reference
    DatabaseReference lastlastref, lastref;
    String babyid, bodytemp, note, timestamp;
    String b, n, t;
    ListView listView;
    ArrayList<String> arrayList;
    ArrayAdapter<String> adapter;
    ArrayList<String> keysList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_temp);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); //referencing toolbar
        setSupportActionBar(toolbar);

        arrayList = new ArrayList<String>();

        adapter = new ArrayAdapter<String>(this, R.layout.rowlayout, R.id.label, arrayList);
        listView = (ListView) findViewById(R.id.listvaccine4);
        listView.setAdapter(adapter);

        populateList();


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public  boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder adb = new AlertDialog.Builder(BodyTemp.this);
                adb.setTitle("Delete this record?");
                adb.setIcon(R.drawable.ic_delete_black_24dp);
                adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String key = keysList.get(position);
                        lastlastref.child(key).removeValue();
                        Toast.makeText(BodyTemp.this, "Record deleted.", Toast.LENGTH_SHORT).show();
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
                        b = dataSnapshot.child("bodytemp_celsius").getValue(String.class);
                        t = dataSnapshot.child("date_time").getValue(String.class);
                        n = dataSnapshot.child("notes").getValue(String.class);

                        getRef.keepSynced(true);

                        if (b != null && t != null && n != null) {
                            AlertDialog.Builder adb = new AlertDialog.Builder(BodyTemp.this);
                            adb.setTitle(b + " °C");
                            adb.setIcon(R.drawable.bodytemp2);
                            adb.setMessage(t + "\n" + "Notes: \n" + n);
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabbody); //referencing floating action button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBodyTempRecord();
            }
        });

    }

    //opena another activity
    public void openBodyTempRecord(){
        Intent intent = new Intent(this, AddBodyTemp.class);
        startActivity(intent);
    }

    public void populateList(){
        // progressBar.setVisibility(View.VISIBLE);
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
                                checkEmpty();
                                checkData();
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
            public void onCancelled(DatabaseError databaseError) {
            }

        });



    }

    public void checkEmpty(){
        DatabaseReference test = lastref.child("baby_features");
        lastlastref = test.child("body_temp_records");
        final TextView norecord = (TextView) findViewById(R.id.nobodytemp);
        test.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("body_temp_records")){
                    norecord.setVisibility(View.GONE);
                }
                else{
                    norecord.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void checkData(){
        lastlastref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                note = dataSnapshot.child("notes").getValue(String.class);
                bodytemp = dataSnapshot.child("bodytemp_celsius").getValue(String.class);
                timestamp = dataSnapshot.child("date_time").getValue(String.class);
                lastlastref.keepSynced(true);
                arrayList.add(bodytemp + " °C\n" +  timestamp);
                keysList.add(dataSnapshot.getKey());
                adapter.notifyDataSetChanged();
                //  progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                arrayList.remove(bodytemp + " °C\n" +  timestamp);
                keysList.remove(dataSnapshot.getKey());
               // adapter.notifyDataSetInvalidated();
               // newAdapter();
                adapter.notifyDataSetChanged();
               // listView.refreshDrawableState();
               // listView.invalidateViews();
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

    public void newAdapter(){
     //  adapter = new ArrayAdapter<String>(this, R.layout.rowlayout, R.id.label, arrayList);
        listView.setAdapter(new ArrayAdapter<String>(this, R.layout.rowlayout, R.id.label, arrayList));
      // adapter.notifyDataSetChanged();
    }
}
