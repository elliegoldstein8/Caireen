package com.example.queenelizabethviii.caireen2;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;


public class AddVitamin extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {


    private Object manager;
    Button done;
    EditText babyname, medicines, dosage, usage, time;
    String babyid;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(); //para maidentify kinsa na user sa firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();  //for storing data to firebase
    DatabaseReference reRef = database.getReference("Users").child(firebaseAuth.getCurrentUser().getUid());
    DatabaseReference myRef = reRef.child("baby"); //reference
    String timeStamp = new SimpleDateFormat("MM/dd/yy, h:mm a").format(new Date());
    DatabaseReference lastnajud;
    Calendar c = Calendar.getInstance();
   // AlarmManager alarmManager;
   AlarmManager alarmManager;
    Intent intent;
    PendingIntent pendingIntent;
    Switch aSwitch;
    String switchu = "No Repeat";
    int hours;
    TextView repeatuu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vitamin);

        time = findViewById(R.id.firstname);
        aSwitch = findViewById(R.id.switch1);
        Button buttonTimePicker = findViewById(R.id.button_timepicker);
        repeatuu = findViewById(R.id.repeatimongnawong);

        buttonTimePicker.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getFragmentManager(), "TimePicker");
            }
        });

        done = findViewById(R.id.signin);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAlarm(c);
                openVitamin();
                addtoActivity();
            }
        });

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

    }

    @Override
    public void onTimeSet(TimePicker view, int hoursOfDay, int minute){

        c.set(Calendar.HOUR_OF_DAY, hoursOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        updateTimeText(c);

    }

    private void updateTimeText(Calendar c){
        String timeText = " ";
        timeText += DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());

        time.setText(timeText);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void startAlarm(Calendar c){
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(getApplicationContext(), MyBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, intent, FLAG_UPDATE_CURRENT);
        if(c.before(Calendar.getInstance())){
            c.add(Calendar.DATE, 1);
        }

      if (switchu=="Repeat Daily"){
              alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
          alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS), pendingIntent);
      }
      else if (switchu == "Specify no. of hours"){
          alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
          alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), TimeUnit.MILLISECONDS.convert(hours, TimeUnit.HOURS), pendingIntent);
        }
        else{
              alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
      }

    }


    public void openVitamin(){
        time = findViewById(R.id.firstname);
        medicines = findViewById(R.id.lastname);
        dosage = findViewById(R.id.email);
        usage = findViewById(R.id.password);

        String medicinestr = medicines.getText().toString();
        String dosagestr = dosage.getText().toString();
        String usagestr = usage.getText().toString();
        String timestr = time.getText().toString();

        if (timestr.isEmpty() && medicinestr.isEmpty()&& dosagestr.isEmpty() && usagestr.isEmpty()){
            Toast vita = Toast.makeText(AddVitamin.this, "You didn't put anything.", Toast.LENGTH_SHORT);
            vita.show();
        } //nganung magerror man dri wtf
        else{
        DatabaseReference  lastlastref = myRef.child(babyid).child("baby_features").child("med_reminder");

            lastlastref.child("medicine").setValue(medicinestr);
            lastlastref.child("dosage").setValue(dosagestr);
            lastlastref.child("usage").setValue(usagestr);
            lastlastref.child("time").setValue(timestr);
            if (switchu=="Repeat Daily"){
                lastlastref.child("repeat").setValue(switchu);
            }
            else if (switchu == "No Repeat"){
                lastlastref.child("repeat").setValue("No Repeat");
            }
            else{
                lastlastref.child("repeat").setValue(Integer.toString(hours));
            }



            android.content.Intent intent = new Intent(this, AddVitReminder.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

    }

    public void addtoActivity(){
        DatabaseReference Refu = lastnajud.child("Activities").push();
        Refu.child("Activity").setValue("Vitamin Reminder");
        Refu.child("Timestamp").setValue(timeStamp);

    }

    public void repeater(View view){
        final CharSequence[] items={"Repeat Daily","Specify no. of hours", "No Repeat"};

        AlertDialog.Builder builder = new AlertDialog.Builder(AddVitamin.this);
        builder.setTitle("Repeat");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(items[i].equals("Repeat Daily")) {
                 //   ActivityCompat.requestPermissions(BabyRegister.this,new String[]{ android.Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_STORAGE);
                    switchu = "Repeat Daily";
                    repeatuu.setText("Repeat Daily");
                }else if(items[i].equals("No Repeat")) {
                    switchu = "No Repeat";
                    dialogInterface.dismiss();
                    repeatuu.setText("No Repeat");
                }
                else if(items[i].equals("Specify no. of hours")) {
                   // ActivityCompat.requestPermissions(BabyRegister.this,new String[]{Manifest.permission.CAMERA},PERMISSION_CAMERA);
                    //mangayog permission for camera
                    dialogInterface.dismiss();

                    AlertDialog.Builder build = new AlertDialog.Builder(AddVitamin.this);
                    build.setTitle("Input the no. of hours");
                    // Set up the input
                    final EditText input = new EditText(AddVitamin.this);
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    build.setView(input);

                    build.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switchu = "Specify no. of hours";
                            hours = Integer.parseInt(input.getText().toString());
                            repeatuu.setText("Repeat every " + hours + " hour(s)");
                        }
                    });

                    build.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    build.show();

                }
            }
        });
        builder.show();
    }
}

