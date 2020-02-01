package com.example.queenelizabethviii.caireen2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import static android.view.View.VISIBLE;

public class AddVaccine extends AppCompatActivity {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(); //para maidentify kinsa na user sa firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();  //for storing data to firebase
    DatabaseReference reRef = database.getReference("Users").child(firebaseAuth.getCurrentUser().getUid());
    DatabaseReference myRef = reRef.child("baby"); //reference
    String timeStamp = new SimpleDateFormat("MM/dd/yy, h:mm a").format(new Date());
    DatabaseReference lastlastref, lastnajud;
    String babyid, vaccine;
    int pos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vaccine);

        Button add = (Button) findViewById(R.id.addvaccine);
        final EditText others = (EditText) findViewById(R.id.editTextothers);
        ;

        TextView date_time = (TextView) findViewById(R.id.time_date4);
        date_time.setText(timeStamp);

        Spinner spinner = (Spinner) findViewById(R.id.spinner); //referencing spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.vaccine_names,android.R.layout.simple_spinner_item); //dropdown lists of vaccine names from strings.xml
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vaccine = parent.getItemAtPosition(position).toString();
                pos = position;
                //Toast.makeText(getBaseContext(), parent.getItemAtPosition(position)+ " Selected", Toast.LENGTH_LONG).show();
            } //when an item is selected

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //for referencing child from firebase
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

        //onclicklistener
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pos == 0){
                    Toast.makeText(AddVaccine.this, "Please select a vaccine.", Toast.LENGTH_LONG).show();
                }
                else {
                    addRecord();
                    addtoActivity();
                }
            }
        });

    }

    public void addRecord(){
        EditText note = (EditText) findViewById(R.id.notevaccine);
        String others;
        final String notes = note.getText().toString();
        lastlastref = lastnajud.child("baby_features").child("immunization_records").push();
        if (vaccine.equals("Others")){
            EditText othersu = (EditText) findViewById(R.id.editTextothers);
            others = othersu.getText().toString();
            if (others.isEmpty()){
                Toast.makeText(AddVaccine.this, "Atleast put the vaccine name please.", Toast.LENGTH_LONG).show();
            }
            else {
                lastlastref.child("note").setValue(notes);
                lastlastref.child("vaccine").setValue(others);
                lastlastref.child("date_time").setValue(timeStamp);
            }
        }
        else{
            lastlastref.child("note").setValue(notes);
            lastlastref.child("vaccine").setValue(vaccine);
            lastlastref.child("date_time").setValue(timeStamp);
        }

       // lastlastref.child("imm_id").setValue(lastlastref.getKey());
        //finish();
        android.content.Intent intent = new Intent(this, Immunization.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void addtoActivity(){
        DatabaseReference Refu = lastnajud.child("Activities").push();
        Refu.child("Activity").setValue("Immunization");
        Refu.child("Timestamp").setValue(timeStamp);
    }

}
