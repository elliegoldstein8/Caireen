package com.example.queenelizabethviii.caireen2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

public class Poop extends AppCompatActivity {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(); //para maidentify kinsa na user sa firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();  //for storing data to firebase
    DatabaseReference myRef = database.getReference("Users").child(firebaseAuth.getCurrentUser().getUid()).child("baby"); //reference
    DatabaseReference lastlastref, lastref;
    String babyid, note, poop_consistency, poop_color, timestamp;
    String poopucon, poopucol, timeu, noteu;
    ListView listView;
    ArrayList<String> arrayList;
    ArrayAdapter<String> adapter;
    ArrayList<String> keysList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poop);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        arrayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, R.layout.rowlayout_poop, R.id.labelpoop, arrayList);
        listView = (ListView) findViewById(R.id.listvaccine2);
        listView.setAdapter(adapter);
        populateList();
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public  boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder adb = new AlertDialog.Builder(Poop.this);
                adb.setTitle("Delete this record?");
                adb.setIcon(R.drawable.ic_delete_black_24dp);
                adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String key = keysList.get(position);
                        lastlastref.child(key).removeValue();
                        Toast.makeText(Poop.this, "Record deleted.", Toast.LENGTH_SHORT).show();
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
                        poopucon = dataSnapshot.child("poop_consistency").getValue(String.class);
                        poopucol = dataSnapshot.child("poop_color").getValue(String.class);
                        timeu = dataSnapshot.child("date_time").getValue(String.class);
                        noteu = dataSnapshot.child("note").getValue(String.class);

                        getRef.keepSynced(true);

                        if (poopucol != null && poopucon != null && timeu != null && noteu != null) {

                            AlertDialog.Builder adb = new AlertDialog.Builder(Poop.this);
                            adb.setTitle(poopucon + ", " + poopucol + " Poop");
                            adb.setIcon(R.drawable.poop2);
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


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabpoop);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              addPoop();
            }
        });

    }
    public void addPoop(){
        Intent intent = new Intent(this, AddPoop.class);
        startActivity(intent);
    }//opens new activity

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
        lastlastref = test.child("poop_records");
        final TextView norecord = (TextView) findViewById(R.id.nopoop);
        test.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("poop_records")){
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

                note = dataSnapshot.child("note").getValue(String.class);
                poop_color = dataSnapshot.child("poop_color").getValue(String.class);
                poop_consistency = dataSnapshot.child("poop_consistency").getValue(String.class);
                timestamp = dataSnapshot.child("date_time").getValue(String.class);


                arrayList.add(poop_consistency + "\n" + poop_color + "\n" + timestamp);
                keysList.add(dataSnapshot.getKey());
                adapter.notifyDataSetChanged();
                lastlastref.keepSynced(true);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                arrayList.remove(poop_consistency + "\n" + poop_color + "\n" + timestamp);
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
