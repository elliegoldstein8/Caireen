package com.example.queenelizabethviii.caireen2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class AddPoop extends AppCompatActivity {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(); //para maidentify kinsa na user sa firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();  //for storing data to firebase
    DatabaseReference reRef = database.getReference("Users").child(firebaseAuth.getCurrentUser().getUid());
    DatabaseReference myRef = reRef.child("baby"); //reference
    String timeStamp = new SimpleDateFormat("MM/dd/yy, h:mm a").format(new Date());
    DatabaseReference lastlastref, lastnajud;
    String babyid;
    String poop_consistency, poop_color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_poop);

        TextView date_time = (TextView) findViewById(R.id.time_date3);
        Button add = (Button) findViewById(R.id.addpoop);

        date_time.setText(timeStamp);

        //for poop consistency spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinner1); //referencing spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.poop_consistencies,android.R.layout.simple_spinner_item); //dropdown lists of poop consistencies from strings.xml
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                poop_consistency = parent.getItemAtPosition(position).toString();
                //Toast.makeText(getBaseContext(), parent.getItemAtPosition(position)+ " Selected", Toast.LENGTH_LONG).show();
            } //when an item is selected

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //for poop color spinner
        Spinner spinnerr = (Spinner) findViewById(R.id.spinner2); //referencing spinner
        ArrayAdapter<CharSequence> adapterr = ArrayAdapter.createFromResource(this,R.array.poop_colors,android.R.layout.simple_spinner_item); //dropdown lists of poop colors from strings.xml
        adapterr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerr.setAdapter(adapterr);
        spinnerr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                poop_color = parent.getItemAtPosition(position).toString();
                //Toast.makeText(getBaseContext(), parent.getItemAtPosition(position)+ " Selected", Toast.LENGTH_LONG).show();
            } //when an item is selected

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    //for referencing from firebase
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

        //for onclicklistener
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (poop_color.equals("Select Poop Color") || poop_consistency.equals("Select Poop Consistency")){
                    Toast.makeText(AddPoop.this, "Please complete details first.", Toast.LENGTH_LONG).show();
                }
                else{
                addRecord();
                addtoActivity();
                }
            }
        });


    }

    public void addRecord(){
        EditText note = (EditText) findViewById(R.id.notepoop);
        final String notes = note.getText().toString();

        lastlastref = myRef.child(babyid).child("baby_features").child("poop_records").push();
        lastlastref.child("poop_consistency").setValue(poop_consistency);
        lastlastref.child("poop_color").setValue(poop_color);
        lastlastref.child("note").setValue(notes);
        lastlastref.child("date_time").setValue(timeStamp);

        android.content.Intent intent = new Intent(this, Poop.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void addtoActivity(){
        DatabaseReference Refu = lastnajud.child("Activities").push();
        Refu.child("Activity").setValue("Poop");
        Refu.child("Timestamp").setValue(timeStamp);
    }
}
