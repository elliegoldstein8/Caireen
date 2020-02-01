package com.example.queenelizabethviii.caireen2;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class AddBodyTemp extends AppCompatActivity {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(); //para maidentify kinsa na user sa firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();  //for storing data to firebase
    DatabaseReference reRef = database.getReference("Users").child(firebaseAuth.getCurrentUser().getUid());
    DatabaseReference myRef = reRef.child("baby"); //reference
    String timeStamp = new SimpleDateFormat("MM/dd/yy, h:mm a").format(new Date());
    DatabaseReference lastlastref, lastnajud;
    String babyid;
    EditText celsius;
    TextInputEditText note;
    String celsiuss,notes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_body_temp);
        Button add = (Button) findViewById(R.id.buttonbodytemp);
        TextView date_time = (TextView) findViewById(R.id.time_date5);
        date_time.setText(timeStamp);
        celsius = (EditText) findViewById(R.id.bodytemp);
        note = (TextInputEditText) findViewById(R.id.notebodytemp);


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

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                celsiuss = celsius.getText().toString();
                notes = note.getText().toString();
                if (celsiuss.isEmpty() && notes.isEmpty()){
                    Toast.makeText(AddBodyTemp.this, "You didn't put anything.", Toast.LENGTH_LONG).show();
                }
                else {
                    addRecord();
                    addtoActivity();
                }
            }
        });

    }

    public void addRecord(){
        celsiuss = celsius.getText().toString();
        notes = note.getText().toString();
            lastlastref = myRef.child(babyid).child("baby_features").child("body_temp_records").push();
            lastlastref.child("bodytemp_celsius").setValue(celsiuss);
            lastlastref.child("notes").setValue(notes);
            lastlastref.child("date_time").setValue(timeStamp);

            android.content.Intent intent = new Intent(this, BodyTemp.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
    }

    public void addtoActivity(){
        DatabaseReference Refu = lastnajud.child("Activities").push();
        Refu.child("Activity").setValue("Body Temperature");
        Refu.child("Timestamp").setValue(timeStamp);
    }
}
