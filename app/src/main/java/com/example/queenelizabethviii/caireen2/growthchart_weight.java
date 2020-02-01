package com.example.queenelizabethviii.caireen2;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

public class growthchart_weight extends Fragment {
    private static final String TAG="growthchart_weight";
    double weight;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(); //para maidentify kinsa na user sa firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();  //for storing data to firebase
    DatabaseReference myRef = database.getReference("Users").child(firebaseAuth.getCurrentUser().getUid()).child("baby"); //reference
    DatabaseReference lastlastref, lastnagid, parasaheight;
    String babyid, babygender, date, babybday;
    boolean gender = true;
    ArrayAdapter<String> adapter;
    GraphView graph;
    int months;
    LineGraphSeries<DataPoint> seriesheight = new LineGraphSeries();//fishy

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.growthchart_weight,container,false);

        graph = (GraphView) view.findViewById(R.id.graphweight);
        // set manual X bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(25);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(60);

        // enable scaling and scrolling
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
        //legends
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        getGenderAgeHeight();
        if (!gender){
            displayPercentilesforGirls();
        }
        else{
            displayPercentilesforBoys();
        }

        graph.addSeries(seriesheight);
        seriesheight.setColor(Color.rgb(0,0,255));
        seriesheight.setThickness(6);
        seriesheight.setTitle("Weight");
        seriesheight.setDrawDataPoints(true);
        seriesheight.setDataPointsRadius(8);
        seriesheight.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
             /*&*   AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                adb.setTitle(dataPoint.getX() + " months and "+ dataPoint.getY()+ "kg weight");
                adb.setIcon(R.drawable.ic_info_outline_black_24dp);
                adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                adb.show();
**/
               Toast.makeText(getActivity(), dataPoint.getX() + " months and "+ dataPoint.getY()+ "kg weight", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }


    public void getGenderAgeHeight(){
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
                            babygender = child.child("baby_gender").getValue(String.class);
                            String monthseu = dataSnapshot.child("age_in_months").getValue(String.class);
                            months = Integer.parseInt(monthseu);

                            if (babyid != null) {
                                if (babygender.equals("Female")){
                                    gender = false;
                                }
                                else{
                                    gender = true;
                                }
                                parasaheight = myRef.child(babyid);
                                lastlastref = parasaheight.child("baby_features").child("growth_charts");

                                lastlastref.addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                        String weightu = dataSnapshot.child("weight").getValue(String.class);
                                        date = dataSnapshot.child("time_stamp").getValue(String.class);
                                        if (weightu!=null){
                                            weight = Double.parseDouble(weightu);
                                            seriesheight.appendData(  new DataPoint(months,weight), true, 100);
                                            lastlastref.keepSynced(true);
                                        }
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
        // populateList();
    }


    public void displayPercentilesforBoys(){
        /**FOR BOYS PA LANG NI SYA**/
        //3rd percentile
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0,2.5),
                new DataPoint(2, 4.4),
                new DataPoint(4, 5.6),
                new DataPoint(6, 6.4),
                new DataPoint(8, 7.0),
                new DataPoint(10,7.5),
                new DataPoint(12, 7.8),
                new DataPoint(14, 8.2),
                new DataPoint(16, 8.5),
                new DataPoint(18, 8.9),
                new DataPoint(20,9.2),
                new DataPoint(22, 9.5),
                new DataPoint(24, 9.8),
                new DataPoint(26, 10.1),
                new DataPoint(28, 10.4),
                new DataPoint(30,10.7),
                new DataPoint(32, 10.9),
                new DataPoint(34, 11.2),
                new DataPoint(36, 11.4),
                new DataPoint(38, 11.7),
                new DataPoint(40,11.9),
                new DataPoint(42, 12.2),
                new DataPoint(44, 12.4),
                new DataPoint(46, 12.7),
                new DataPoint(48, 12.9),
                new DataPoint(50,13.1),
                new DataPoint(52, 13.4),
                new DataPoint(54, 13.6),
                new DataPoint(56, 13.8),
                new DataPoint(58, 14.1),
                new DataPoint(60, 14.3)
        });
        graph.addSeries(series);
        series.setColor(Color.rgb(226,91,34));
        series.setThickness(3);
        series.setTitle("3rd");


        //15th percentile
        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0,2.9),
                new DataPoint(2, 4.9),
                new DataPoint(4, 6.2),
                new DataPoint(6, 7.1),
                new DataPoint(8, 7.7),
                new DataPoint(10,8.2),
                new DataPoint(12, 8.6),
                new DataPoint(14, 9.0),
                new DataPoint(16, 9.4),
                new DataPoint(18, 9.7),
                new DataPoint(20,10.1),
                new DataPoint(22, 10.5),
                new DataPoint(24, 10.8),
                new DataPoint(26, 11.1),
                new DataPoint(28, 11.5),
                new DataPoint(30,11.8),
                new DataPoint(32, 12.1),
                new DataPoint(34, 12.4),
                new DataPoint(36, 12.7),
                new DataPoint(38, 12.9),
                new DataPoint(40,13.2),
                new DataPoint(42, 13.5),
                new DataPoint(44, 13.8),
                new DataPoint(46, 14.1),
                new DataPoint(48, 14.3),
                new DataPoint(50,14.6),
                new DataPoint(52, 14.9),
                new DataPoint(54, 15.2),
                new DataPoint(56, 15.4),
                new DataPoint(58, 15.7),
                new DataPoint(60, 16.0)
        });
        graph.addSeries(series2);
        series2.setColor(Color.rgb(51,204,51));
        series2.setThickness(3);
        series2.setTitle("15th");


        //50th percentile
        LineGraphSeries<DataPoint> series3 = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0,3.3),
                new DataPoint(2, 5.6),
                new DataPoint(4, 7.0),
                new DataPoint(6, 7.9),
                new DataPoint(8, 8.6),
                new DataPoint(10,9.2),
                new DataPoint(12, 9.6),
                new DataPoint(14, 10.1),
                new DataPoint(16, 10.5),
                new DataPoint(18, 10.9),
                new DataPoint(20,11.3),
                new DataPoint(22, 11.8),
                new DataPoint(24, 12.2),
                new DataPoint(26, 12.5),
                new DataPoint(28, 12.9),
                new DataPoint(30,13.3),
                new DataPoint(32, 13.7),
                new DataPoint(34, 14.0),
                new DataPoint(36, 14.3),
                new DataPoint(38, 14.7),
                new DataPoint(40,15.0),
                new DataPoint(42, 15.3),
                new DataPoint(44, 15.7),
                new DataPoint(46, 16.0),
                new DataPoint(48, 16.3),
                new DataPoint(50,16.7),
                new DataPoint(52, 17.0),
                new DataPoint(54, 17.3),
                new DataPoint(56, 17.7),
                new DataPoint(58, 18.0),
                new DataPoint(60, 18.3)
        });
        graph.addSeries(series3);
        series3.setColor(Color.rgb(255,51,153));
        series3.setThickness(3);
        series3.setTitle("50th");


        //85th percentile
        LineGraphSeries<DataPoint> series4 = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0,3.9),
                new DataPoint(2, 6.3),
                new DataPoint(4, 7.9),
                new DataPoint(6, 8.9),
                new DataPoint(8, 9.6),
                new DataPoint(10, 10.3),
                new DataPoint(12, 10.8),
                new DataPoint(14, 11.3),
                new DataPoint(16, 11.8),
                new DataPoint(18,12.3),
                new DataPoint(20, 12.7),
                new DataPoint(22, 13.2),
                new DataPoint(24, 13.7),
                new DataPoint(26, 14.1),
                new DataPoint(28,14.6),
                new DataPoint(30, 15.0),
                new DataPoint(32, 15.5),
                new DataPoint(34, 15.9),
                new DataPoint(36, 16.3),
                new DataPoint(38,16.7),
                new DataPoint(40, 17.1),
                new DataPoint(42, 17.5),
                new DataPoint(44, 17.9),
                new DataPoint(46, 18.3),
                new DataPoint(48,18.7),
                new DataPoint(50, 19.1),
                new DataPoint(52, 19.5),
                new DataPoint(54, 19.9),
                new DataPoint(56, 20.3),
                new DataPoint(58, 20.7),
                new DataPoint(60, 21.1)
        });
        graph.addSeries(series4);
        series4.setColor(Color.rgb(204,153,0));
        series4.setThickness(3);
        series4.setTitle("85th");


        //97th percentile
        LineGraphSeries<DataPoint> series5 = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0,4.3),
                new DataPoint(2, 7.0),
                new DataPoint(4, 8.6),
                new DataPoint(6, 9.7),
                new DataPoint(8, 10.5),
                new DataPoint(10,11.2),
                new DataPoint(12,11.8 ),
                new DataPoint(14, 12.4),
                new DataPoint(16, 12.9),
                new DataPoint(18, 13.5),
                new DataPoint(20,14.0),
                new DataPoint(22, 14.5),
                new DataPoint(24,15.1 ),
                new DataPoint(26, 15.6),
                new DataPoint(28, 16.1),
                new DataPoint(30,16.6),
                new DataPoint(32, 17.1),
                new DataPoint(34,17.6 ),
                new DataPoint(36, 18.0),
                new DataPoint(38, 18.5),
                new DataPoint(40,19.0),
                new DataPoint(42, 19.4),
                new DataPoint(44,19.9 ),
                new DataPoint(46, 20.4),
                new DataPoint(48, 20.9),
                new DataPoint(50,21.3),
                new DataPoint(52, 21.8),
                new DataPoint(54, 22.3),
                new DataPoint(56, 22.8),
                new DataPoint(58, 23.3),
                new DataPoint(60, 23.8)
        });
        graph.addSeries(series5);
        series5.setColor(Color.rgb(128,0,0));
        series5.setThickness(3);
        series5.setTitle("97th");

    }

    public void displayPercentilesforGirls(){
        /**FOR GIRLS NI SYA**/

         //3rd percentile
         LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
         new DataPoint(0,2.4),
         new DataPoint(2, 4),
         new DataPoint(4, 5.1),
         new DataPoint(6, 5.8),
         new DataPoint(8, 6.3),
         new DataPoint(10,6.8),
         new DataPoint(12, 7.1),
         new DataPoint(14, 7.5),
         new DataPoint(16, 7.8),
         new DataPoint(18, 8.2),
         new DataPoint(20,8.5),
         new DataPoint(22, 8.8),
         new DataPoint(24, 9.2),
         new DataPoint(26, 9.5),
         new DataPoint(28, 9.8),
         new DataPoint(30,10.1),
         new DataPoint(32, 10.4),
         new DataPoint(34, 10.7),
         new DataPoint(36, 11),
         new DataPoint(38, 11.2),
         new DataPoint(40,11.5),
         new DataPoint(42, 11.8),
         new DataPoint(44, 12),
         new DataPoint(46, 12.3),
         new DataPoint(48, 12.5),
         new DataPoint(50,12.8),
         new DataPoint(52, 13),
         new DataPoint(54, 13.2),
         new DataPoint(56, 13.5),
         new DataPoint(58, 13.7),
         new DataPoint(60, 14)
         });
         graph.addSeries(series);
         series.setColor(Color.rgb(226,91,34));
         series.setThickness(3);
         series.setTitle("3rd");



         //15th percentile
         LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>(new DataPoint[] {
         new DataPoint(0,2.8),
         new DataPoint(2, 4.5),
         new DataPoint(4, 5.6),
         new DataPoint(6, 6.4),
         new DataPoint(8, 7),
         new DataPoint(10,7.5),
         new DataPoint(12, 7.9),
         new DataPoint(14, 8.3),
         new DataPoint(16, 8.7),
         new DataPoint(18, 9),
         new DataPoint(20,9.4),
         new DataPoint(22, 9.8),
         new DataPoint(24, 10.1),
         new DataPoint(26, 10.5),
         new DataPoint(28, 10.8),
         new DataPoint(30,11.2),
         new DataPoint(32, 11.5),
         new DataPoint(34, 11.8),
         new DataPoint(36, 12.1),
         new DataPoint(38, 12.5),
         new DataPoint(40,12.8),
         new DataPoint(42, 13.1),
         new DataPoint(44, 13.4),
         new DataPoint(46, 13.7),
         new DataPoint(48, 14),
         new DataPoint(50, 14.3),
         new DataPoint(52, 14.5),
         new DataPoint(54, 14.8),
         new DataPoint(56, 15.1),
         new DataPoint(58, 15.4),
         new DataPoint(60, 15.7)
         });
         graph.addSeries(series2);
         series2.setColor(Color.rgb(51,204,51));
         series2.setThickness(3);
         series2.setTitle("15th");

         //50th percentile
         LineGraphSeries<DataPoint> series3 = new LineGraphSeries<DataPoint>(new DataPoint[] {
         new DataPoint(0,3.2),
         new DataPoint(2, 5.1),
         new DataPoint(4, 6.4),
         new DataPoint(6, 7.3),
         new DataPoint(8, 7.9),
         new DataPoint(10,8.5),
         new DataPoint(12, 8.9),
         new DataPoint(14, 9.4),
         new DataPoint(16, 9.8),
         new DataPoint(18, 10.2),
         new DataPoint(20,10.6),
         new DataPoint(22, 11.1),
         new DataPoint(24, 11.5),
         new DataPoint(26, 11.9),
         new DataPoint(28, 12.3),
         new DataPoint(30,12.7),
         new DataPoint(32, 13.1),
         new DataPoint(34, 13.5),
         new DataPoint(36, 13.9),
         new DataPoint(38, 14.2),
         new DataPoint(40,14.6),
         new DataPoint(42, 15),
         new DataPoint(44, 15.3),
         new DataPoint(46, 15.7),
         new DataPoint(48, 16.1),
         new DataPoint(50,16.4),
         new DataPoint(52, 16.8),
         new DataPoint(54, 17.2),
         new DataPoint(56, 17.5),
         new DataPoint(58, 17.9),
         new DataPoint(60, 18.2)
         });
         graph.addSeries(series3);
         series3.setColor(Color.rgb(255,51,153));
         series3.setThickness(3);
         series3.setTitle("50th");

         //85th percentile
         LineGraphSeries<DataPoint> series4 = new LineGraphSeries<DataPoint>(new DataPoint[] {
         new DataPoint(0,3.7),
         new DataPoint(2, 5.9),
         new DataPoint(4, 7.3),
         new DataPoint(6, 8.3),
         new DataPoint(8, 9),
         new DataPoint(10, 9.6),
         new DataPoint(12, 10.2),
         new DataPoint(14, 10.7),
         new DataPoint(16, 11.2),
         new DataPoint(18,11.6),
         new DataPoint(20, 12.1),
         new DataPoint(22, 12.6),
         new DataPoint(24, 13.1),
         new DataPoint(26, 13.6),
         new DataPoint(28,14),
         new DataPoint(30, 14.5),
         new DataPoint(32, 15),
         new DataPoint(34, 15.4),
         new DataPoint(36, 15.9),
         new DataPoint(38,16.3),
         new DataPoint(40, 16.8),
         new DataPoint(42, 17.3),
         new DataPoint(44, 17.7),
         new DataPoint(46, 18.2),
         new DataPoint(48,18.6),
         new DataPoint(50, 19.1),
         new DataPoint(52, 19.5),
         new DataPoint(54, 20),
         new DataPoint(56, 20.4),
         new DataPoint(58, 20.9),
         new DataPoint(60, 21.3)
         });
         graph.addSeries(series4);
         series4.setColor(Color.rgb(204,153,0));
         series4.setThickness(3);
         series4.setTitle("85th");

         //97th percentile
         LineGraphSeries<DataPoint> series5 = new LineGraphSeries<DataPoint>(new DataPoint[] {
         new DataPoint(0,4.2),
         new DataPoint(2, 6.5),
         new DataPoint(4, 8.1),
         new DataPoint(6, 9.2),
         new DataPoint(8, 10),
         new DataPoint(10,10.7),
         new DataPoint(12,11.3 ),
         new DataPoint(14, 11.9),
         new DataPoint(16, 12.5),
         new DataPoint(18, 13),
         new DataPoint(20,13.5),
         new DataPoint(22, 14.1),
         new DataPoint(24,14.6),
         new DataPoint(26, 15.2),
         new DataPoint(28, 15.7),
         new DataPoint(30,16.2),
         new DataPoint(32, 16.8),
         new DataPoint(34,17.3),
         new DataPoint(36, 17.8),
         new DataPoint(38, 18.4),
         new DataPoint(40,18.9),
         new DataPoint(42, 19.5),
         new DataPoint(44, 20),
         new DataPoint(46, 20.6),
         new DataPoint(48, 21.1),
         new DataPoint(50,21.7),
         new DataPoint(52, 22.2),
         new DataPoint(54, 22.8),
         new DataPoint(56, 23.3),
         new DataPoint(58, 23.9),
         new DataPoint(60, 24.4)
         });
         graph.addSeries(series5);
         series5.setColor(Color.rgb(128,0,0));
         series5.setThickness(3);
         series5.setTitle("97th");

    }



}
