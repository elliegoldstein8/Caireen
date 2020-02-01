package com.example.queenelizabethviii.caireen2;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Queen Elizabeth VIII on 4/18/2018.
 */

public class homepage_home extends Fragment {
    private static final String TAG = "homepagehome";
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(); //para maidentify kinsa na user sa firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();  //for storing data to firebase
    DatabaseReference reRef = database.getReference("Users").child(firebaseAuth.getCurrentUser().getUid()).child("baby");
    ListView listView;
    ArrayList<String> arrayList;
    ArrayAdapter<String> adapter;
    String activity, time;
    String babyid;
    DatabaseReference lastlastref;
    TextView text;
    int count = 0;
    Button butOne;
    Button butTwo;
    String[] content = new String[] {
            "Human babies are the only primates who smile at their parents.",
            "A baby is born in the world every three seconds.",
            "An average baby will go through approximately 2,700 diapers a year",
            "The word “baby” may come from a baby’s babbling, as in “ba-ba-ba-ba.”",
            "A baby will eat an estimated 15 pounds of cereal per year.",
            "In Medieval Europe, unruly babies were thought to be the result of inferior breast milk.",
            "Research indicates that a baby’s name influences a baby’s life into adulthood. For example, a newborn boy with a more “feminine” name could lead to behavioral problems in life.",
            "Most newborns will lose all the hair they are born with in the first three or four months of life.",
            "A baby’s first social smile appears between four and six weeks after birth.",
            "A baby can recognize the smell and voice of its mother at birth. It takes a few weeks before a baby can see the difference between its mother and other adults.",
            "A newborn urinates about every 20 minutes and then roughly every hour at 6 months.",
            "Your baby has a specific cry that you can recognize just three days after birth.",
            "In your baby's first three months of life, he can only see things that are eight or nine inches away.",
            "Babies are always listening, even before they're born. Even a baby as young as two days old will recognize his mother's voice, even if he only hears one single syllable.",
            "Your baby's brain will double in the first year of life. The brain at one-year-old is half the size of an adult brain."
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.homepageu_home, container,false);
        arrayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(view.getContext(), R.layout.rowlayout_home, R.id.labelactivity, arrayList);
        listView = (ListView) view.findViewById(R.id.listviewactivities);
        listView.setAdapter(adapter);
        reRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                // Log.d(TAG, "Value is: " + value);
                for (final DataSnapshot child : dataSnapshot.getChildren()) {
                    // Log.i("TAG", "child key = " + child.getKey());
                    String key = child.getKey();
                    //String babyname = dataSnapshot.getValue(String.class);
                    final DatabaseReference newRef = reRef.child(key);
                    final ValueEventListener valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            babyid = child.child("baby_id").getValue(String.class);
                            if (babyid != null) {
                                lastlastref = reRef.child(babyid).child("Activities");
                                populateList();
                            }
                            //lastlastref = newRef.child("Activities");
                                //                        calMonths();
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

        text = view.findViewById(R.id.textView12);
        text.setText(content[count]);

        butTwo = view.findViewById(R.id.button5);
        butOne = view.findViewById(R.id.button6);
        butOne.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (count < 14) { //4 imung gireplace dri
                    count++;
                    text.setText(content[count]);
                }
            }
        });
        butTwo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (count > 0) {
                    count--;
                    text.setText(content[count]);
                }
            }
        });

        return view;

    }




    public void populateList(){
        lastlastref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                activity = dataSnapshot.child("Activity").getValue(String.class);
                time = dataSnapshot.child("Timestamp").getValue(String.class);
                arrayList.add(activity + "\n" + time);
                Collections.reverse(arrayList);
                adapter.notifyDataSetChanged();
                lastlastref.keepSynced(true);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

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
