package com.example.queenelizabethviii.caireen2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class GrowthDiary extends AppCompatActivity {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(); //para maidentify kinsa na user sa firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();  //for storing data to firebase
    DatabaseReference myRef = database.getReference("Users").child(firebaseAuth.getCurrentUser().getUid()).child("baby"); //reference
    DatabaseReference lastlastref, lastref;
    String babyid, description, title, timestamp, img_path;
    String descriptionu, titleu, timestampu, img_pathu;
    ListView listView;
    ArrayList<String> arrayList;
    ArrayAdapter<String> adapter;
    ArrayList<String> keysList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_growth_diary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        arrayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, R.layout.rowlayout_diary, R.id.labeldiary, arrayList);
        listView = (ListView) findViewById(R.id.listdiary);
        listView.setAdapter(adapter);
        populateList();
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public  boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder adb = new AlertDialog.Builder(GrowthDiary.this);
                        adb.setTitle("Delete this record?");
                        adb.setIcon(R.drawable.ic_delete_black_24dp);
                        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String key = keysList.get(position);
                                lastlastref.child(key).removeValue();
                                Toast.makeText(GrowthDiary.this, "Record deleted.", Toast.LENGTH_SHORT).show();
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
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
                //setContentView(R.layout.activity_growth_diary_content);
                final String key = keysList.get(position);
                //lastlastref.child(key).child("diary_title")
                final DatabaseReference lastu = lastlastref.child(key);
                lastu.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        titleu = dataSnapshot.child("diary_title").getValue(String.class);
                        descriptionu = dataSnapshot.child("diary_description").getValue(String.class);
                        img_pathu = dataSnapshot.child("img_path").getValue(String.class);
                        timestampu = dataSnapshot.child("date_time").getValue(String.class);

                        lastu.keepSynced(true);

                        if (titleu != null && timestampu != null) {
                            AlertDialog.Builder adb = new AlertDialog.Builder(GrowthDiary.this);
                            adb.setTitle(titleu);
                            LayoutInflater factory = LayoutInflater.from(GrowthDiary.this);
                            final View view = factory.inflate(R.layout.activity_growth_diary_content, null);
                            final ImageView i = (ImageView) view.findViewById(R.id.img_pathu);


                            if (img_pathu != null) {
                                Picasso.get()
                                        .load(img_pathu)
                                        .into(i, new Callback() {
                                            @Override
                                            public void onSuccess() {

                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                Picasso.get()
                                                        .load(img_pathu)
                                                        .networkPolicy(NetworkPolicy.OFFLINE)
                                                        .into(i);
                                            }
                                        });
                            }
                            adb.setIcon(R.drawable.diary2);
                            adb.setView(view);
                            adb.setMessage(timestampu + "\n" + descriptionu);
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


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabdiary);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDiary();
            }
        });

    }


    public void addDiary(){
        Intent intent = new Intent(this, AddDiary.class);
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
        lastlastref = test.child("baby_diary");
        final TextView norecord = (TextView) findViewById(R.id.nodiary);
        test.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("baby_diary")){
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
                title = dataSnapshot.child("diary_title").getValue(String.class);
                description = dataSnapshot.child("diary_description").getValue(String.class);
                img_path = dataSnapshot.child("img_path").getValue(String.class);
                timestamp = dataSnapshot.child("date_time").getValue(String.class);

                lastlastref.keepSynced(true);
                arrayList.add(title + "\n" + timestamp); //wala pa niy picture
                keysList.add(dataSnapshot.getKey());
                adapter.notifyDataSetChanged();


                //  progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                arrayList.remove(title + "\n" + timestamp); //wala pa niy picture
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
