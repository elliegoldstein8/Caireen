package com.example.queenelizabethviii.caireen2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import android.text.format.DateFormat;
import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

   @Override
   public Dialog onCreateDialog(Bundle savedInstanceState){
       Calendar c = Calendar.getInstance();
       int hour = c.get(Calendar.HOUR_OF_DAY);
       int minute = c.get(Calendar.MINUTE);

       return new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener)getActivity(), hour, minute, DateFormat.is24HourFormat(getActivity()));
   }

   @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute){

   }
}
