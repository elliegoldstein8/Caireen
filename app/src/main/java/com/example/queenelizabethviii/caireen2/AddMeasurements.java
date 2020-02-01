package com.example.queenelizabethviii.caireen2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddMeasurements extends AppCompatActivity {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(); //para maidentify kinsa na user sa firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();  //for storing data to firebase
    DatabaseReference reRef =  database.getReference("Users").child(firebaseAuth.getCurrentUser().getUid());
    DatabaseReference myRef = reRef.child("baby"); //reference
    //DatabaseHelper helper = new DatabaseHelper(this);
    String timeStamp = new SimpleDateFormat("MM/dd/yy, h:mm a").format(new Date());
    DatabaseReference lastlastref, lastnajud;
    String babyid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_measurements);
        Button addmeasurement = (Button) findViewById(R.id.addmeasurement);
        TextView date_time = (TextView) findViewById(R.id.time_date2);

        date_time.setText(timeStamp);
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
                                lastnajud = myRef.child(babyid);
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

        addmeasurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRecord();
                addtoActivity();
            }
        });

    }
    public void addRecord(){
        EditText weight = (EditText) findViewById(R.id.weightinput);
        EditText height = (EditText) findViewById(R.id.heightinput);
        EditText headsize = (EditText) findViewById(R.id.headsizeinput);
        final String wey = weight.getText().toString();
        final String hi = height.getText().toString();
        final String head = headsize.getText().toString();

        if (wey.isEmpty() && hi.isEmpty() && head.isEmpty()){
            Toast.makeText(AddMeasurements.this, "You didn't enter anything.", Toast.LENGTH_LONG).show();
        }

        else {

            lastlastref = myRef.child(babyid).child("baby_features").child("growth_charts").push();

            if (!wey.equals("")) {
                lastlastref.child("weight").setValue(wey);
            }
            if (!hi.equals("")) {
                lastlastref.child("height").setValue(hi);
            }
            if (!head.equals("")) {
                lastlastref.child("head_circumference").setValue(head);
            }
            lastlastref.child("date_time").setValue(timeStamp);


            android.content.Intent intent = new Intent(this, GrowthCharts.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

    }

    public void addtoActivity(){
        DatabaseReference Refu = lastnajud.child("Activities").push();
        Refu.child("Activity").setValue("Growth Charts");
        Refu.child("Timestamp").setValue(timeStamp);
    }

}
