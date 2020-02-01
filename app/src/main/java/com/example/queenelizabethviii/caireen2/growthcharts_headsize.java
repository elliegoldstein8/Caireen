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

public class growthcharts_headsize extends Fragment {
    private static final String TAG="growthcharts_headsize";
    double headsize;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(); //para maidentify kinsa na user sa firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();  //for storing data to firebase
    DatabaseReference myRef = database.getReference("Users").child(firebaseAuth.getCurrentUser().getUid()).child("baby"); //reference
    DatabaseReference lastlastref, lastnagid, parasaheight;
    String babyid, babygender, babybday, date;
    boolean gender = true;
    ArrayAdapter<String> adapter;
    GraphView graph;
    int months;
    LineGraphSeries<DataPoint> seriesheight = new LineGraphSeries();//fishy

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.growthchart_headsize,container,false);

        graph = (GraphView) view.findViewById(R.id.graphheadsize);
        // set manual X bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(30);
        graph.getViewport().setMaxY(60);

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
        seriesheight.setTitle("Head Size");
        seriesheight.setDrawDataPoints(true);
        seriesheight.setDataPointsRadius(8);
        seriesheight.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
             //   Toast.makeText(getActivity(), dataPoint+ "\n" + date, Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), dataPoint.getX() + " months and "+ dataPoint.getY()+ "cm head circumference", Toast.LENGTH_SHORT).show();
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
                            babybday = child.child("baby_bday").getValue(String.class); //change date data type
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
                                        String headsizeu = dataSnapshot.child("head_circumference").getValue(String.class);
                                        date = dataSnapshot.child("time_stamp").getValue(String.class);
                                        if (headsizeu!=null){
                                            headsize = Double.parseDouble(headsizeu);
                                            seriesheight.appendData(  new DataPoint(months,headsize), true, 100);
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
                new DataPoint(0,32.1),
                new DataPoint(2, 36.9),
                new DataPoint(4, 39.4),
                new DataPoint(6, 41.0),
                new DataPoint(8, 42.2),
                new DataPoint(10,43),
                new DataPoint(12, 43.6),
                new DataPoint(14, 44.1),
                new DataPoint(16, 44.5),
                new DataPoint(18, 44.9),
                new DataPoint(20,45.2),
                new DataPoint(22, 45.4),
                new DataPoint(24, 45.7),
                new DataPoint(26,45.9),
                new DataPoint(28, 46.1),
                new DataPoint(30, 46.3),
                new DataPoint(32,46.5),
                new DataPoint(34, 46.6),
                new DataPoint(36, 46.8),
                new DataPoint(38, 46.9),
                new DataPoint(40,47),
                new DataPoint(42, 47.2),
                new DataPoint(44, 47.3),
                new DataPoint(46, 47.4),
                new DataPoint(48, 47.5),
                new DataPoint(50,47.5),
                new DataPoint(52, 47.6),
                new DataPoint(54, 47.7),
                new DataPoint(56, 47.8),
                new DataPoint(58, 47.9),
                new DataPoint(60, 47.9)
        });
        graph.addSeries(series);
        series.setColor(Color.rgb(226,91,34));
        series.setThickness(3);
        series.setTitle("3rd");

        //15th percentile
        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0,33.1),
                new DataPoint(2, 37.9),
                new DataPoint(4, 40.4),
                new DataPoint(6, 42.1),
                new DataPoint(8, 43.2),
                new DataPoint(10,44.1),
                new DataPoint(12, 44.7),
                new DataPoint(14, 45.2),
                new DataPoint(16, 45.6),
                new DataPoint(18, 46.0),
                new DataPoint(20,46.3),
                new DataPoint(22, 46.6),
                new DataPoint(24, 46.8),
                new DataPoint(26, 47.1),
                new DataPoint(28, 47.3),
                new DataPoint(30,47.5),
                new DataPoint(32, 47.7),
                new DataPoint(34, 47.8),
                new DataPoint(36, 48),
                new DataPoint(38, 48.1),
                new DataPoint(40,48.3),
                new DataPoint(42, 48.4),
                new DataPoint(44, 48.5),
                new DataPoint(46, 48.6),
                new DataPoint(48, 48.7),
                new DataPoint(50,48.8),
                new DataPoint(52, 48.9),
                new DataPoint(54, 49),
                new DataPoint(56, 49),
                new DataPoint(58, 49.1),
                new DataPoint(60, 49.2)
        });
        graph.addSeries(series2);
        series2.setColor(Color.rgb(51,204,51));
        series2.setThickness(3);
        series2.setTitle("15th");

        //50th percentile
        LineGraphSeries<DataPoint> series3 = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0,34.5),
                new DataPoint(2, 39.1),
                new DataPoint(4, 41.6),
                new DataPoint(6, 43.3),
                new DataPoint(8, 44.5),
                new DataPoint(10,45.4),
                new DataPoint(12, 46.1),
                new DataPoint(14, 46.6),
                new DataPoint(16, 47),
                new DataPoint(18, 47.4),
                new DataPoint(20,47.7),
                new DataPoint(22, 48),
                new DataPoint(24, 48.3),
                new DataPoint(26, 48.5),
                new DataPoint(28, 48.7),
                new DataPoint(30,48.9),
                new DataPoint(32, 49.1),
                new DataPoint(34, 49.3),
                new DataPoint(36, 49.5),
                new DataPoint(38, 49.6),
                new DataPoint(40,49.7),
                new DataPoint(42, 49.9),
                new DataPoint(44, 50),
                new DataPoint(46, 50.1),
                new DataPoint(48, 50.2),
                new DataPoint(50,50.3),
                new DataPoint(52, 50.4),
                new DataPoint(54, 50.5),
                new DataPoint(56, 50.6),
                new DataPoint(58, 50.7),
                new DataPoint(60, 50.7)
        });
        graph.addSeries(series3);
        series3.setColor(Color.rgb(255,51,153));
        series3.setThickness(3);
        series3.setTitle("50th");

        //85th percentile
        LineGraphSeries<DataPoint> series4 = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0,35.8),
                new DataPoint(2, 40.3),
                new DataPoint(4, 42.9),
                new DataPoint(6, 44.6),
                new DataPoint(8, 45.8),
                new DataPoint(10,46.7),
                new DataPoint(12, 47.4),
                new DataPoint(14, 47.9),
                new DataPoint(16, 48.4),
                new DataPoint(18, 48.7),
                new DataPoint(20,49.1),
                new DataPoint(22, 49.4),
                new DataPoint(24, 49.7),
                new DataPoint(26, 49.9),
                new DataPoint(28, 50.2),
                new DataPoint(30,50.4),
                new DataPoint(32, 50.6),
                new DataPoint(34, 50.8),
                new DataPoint(36, 50.9),
                new DataPoint(38, 51.1),
                new DataPoint(40,51.2),
                new DataPoint(42, 51.4),
                new DataPoint(44, 51.5),
                new DataPoint(46, 51.6),
                new DataPoint(48, 51.7),
                new DataPoint(50,51.8),
                new DataPoint(52, 51.9),
                new DataPoint(54, 52),
                new DataPoint(56, 52.1),
                new DataPoint(58, 52.2),
                new DataPoint(60, 52.3)
        });
        graph.addSeries(series4);
        series4.setColor(Color.rgb(204,153,0));
        series4.setThickness(3);
        series4.setTitle("85th");

        //97th percentile
        LineGraphSeries<DataPoint> series5 = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0,36.9),
                new DataPoint(2, 41.3),
                new DataPoint(4, 43.9),
                new DataPoint(6, 45.6),
                new DataPoint(8, 46.9),
                new DataPoint(10,47.8),
                new DataPoint(12, 48.5),
                new DataPoint(14, 49),
                new DataPoint(16, 49.5),
                new DataPoint(18, 49.9),
                new DataPoint(20,50.2),
                new DataPoint(22, 50.5),
                new DataPoint(24, 50.8),
                new DataPoint(26, 51.1),
                new DataPoint(28, 51.3),
                new DataPoint(30,51.6),
                new DataPoint(32, 51.8),
                new DataPoint(34, 52),
                new DataPoint(36, 52.1),
                new DataPoint(38, 52.3),
                new DataPoint(40,52.4),
                new DataPoint(42, 52.6),
                new DataPoint(44, 52.7),
                new DataPoint(46, 52.8),
                new DataPoint(48, 53),
                new DataPoint(50,53.1),
                new DataPoint(52, 53.2),
                new DataPoint(54, 53.3),
                new DataPoint(56, 53.4),
                new DataPoint(58, 53.5),
                new DataPoint(60, 53.5)
        });
        graph.addSeries(series5);
        series5.setColor(Color.rgb(128,0,0));
        series5.setThickness(3);
        series5.setTitle("97th");
    }

    public void displayPercentilesforGirls(){
        /**FOR GIRLS NAPOD NI SYA**/
         //3rd percentile
         LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
         new DataPoint(0,31.7),
         new DataPoint(2, 36),
         new DataPoint(4, 38.2),
         new DataPoint(6, 39.7),
         new DataPoint(8, 40.9),
         new DataPoint(10,41.7),
         new DataPoint(12, 42.3),
         new DataPoint(14, 42.9),
         new DataPoint(16, 43.3),
         new DataPoint(18, 43.6),
         new DataPoint(20,44),
         new DataPoint(22, 44.3),
         new DataPoint(24, 44.6),
         new DataPoint(26,44.8),
         new DataPoint(28, 45.1),
         new DataPoint(30, 45.3),
         new DataPoint(32,45.5),
         new DataPoint(34, 45.7),
         new DataPoint(36, 45.9),
         new DataPoint(38, 46),
         new DataPoint(40,46.2),
         new DataPoint(42, 46.3),
         new DataPoint(44, 46.4),
         new DataPoint(46, 46.5),
         new DataPoint(48, 46.7),
         new DataPoint(50,46.8),
         new DataPoint(52, 46.9),
         new DataPoint(54, 47.0),
         new DataPoint(56, 47.1),
         new DataPoint(58, 47.2),
         new DataPoint(60, 47.2)
         });
         graph.addSeries(series);
         series.setColor(Color.rgb(226,91,34));
         series.setThickness(3);
         series.setTitle("3rd");

         //15th percentile
         LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>(new DataPoint[] {
         new DataPoint(0,32.7),
         new DataPoint(2, 37),
         new DataPoint(4, 39.3),
         new DataPoint(6, 40.8),
         new DataPoint(8, 42),
         new DataPoint(10,42.8),
         new DataPoint(12, 43.5),
         new DataPoint(14, 44),
         new DataPoint(16, 44.4),
         new DataPoint(18, 44.8),
         new DataPoint(20,45.1),
         new DataPoint(22, 45.4),
         new DataPoint(24, 45.7),
         new DataPoint(26, 46),
         new DataPoint(28, 46.3),
         new DataPoint(30,46.5),
         new DataPoint(32, 46.7),
         new DataPoint(34, 46.9),
         new DataPoint(36, 47),
         new DataPoint(38, 47.2),
         new DataPoint(40,47.4),
         new DataPoint(42, 47.5),
         new DataPoint(44, 47.6),
         new DataPoint(46, 47.7),
         new DataPoint(48, 47.9),
         new DataPoint(50,48),
         new DataPoint(52, 48.1),
         new DataPoint(54, 48.2),
         new DataPoint(56, 48.3),
         new DataPoint(58, 48.4),
         new DataPoint(60, 48.4)
         });
         graph.addSeries(series2);
         series2.setColor(Color.rgb(51,204,51));
         series2.setThickness(3);
         series2.setTitle("15th");

         //50th percentile
         LineGraphSeries<DataPoint> series3 = new LineGraphSeries<DataPoint>(new DataPoint[] {
         new DataPoint(0,33.9),
         new DataPoint(2, 38.3),
         new DataPoint(4, 40.6),
         new DataPoint(6, 42.2),
         new DataPoint(8, 43.4),
         new DataPoint(10,44.2),
         new DataPoint(12, 44.9),
         new DataPoint(14, 45.4),
         new DataPoint(16, 45.9),
         new DataPoint(18, 46.2),
         new DataPoint(20,46.6),
         new DataPoint(22, 46.9),
         new DataPoint(24, 47.2),
         new DataPoint(26, 47.5),
         new DataPoint(28, 47.7),
         new DataPoint(30,47.9),
         new DataPoint(32, 48.1),
         new DataPoint(34, 48.3),
         new DataPoint(36, 48.5),
         new DataPoint(38, 48.7),
         new DataPoint(40,48.8),
         new DataPoint(42, 49),
         new DataPoint(44, 49.1),
         new DataPoint(46, 49.2),
         new DataPoint(48, 49.3),
         new DataPoint(50,49.4),
         new DataPoint(52, 49.5),
         new DataPoint(54, 49.6),
         new DataPoint(56, 49.7),
         new DataPoint(58, 49.8),
         new DataPoint(60, 49.9)
         });
         graph.addSeries(series3);
         series3.setColor(Color.rgb(255,51,153));
         series3.setThickness(3);
         series3.setTitle("50th");

         //85th percentile
         LineGraphSeries<DataPoint> series4 = new LineGraphSeries<DataPoint>(new DataPoint[] {
         new DataPoint(0,35.1),
         new DataPoint(2, 39.5),
         new DataPoint(4, 41.9),
         new DataPoint(6, 43.5),
         new DataPoint(8, 44.7),
         new DataPoint(10,45.6),
         new DataPoint(12, 46.3),
         new DataPoint(14, 46.8),
         new DataPoint(16, 47.3),
         new DataPoint(18, 47.7),
         new DataPoint(20,48),
         new DataPoint(22, 48.3),
         new DataPoint(24, 48.6),
         new DataPoint(26, 48.9),
         new DataPoint(28, 49.2),
         new DataPoint(30,49.4),
         new DataPoint(32, 49.6),
         new DataPoint(34, 49.8),
         new DataPoint(36, 50),
         new DataPoint(38, 50.1),
         new DataPoint(40,50.3),
         new DataPoint(42, 50.4),
         new DataPoint(44, 50.6),
         new DataPoint(46, 50.7),
         new DataPoint(48, 50.8),
         new DataPoint(50,50.9),
         new DataPoint(52, 51),
         new DataPoint(54, 51.1),
         new DataPoint(56, 51.2),
         new DataPoint(58, 51.3),
         new DataPoint(60, 51.4)
         });
         graph.addSeries(series4);
         series4.setColor(Color.rgb(204,153,0));
         series4.setThickness(3);
         series4.setTitle("85th");

         //97th percentile
         LineGraphSeries<DataPoint> series5 = new LineGraphSeries<DataPoint>(new DataPoint[] {
         new DataPoint(0,36.1),
         new DataPoint(2, 40.5),
         new DataPoint(4, 43),
         new DataPoint(6, 44.6),
         new DataPoint(8, 45.9),
         new DataPoint(10,46.8),
         new DataPoint(12, 47.5),
         new DataPoint(14, 48),
         new DataPoint(16, 48.5),
         new DataPoint(18, 48.8),
         new DataPoint(20,49.2),
         new DataPoint(22, 49.5),
         new DataPoint(24, 49.8),
         new DataPoint(26, 50.1),
         new DataPoint(28, 50.3),
         new DataPoint(30,50.6),
         new DataPoint(32, 50.8),
         new DataPoint(34, 51),
         new DataPoint(36, 51.2),
         new DataPoint(38, 51.3),
         new DataPoint(40,51.5),
         new DataPoint(42, 51.6),
         new DataPoint(44, 51.8),
         new DataPoint(46, 51.9),
         new DataPoint(48, 52),
         new DataPoint(50,52.1),
         new DataPoint(52, 52.2),
         new DataPoint(54, 52.3),
         new DataPoint(56, 52.4),
         new DataPoint(58, 52.5),
         new DataPoint(60, 52.6)
         });
         graph.addSeries(series5);
         series5.setColor(Color.rgb(128,0,0));
         series5.setThickness(3);
         series5.setTitle("97th");
    }


}
