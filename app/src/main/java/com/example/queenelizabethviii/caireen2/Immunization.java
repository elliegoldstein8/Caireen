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
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import java.util.Map;

import static android.view.View.VISIBLE;

public class Immunization extends AppCompatActivity {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(); //para maidentify kinsa na user sa firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();  //for storing data to firebase
    DatabaseReference myRef = database.getReference("Users").child(firebaseAuth.getCurrentUser().getUid()).child("baby"); //reference
    DatabaseReference lastlastref, lastref;
    String babyid, vaccine, timestamp;
    String imm,timeu,noteu;
    Immunization records;
    ListView listView;
    ArrayList<String> arrayList;
    ArrayAdapter<String> adapter;
    ProgressBar progressBar;
    ArrayList<String> keysList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaccine);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator_vaccine); //referencing loading icon

        arrayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, R.layout.rowlayout_immunization, R.id.labelimmunization, arrayList);
        listView = (ListView) findViewById(R.id.listvaccine);
        listView.setAdapter(adapter);


        populateList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                final String key = keysList.get(position);
                final DatabaseReference getRef = lastlastref.child(key);
                getRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        imm = dataSnapshot.child("vaccine").getValue(String.class);
                        timeu = dataSnapshot.child("date_time").getValue(String.class);
                        noteu = dataSnapshot.child("note").getValue(String.class);

                        getRef.keepSynced(true);

                        if (imm == null && timeu == null && noteu == null) {
                            //do nothing
                        } else {
                            AlertDialog.Builder adb = new AlertDialog.Builder(Immunization.this);
                            adb.setTitle(imm);
                            adb.setIcon(R.drawable.immunization);
                            adb.setMessage(timeu + "\n" + "Notes: \n" + noteu);
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

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public  boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder adb = new AlertDialog.Builder(Immunization.this);
                adb.setTitle("Delete this record?");
                adb.setIcon(R.drawable.ic_delete_black_24dp);
                adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                            String key = keysList.get(position);
                            lastlastref.child(key).removeValue();
                            Toast.makeText(Immunization.this, "Record deleted.", Toast.LENGTH_SHORT).show();
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


       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabvaccine);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVaccine();
            }
        });


    }


    public void addVaccine(){
        Intent intent = new Intent(this, AddVaccine.class);
        startActivity(intent);
    }// opens a new activity

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


        //arrayList

    }

    public void checkEmpty(){
        DatabaseReference test = lastref.child("baby_features");
        lastlastref = test.child("immunization_records");
        final TextView norecord = (TextView) findViewById(R.id.novaccine);
        test.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("immunization_records")){
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

                // note = dataSnapshot.child("note").getValue(String.class);
                vaccine = dataSnapshot.child("vaccine").getValue(String.class);
                timestamp = dataSnapshot.child("date_time").getValue(String.class);
                lastlastref.keepSynced(true);
                arrayList.add(vaccine + "\n" + timestamp);
                keysList.add(dataSnapshot.getKey());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                arrayList.remove(vaccine + "\n" +  timestamp);
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

    }




