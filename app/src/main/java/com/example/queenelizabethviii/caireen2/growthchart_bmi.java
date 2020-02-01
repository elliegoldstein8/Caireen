package com.example.queenelizabethviii.caireen2;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

public class growthchart_bmi extends Fragment {
    private static final String TAG="growthchart_bmi";
    double weight, height;
    double bmi;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(); //para maidentify kinsa na user sa firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();  //for storing data to firebase
    DatabaseReference myRef = database.getReference("Users").child(firebaseAuth.getCurrentUser().getUid()).child("baby"); //reference
    DatabaseReference lastlastref, parasaheight;
    String babyid, babygender, date, babybday;
    boolean gender = true;
    ArrayAdapter<String> adapter;
    GraphView graph;
    int months;
    LineGraphSeries<DataPoint> seriesheight = new LineGraphSeries();//fishy


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.growthchart_bmi,container,false);


        graph = (GraphView) view.findViewById(R.id.graphbmi);
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
        seriesheight.setTitle("BMI");
        seriesheight.setDrawDataPoints(true);
        seriesheight.setDataPointsRadius(8);
        seriesheight.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(getActivity(), dataPoint.getX() + " months, with a BMI of "+ dataPoint.getY(), Toast.LENGTH_SHORT).show();
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
                                        String heightu = dataSnapshot.child("height").getValue(String.class);
                                        String weightu = dataSnapshot.child("weight").getValue(String.class);
                                        date = dataSnapshot.child("time_stamp").getValue(String.class);
                                        if (heightu!=null && weightu !=null){
                                            height = Double.parseDouble(heightu);
                                            weight = Double.parseDouble(weightu);
                                            //double calculate = height/100; //convert to meters
                                            bmi = weight/(height/100)*(height/100); //calculates bmi
                                            seriesheight.appendData(  new DataPoint(months,bmi), true, 100);
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

    }


    public void displayPercentilesforBoys(){
        /**FOR PERCENTILE GRAPHS (BOYS)**/
        //3rd percentile
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0,11.3),
                new DataPoint(2, 13.8),
                new DataPoint(4, 14.7),
                new DataPoint(6, 14.9),
                new DataPoint(8, 14.9),
                new DataPoint(10,14.7),
                new DataPoint(12, 14.5),
                new DataPoint(14, 14.3),
                new DataPoint(16, 14.2),
                new DataPoint(18, 14),
                new DataPoint(20,13.9),
                new DataPoint(22, 13.8),
                new DataPoint(24, 13.7),
                new DataPoint(24,13.9),
                new DataPoint(26, 13.8),
                new DataPoint(28, 13.8),
                new DataPoint(30,13.7),
                new DataPoint(32, 13.6),
                new DataPoint(34, 13.5),
                new DataPoint(36, 13.5),
                new DataPoint(38, 13.4),
                new DataPoint(40,13.4),
                new DataPoint(42, 13.3),
                new DataPoint(44, 13.3),
                new DataPoint(46, 13.2),
                new DataPoint(48, 13.2),
                new DataPoint(50,13.2),
                new DataPoint(52, 13.1),
                new DataPoint(54, 13.1),
                new DataPoint(56, 13.1),
                new DataPoint(58, 13),
                new DataPoint(60, 13)
        });
        graph.addSeries(series);
        series.setColor(Color.rgb(226,91,34));
        series.setThickness(3);
        series.setTitle("3rd");

        //15th percentile
        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0,12.2),
                new DataPoint(2, 14.9),
                new DataPoint(4, 15.7),
                new DataPoint(6, 15.9),
                new DataPoint(8, 15.9),
                new DataPoint(10,15.7),
                new DataPoint(12, 15.5),
                new DataPoint(14, 15.3),
                new DataPoint(16, 15.1),
                new DataPoint(18, 14.9),
                new DataPoint(20,14.8),
                new DataPoint(22, 14.6),
                new DataPoint(24, 14.5),
                new DataPoint(24,14.8),
                new DataPoint(26, 14.7),
                new DataPoint(28, 14.7),
                new DataPoint(30,14.6),
                new DataPoint(32, 14.5),
                new DataPoint(34, 14.4),
                new DataPoint(36, 14.4),
                new DataPoint(38, 14.3),
                new DataPoint(40,14.3),
                new DataPoint(42, 14.2),
                new DataPoint(44, 14.2),
                new DataPoint(46, 14.1),
                new DataPoint(48, 14.1),
                new DataPoint(50,14.1),
                new DataPoint(52, 14),
                new DataPoint(54, 14),
                new DataPoint(56, 14),
                new DataPoint(58, 13.9),
                new DataPoint(60, 13.9)
        });
        graph.addSeries(series2);
        series2.setColor(Color.rgb(51,204,51));
        series2.setThickness(3);
        series2.setTitle("15th");

        //50th percentile
        LineGraphSeries<DataPoint> series3 = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0,13.4),
                new DataPoint(2, 16.3),
                new DataPoint(4, 17.2),
                new DataPoint(6, 17.3),
                new DataPoint(8, 17.3),
                new DataPoint(10,17),
                new DataPoint(12, 16.8),
                new DataPoint(14, 16.6),
                new DataPoint(16, 16.3),
                new DataPoint(18, 16.1),
                new DataPoint(20,16),
                new DataPoint(22, 15.8),
                new DataPoint(24, 15.7),
                new DataPoint(24,16),
                new DataPoint(26, 15.9),
                new DataPoint(28, 15.9),
                new DataPoint(30,15.8),
                new DataPoint(32, 15.7),
                new DataPoint(34, 15.7),
                new DataPoint(36, 15.6),
                new DataPoint(38, 15.5),
                new DataPoint(40,15.5),
                new DataPoint(42, 15.4),
                new DataPoint(44, 15.4),
                new DataPoint(46, 15.4),
                new DataPoint(48, 15.3),
                new DataPoint(50,15.3),
                new DataPoint(52, 15.3),
                new DataPoint(54, 15.3),
                new DataPoint(56, 15.2),
                new DataPoint(58, 15.2),
                new DataPoint(60, 15.2)
        });
        graph.addSeries(series3);
        series3.setColor(Color.rgb(255,51,153));
        series3.setThickness(3);
        series3.setTitle("50th");

        //85th percentile
        LineGraphSeries<DataPoint> series4 = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0,14.8),
                new DataPoint(2, 17.8),
                new DataPoint(4, 18.7),
                new DataPoint(6, 18.9),
                new DataPoint(8, 18.8),
                new DataPoint(10,18.6),
                new DataPoint(12, 18.3),
                new DataPoint(14, 18),
                new DataPoint(16, 17.8),
                new DataPoint(18, 17.5),
                new DataPoint(20,17.4),
                new DataPoint(22, 17.2),
                new DataPoint(24, 17.1),
                new DataPoint(24,17.4),
                new DataPoint(26, 17.3),
                new DataPoint(28, 17.2),
                new DataPoint(30,17.2),
                new DataPoint(32, 17.1),
                new DataPoint(34, 17),
                new DataPoint(36, 17),
                new DataPoint(38, 16.9),
                new DataPoint(40,16.8),
                new DataPoint(42, 16.8),
                new DataPoint(44, 16.8),
                new DataPoint(46, 16.7),
                new DataPoint(48, 16.7),
                new DataPoint(50,16.7),
                new DataPoint(52, 16.7),
                new DataPoint(54, 16.7),
                new DataPoint(56, 16.7),
                new DataPoint(58, 16.7),
                new DataPoint(60, 16.7)
        });
        graph.addSeries(series4);
        series4.setColor(Color.rgb(204,153,0));
        series4.setThickness(3);
        series4.setTitle("85th");

        //97th percentile
        LineGraphSeries<DataPoint> series5 = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0,16.1),
                new DataPoint(2, 19.2),
                new DataPoint(4, 20.1),
                new DataPoint(6, 20.3),
                new DataPoint(8, 20.2),
                new DataPoint(10,19.9),
                new DataPoint(12, 19.6),
                new DataPoint(14, 19.3),
                new DataPoint(16, 19.1),
                new DataPoint(18, 18.8),
                new DataPoint(20,18.6),
                new DataPoint(22, 18.5),
                new DataPoint(24, 18.3),
                new DataPoint(24,18.7),
                new DataPoint(26, 18.6),
                new DataPoint(28, 18.5),
                new DataPoint(30,18.4),
                new DataPoint(32, 18.3),
                new DataPoint(34, 18.2),
                new DataPoint(36, 18.2),
                new DataPoint(38, 18.1),
                new DataPoint(40,18.1),
                new DataPoint(42, 18),
                new DataPoint(44, 18),
                new DataPoint(46, 18),
                new DataPoint(48, 18),
                new DataPoint(50,18),
                new DataPoint(52, 18),
                new DataPoint(54, 18),
                new DataPoint(56, 18),
                new DataPoint(58, 18),
                new DataPoint(60, 18.1)
        });
        graph.addSeries(series5);
        series5.setColor(Color.rgb(128,0,0));
        series5.setThickness(3);
        series5.setTitle("97th");

    }

    public void displayPercentilesforGirls(){
        /**
         * FOR GIRLS NAPOD NI SYA**/
        //3rd percentile
         LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
         new DataPoint(0,11.2),
         new DataPoint(2, 13.2),
         new DataPoint(4, 14),
         new DataPoint(6, 14.3),
         new DataPoint(8, 14.3),
         new DataPoint(10,14.1),
         new DataPoint(12, 13.9),
         new DataPoint(14, 13.7),
         new DataPoint(16, 13.6),
         new DataPoint(18, 13.4),
         new DataPoint(20,13.3),
         new DataPoint(22, 13.3),
         new DataPoint(24, 13.2),
         new DataPoint(24,13.5),
         new DataPoint(26, 13.4),
         new DataPoint(28, 13.4),
         new DataPoint(30,13.3),
         new DataPoint(32, 13.3),
         new DataPoint(34, 13.2),
         new DataPoint(36, 13.2),
         new DataPoint(38, 13.2),
         new DataPoint(40,13.1),
         new DataPoint(42, 13.1),
         new DataPoint(44, 13),
         new DataPoint(46, 13),
         new DataPoint(48, 12.9),
         new DataPoint(50,12.9),
         new DataPoint(52, 12.9),
         new DataPoint(54, 12.9),
         new DataPoint(56, 12.8),
         new DataPoint(58, 12.8),
         new DataPoint(60, 12.8)
         });
         graph.addSeries(series);
         series.setColor(Color.rgb(226,91,34));
         series.setThickness(3);
         series.setTitle("3rd");

         //15th percentile
         LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>(new DataPoint[] {
         new DataPoint(0,12.1),
         new DataPoint(2, 14.3),
         new DataPoint(4, 15.2),
         new DataPoint(6, 15.4),
         new DataPoint(8, 15.4),
         new DataPoint(10,15.2),
         new DataPoint(12, 15),
         new DataPoint(14, 14.7),
         new DataPoint(16, 14.6),
         new DataPoint(18, 14.4),
         new DataPoint(20,14.3),
         new DataPoint(22, 14.2),
         new DataPoint(24, 14.1),
         new DataPoint(24,14.4),
         new DataPoint(26, 14.4),
         new DataPoint(28, 14.3),
         new DataPoint(30,14.3),
         new DataPoint(32, 14.2),
         new DataPoint(34, 14.2),
         new DataPoint(36, 14.1),
         new DataPoint(38, 14.1),
         new DataPoint(40,14),
         new DataPoint(42, 14),
         new DataPoint(44, 14),
         new DataPoint(46, 13.9),
         new DataPoint(48, 13.9),
         new DataPoint(50,13.9),
         new DataPoint(52, 13.9),
         new DataPoint(54, 13.9),
         new DataPoint(56, 13.8),
         new DataPoint(58, 13.8),
         new DataPoint(60, 13.8)
         });
         graph.addSeries(series2);
         series2.setColor(Color.rgb(51,204,51));
         series2.setThickness(3);
         series2.setTitle("15th");

         //50th percentile
         LineGraphSeries<DataPoint> series3 = new LineGraphSeries<DataPoint>(new DataPoint[] {
         new DataPoint(0,13.3),
         new DataPoint(2, 15.8),
         new DataPoint(4, 16.7),
         new DataPoint(6, 16.9),
         new DataPoint(8, 16.8),
         new DataPoint(10,16.6),
         new DataPoint(12, 16.4),
         new DataPoint(14, 16.1),
         new DataPoint(16, 15.9),
         new DataPoint(18, 15.7),
         new DataPoint(20,15.6),
         new DataPoint(22, 15.5),
         new DataPoint(24, 15.4),
         new DataPoint(24,15.7),
         new DataPoint(26, 15.6),
         new DataPoint(28, 15.6),
         new DataPoint(30,15.5),
         new DataPoint(32, 15.5),
         new DataPoint(34, 15.4),
         new DataPoint(36, 15.4),
         new DataPoint(38, 15.4),
         new DataPoint(40,15.3),
         new DataPoint(42, 15.3),
         new DataPoint(44, 15.3),
         new DataPoint(46, 15.3),
         new DataPoint(48, 15.3),
         new DataPoint(50,15.3),
         new DataPoint(52, 15.2),
         new DataPoint(54, 15.3),
         new DataPoint(56, 15.3),
         new DataPoint(58, 15.3),
         new DataPoint(60, 15.3)
         });
         graph.addSeries(series3);
         series3.setColor(Color.rgb(255,51,153));
         series3.setThickness(3);
         series3.setTitle("50th");

         //85th percentile
         LineGraphSeries<DataPoint> series4 = new LineGraphSeries<DataPoint>(new DataPoint[] {
         new DataPoint(0,14.7),
         new DataPoint(2, 17.4),
         new DataPoint(4, 18.3),
         new DataPoint(6, 18.6),
         new DataPoint(8, 18.5),
         new DataPoint(10,18.2),
         new DataPoint(12, 17.9),
         new DataPoint(14, 17.7),
         new DataPoint(16, 17.4),
         new DataPoint(18, 17.2),
         new DataPoint(20,17.1),
         new DataPoint(22, 17),
         new DataPoint(24, 16.9),
         new DataPoint(24,17.2),
         new DataPoint(26, 17.1),
         new DataPoint(28, 17),
         new DataPoint(30,17),
         new DataPoint(32, 16.9),
         new DataPoint(34, 16.9),
         new DataPoint(36, 16.9),
         new DataPoint(38, 16.8),
         new DataPoint(40,16.8),
         new DataPoint(42, 16.8),
         new DataPoint(44, 16.8),
         new DataPoint(46, 16.8),
         new DataPoint(48, 16.8),
         new DataPoint(50,16.8),
         new DataPoint(52, 16.9),
         new DataPoint(54, 16.9),
         new DataPoint(56, 16.9),
         new DataPoint(58, 16.9),
         new DataPoint(60, 17)
         });
         graph.addSeries(series4);
         series4.setColor(Color.rgb(204,153,0));
         series4.setThickness(3);
         series4.setTitle("85th");

         //97th percentile
         LineGraphSeries<DataPoint> series5 = new LineGraphSeries<DataPoint>(new DataPoint[] {
         new DataPoint(0,15.9),
         new DataPoint(2, 18.8),
         new DataPoint(4, 19.8),
         new DataPoint(6, 20.1),
         new DataPoint(8, 20),
         new DataPoint(10,19.7),
         new DataPoint(12, 19.4),
         new DataPoint(14, 19.1),
         new DataPoint(16, 18.8),
         new DataPoint(18, 18.6),
         new DataPoint(20,18.5),
         new DataPoint(22, 18.3),
         new DataPoint(24, 18.2),
         new DataPoint(24,18.5),
         new DataPoint(26, 18.5),
         new DataPoint(28, 18.4),
         new DataPoint(30,18.3),
         new DataPoint(32, 18.3),
         new DataPoint(34, 18.2),
         new DataPoint(36, 18.2),
         new DataPoint(38, 18.2),
         new DataPoint(40,18.2),
         new DataPoint(42, 18.2),
         new DataPoint(44, 18.2),
         new DataPoint(46, 18.3),
         new DataPoint(48, 18.3),
         new DataPoint(50,18.3),
         new DataPoint(52, 18.4),
         new DataPoint(54, 18.4),
         new DataPoint(56, 18.5),
         new DataPoint(58, 18.5),
         new DataPoint(60, 18.6)
         });
         graph.addSeries(series5);
         series5.setColor(Color.rgb(128,0,0));
         series5.setThickness(3);
         series5.setTitle("97th");

    }



}
