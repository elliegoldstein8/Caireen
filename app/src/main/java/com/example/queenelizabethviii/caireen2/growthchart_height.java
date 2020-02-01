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

import java.util.ArrayList;

public class growthchart_height extends Fragment {
    private static final String TAG="growthchart_height";
    double height;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(); //para maidentify kinsa na user sa firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();  //for storing data to firebase
    DatabaseReference myRef = database.getReference("Users").child(firebaseAuth.getCurrentUser().getUid()).child("baby"); //reference
    DatabaseReference lastlastref, parasaheight, last;
    String babyid, babygender, babybday, date;
    boolean gender = true;
    ArrayAdapter<String> adapter;
    GraphView graph;
    int months;
    LineGraphSeries<DataPoint> seriesheight = new LineGraphSeries();//fishy

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.growthchart_height,container,false);

        //3rd percentile
        graph = (GraphView) view.findViewById(R.id.graphheight);
        // set manual X bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(40);
        graph.getViewport().setMaxY(120);

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
            displayPercentileforGirls();
        }
        else{
            displayPercentilesforBoys();
        }


        graph.addSeries(seriesheight);
        seriesheight.setColor(Color.rgb(0,0,255));
        seriesheight.setThickness(6);
        seriesheight.setTitle("Height");
        seriesheight.setDrawDataPoints(true);
        seriesheight.setDataPointsRadius(8);
        seriesheight.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(getActivity(), dataPoint.getX() + " months and "+ dataPoint.getY()+ "cm height", Toast.LENGTH_SHORT).show();
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
                                        String heightu = dataSnapshot.child("height").getValue(String.class);
                                        date = dataSnapshot.child("time_stamp").getValue(String.class);
                                        if (heightu != null) {
                                            height = Double.parseDouble(heightu);
                                            seriesheight.appendData(new DataPoint(months, height), true, 100);
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
            new DataPoint(0,46.3),
            new DataPoint(2, 54.7),
            new DataPoint(4, 60),
            new DataPoint(6, 63.6),
            new DataPoint(8, 66.5),
            new DataPoint(10,69),
            new DataPoint(12, 71.3),
            new DataPoint(14, 73.4),
            new DataPoint(16, 75.4),
            new DataPoint(18, 77.2),
            new DataPoint(20,78.9),
            new DataPoint(22, 80.5),
            new DataPoint(24, 82.1),
            new DataPoint(24,81.4),
            new DataPoint(26, 82.8),
            new DataPoint(28, 84.2),
            new DataPoint(30,85.5),
            new DataPoint(32, 86.8),
            new DataPoint(34, 88),
            new DataPoint(36, 89.1),
            new DataPoint(38, 90.2),
            new DataPoint(40,91.3),
            new DataPoint(42, 92.4),
            new DataPoint(44, 93.4),
            new DataPoint(46, 94.4),
            new DataPoint(48, 95.4),
            new DataPoint(50,96.4),
            new DataPoint(52, 97.4),
            new DataPoint(54, 98.4),
            new DataPoint(56, 99.3),
            new DataPoint(58, 100.3),
            new DataPoint(60, 101.2)
    });
    graph.addSeries(series);
    series.setColor(Color.rgb(226,91,34));
    series.setThickness(3);
    series.setTitle("3rd");

    //15th percentile
    LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>(new DataPoint[] {
            new DataPoint(0,47.9),
            new DataPoint(2, 56.4),
            new DataPoint(4, 61.7),
            new DataPoint(6, 65.4),
            new DataPoint(8, 68.3),
            new DataPoint(10,70.9),
            new DataPoint(12, 73.3),
            new DataPoint(14, 75.5),
            new DataPoint(16, 77.5),
            new DataPoint(18, 79.5),
            new DataPoint(20,81.3),
            new DataPoint(22, 83),
            new DataPoint(24, 84.6),
            new DataPoint(24,83.9),
            new DataPoint(26, 85.5),
            new DataPoint(28, 87),
            new DataPoint(30,88.4),
            new DataPoint(32, 89.7),
            new DataPoint(34, 91),
            new DataPoint(36, 92.2),
            new DataPoint(38, 93.4),
            new DataPoint(40,94.6),
            new DataPoint(42, 95.7),
            new DataPoint(44, 96.8),
            new DataPoint(46, 97.9),
            new DataPoint(48, 99),
            new DataPoint(50,100),
            new DataPoint(52, 101.1),
            new DataPoint(54, 102.1),
            new DataPoint(56, 103.1),
            new DataPoint(58, 104.1),
            new DataPoint(60, 105.2)
    });
    graph.addSeries(series2);
    series2.setColor(Color.rgb(51,204,51));
    series2.setThickness(3);
    series2.setTitle("15th");

    //50th percentile
    LineGraphSeries<DataPoint> series3 = new LineGraphSeries<DataPoint>(new DataPoint[] {
            new DataPoint(0,49.9),
            new DataPoint(2, 58.4),
            new DataPoint(4, 63.9),
            new DataPoint(6, 67.6),
            new DataPoint(8, 70.6),
            new DataPoint(10,73.3),
            new DataPoint(12, 75.7),
            new DataPoint(14, 78),
            new DataPoint(16, 80.2),
            new DataPoint(18, 82.3),
            new DataPoint(20,84.2),
            new DataPoint(22, 86),
            new DataPoint(24, 87.8),
            new DataPoint(24,87.1),
            new DataPoint(26, 88.8),
            new DataPoint(28, 90.4),
            new DataPoint(30,91.9),
            new DataPoint(32, 93.4),
            new DataPoint(34, 94.8),
            new DataPoint(36, 96.1),
            new DataPoint(38, 97.4),
            new DataPoint(40,98.6),
            new DataPoint(42, 99.9),
            new DataPoint(44, 101),
            new DataPoint(46, 102.2),
            new DataPoint(48, 103.3),
            new DataPoint(50,104.4),
            new DataPoint(52, 105.6),
            new DataPoint(54, 106.7),
            new DataPoint(56, 107.8),
            new DataPoint(58, 108.9),
            new DataPoint(60, 110)
    });
    graph.addSeries(series3);
    series3.setColor(Color.rgb(255,51,153));
    series3.setThickness(3);
    series3.setTitle("50th");

    //85th percentile
    LineGraphSeries<DataPoint> series4 = new LineGraphSeries<DataPoint>(new DataPoint[] {
            new DataPoint(0,51.8),
            new DataPoint(2, 60.5),
            new DataPoint(4, 66),
            new DataPoint(6, 69.8),
            new DataPoint(8, 72.9),
            new DataPoint(10,75.6),
            new DataPoint(12, 78.2),
            new DataPoint(14, 80.6),
            new DataPoint(16, 82.9),
            new DataPoint(18, 85.1),
            new DataPoint(20,87.1),
            new DataPoint(22, 89.1),
            new DataPoint(24, 91.0),
            new DataPoint(24,90.3),
            new DataPoint(26, 92.1),
            new DataPoint(28, 93.8),
            new DataPoint(30,95.5),
            new DataPoint(32, 97),
            new DataPoint(34, 98.5),
            new DataPoint(36, 99.9),
            new DataPoint(38, 101.3),
            new DataPoint(40,102.7),
            new DataPoint(42, 104),
            new DataPoint(44, 105.2),
            new DataPoint(46, 106.5),
            new DataPoint(48, 107.7),
            new DataPoint(50,108.9),
            new DataPoint(52, 110.1),
            new DataPoint(54, 111.2),
            new DataPoint(56, 112.4),
            new DataPoint(58, 113.6),
            new DataPoint(60, 114.8)
    });
    graph.addSeries(series4);
    series4.setColor(Color.rgb(204,153,0));
    series4.setThickness(3);
    series4.setTitle("85th");

    //97th percentile
    LineGraphSeries<DataPoint> series5 = new LineGraphSeries<DataPoint>(new DataPoint[] {
            new DataPoint(0,53.4),
            new DataPoint(2, 62.2),
            new DataPoint(4, 67.8),
            new DataPoint(6, 71.6),
            new DataPoint(8, 74.7),
            new DataPoint(10,77.6),
            new DataPoint(12, 80.2),
            new DataPoint(14, 82.7),
            new DataPoint(16, 85.1),
            new DataPoint(18, 87.3),
            new DataPoint(20,89.5),
            new DataPoint(22, 91.6),
            new DataPoint(24, 93.6),
            new DataPoint(24,92.9),
            new DataPoint(26, 94.8),
            new DataPoint(28, 96.6),
            new DataPoint(30,98.3),
            new DataPoint(32, 100),
            new DataPoint(34, 101.5),
            new DataPoint(36, 103.1),
            new DataPoint(38, 104.5),
            new DataPoint(40,105.9),
            new DataPoint(42, 107.3),
            new DataPoint(44, 108.6),
            new DataPoint(46, 109.9),
            new DataPoint(48, 111.2),
            new DataPoint(50,112.5),
            new DataPoint(52, 113.7),
            new DataPoint(54, 115),
            new DataPoint(56, 116.2),
            new DataPoint(58, 117.4),
            new DataPoint(60, 118.7)
    });
    graph.addSeries(series5);
    series5.setColor(Color.rgb(128,0,0));
    series5.setThickness(3);
    series5.setTitle("97th");
}
public void displayPercentileforGirls(){
    /**
     * FOR GIRLS NAPOD NI SYA
     **/
    //3rd percentile
     LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
     new DataPoint(0,45.6),
     new DataPoint(2, 53.2),
     new DataPoint(4, 58),
     new DataPoint(6, 61.5),
     new DataPoint(8, 64.3),
     new DataPoint(10,66.8),
     new DataPoint(12, 69.2),
     new DataPoint(14, 71.3),
     new DataPoint(16, 73.3),
     new DataPoint(18, 75.2),
     new DataPoint(20,77),
     new DataPoint(22, 78.7),
     new DataPoint(24, 80.3),
     new DataPoint(24,79.6),
     new DataPoint(26, 81.2),
     new DataPoint(28, 82.6),
     new DataPoint(30,84),
     new DataPoint(32, 85.4),
     new DataPoint(34, 86.7),
     new DataPoint(36, 87.9),
     new DataPoint(38, 89.1),
     new DataPoint(40,90.3),
     new DataPoint(42, 91.4),
     new DataPoint(44, 92.5),
     new DataPoint(46, 93.6),
     new DataPoint(48, 94.6),
     new DataPoint(50,95.7),
     new DataPoint(52, 96.7),
     new DataPoint(54, 97.6),
     new DataPoint(56, 98.6),
     new DataPoint(58, 99.6),
     new DataPoint(60, 100.5)
     });
     graph.addSeries(series);
     series.setColor(Color.rgb(226,91,34));
     series.setThickness(3);
     series.setTitle("3rd");

     //15th percentile
     LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>(new DataPoint[] {
     new DataPoint(0,47.2),
     new DataPoint(2, 55),
     new DataPoint(4, 59.8),
     new DataPoint(6, 63.4),
     new DataPoint(8, 66.3),
     new DataPoint(10,68.9),
     new DataPoint(12, 71.3),
     new DataPoint(14, 73.6),
     new DataPoint(16, 75.7),
     new DataPoint(18, 77.7),
     new DataPoint(20,79.6),
     new DataPoint(22, 81.4),
     new DataPoint(24, 83.1),
     new DataPoint(24,82.4),
     new DataPoint(26, 84),
     new DataPoint(28, 85.5),
     new DataPoint(30,87),
     new DataPoint(32, 88.4),
     new DataPoint(34, 89.8),
     new DataPoint(36, 91.1),
     new DataPoint(38, 92.4),
     new DataPoint(40,93.6),
     new DataPoint(42, 94.8),
     new DataPoint(44, 96),
     new DataPoint(46, 97.2),
     new DataPoint(48, 98.3),
     new DataPoint(50,99.4),
     new DataPoint(52, 100.4),
     new DataPoint(54, 101.5),
     new DataPoint(56, 102.5),
     new DataPoint(58, 103.5),
     new DataPoint(60, 104.5)
     });
     graph.addSeries(series2);
     series2.setColor(Color.rgb(51,204,51));
     series2.setThickness(3);
     series2.setTitle("15th");

     //50th percentile
     LineGraphSeries<DataPoint> series3 = new LineGraphSeries<DataPoint>(new DataPoint[] {
     new DataPoint(0,49.1),
     new DataPoint(2, 57.1),
     new DataPoint(4, 62.1),
     new DataPoint(6, 65.7),
     new DataPoint(8, 68.7),
     new DataPoint(10,71.5),
     new DataPoint(12, 74),
     new DataPoint(14, 76.4),
     new DataPoint(16, 78.6),
     new DataPoint(18, 80.7),
     new DataPoint(20,82.7),
     new DataPoint(22, 84.6),
     new DataPoint(24, 86.4),
     new DataPoint(24,85.7),
     new DataPoint(26, 87.4),
     new DataPoint(28, 89.1),
     new DataPoint(30,90.7),
     new DataPoint(32, 92.2),
     new DataPoint(34, 93.6),
     new DataPoint(36, 95.1),
     new DataPoint(38, 96.4),
     new DataPoint(40,97.7),
     new DataPoint(42, 99),
     new DataPoint(44, 100.3),
     new DataPoint(46, 101.5),
     new DataPoint(48, 102.7),
     new DataPoint(50,103.9),
     new DataPoint(52, 105),
     new DataPoint(54, 106.2),
     new DataPoint(56, 107.3),
     new DataPoint(58, 108.4),
     new DataPoint(60, 109.4)
     });
     graph.addSeries(series3);
     series3.setColor(Color.rgb(255,51,153));
     series3.setThickness(3);
     series3.setTitle("50th");

     //85th percentile
     LineGraphSeries<DataPoint> series4 = new LineGraphSeries<DataPoint>(new DataPoint[] {
     new DataPoint(0,51.1),
     new DataPoint(2, 59.2),
     new DataPoint(4, 64.3),
     new DataPoint(6, 68.1),
     new DataPoint(8, 71.2),
     new DataPoint(10,74),
     new DataPoint(12, 76.7),
     new DataPoint(14, 79.2),
     new DataPoint(16, 81.5),
     new DataPoint(18, 83.7),
     new DataPoint(20,85.8),
     new DataPoint(22, 87.8),
     new DataPoint(24, 89.8),
     new DataPoint(24,89.1),
     new DataPoint(26, 90.9),
     new DataPoint(28, 92.7),
     new DataPoint(30,94.3),
     new DataPoint(32, 95.9),
     new DataPoint(34, 97.5),
     new DataPoint(36, 99),
     new DataPoint(38, 100.5),
     new DataPoint(40,101.9),
     new DataPoint(42, 103.3),
     new DataPoint(44, 104.6),
     new DataPoint(46, 105.9),
     new DataPoint(48, 107.2),
     new DataPoint(50,108.4),
     new DataPoint(52, 109.7),
     new DataPoint(54, 110.9),
     new DataPoint(56, 112.1),
     new DataPoint(58, 113.2),
     new DataPoint(60, 114.4)
     });
     graph.addSeries(series4);
     series4.setColor(Color.rgb(204,153,0));
     series4.setThickness(3);
     series4.setTitle("85th");

     //97th percentile
     LineGraphSeries<DataPoint> series5 = new LineGraphSeries<DataPoint>(new DataPoint[] {
     new DataPoint(0,52.7),
     new DataPoint(2, 60.9),
     new DataPoint(4, 66.2),
     new DataPoint(6, 70),
     new DataPoint(8, 73.2),
     new DataPoint(10,76.1),
     new DataPoint(12, 78.9),
     new DataPoint(14, 81.4),
     new DataPoint(16, 83.9),
     new DataPoint(18, 86.2),
     new DataPoint(20,88.4),
     new DataPoint(22, 90.5),
     new DataPoint(24, 92.5),
     new DataPoint(24,91.8),
     new DataPoint(26, 93.7),
     new DataPoint(28, 95.6),
     new DataPoint(30,97.3),
     new DataPoint(32, 99),
     new DataPoint(34, 100.6),
     new DataPoint(36, 102.2),
     new DataPoint(38, 103.7),
     new DataPoint(40,105.2),
     new DataPoint(42, 106.7),
     new DataPoint(44, 108.1),
     new DataPoint(46, 109.5),
     new DataPoint(48, 110.8),
     new DataPoint(50,112.1),
     new DataPoint(52, 113.4),
     new DataPoint(54, 114.7),
     new DataPoint(56, 116),
     new DataPoint(58, 117.2),
     new DataPoint(60, 118.4)
     });
     graph.addSeries(series5);
     series5.setColor(Color.rgb(128,0,0));
     series5.setThickness(3);
     series5.setTitle("97th");

}


}


