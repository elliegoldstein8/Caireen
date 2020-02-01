package com.example.queenelizabethviii.caireen2;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.android.volley.VolleyLog.TAG;

public class articles_immunization extends Fragment {
    TextView resultTextView;
    ProgressBar progressBar;

    private static final String TAG = "searchApp";
    static String result = null;
    Integer responseCode = null;
    String responseMessage = "";
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(); //para maidentify kinsa na user sa firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();  //for storing data to firebase
    DatabaseReference myRef = database.getReference("Users").child(firebaseAuth.getCurrentUser().getUid()).child("baby"); //reference
    DatabaseReference lastlastref, lastref;
    String babyid, vaccine;
    ListView listViewFeed;
    List<FeedItem> listFeedItems;
    ListAdapter adapterFeed;
    // Your API key
    // TODO replace with your value
    String apikey = "AIzaSyDlVTFYE5o34RVZOMcBd88BHVYhEYIE0TM";

    // Your Search Engine ID
    // TODO replace with your value
    String cx = "016635520467635091546:du6q3nauzma";



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.articles_immunization,container,false);
        Log.d(TAG, "**** APP START ****");
        resultTextView = (TextView) view.findViewById(R.id.noimmrecord); //referencing output textview
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar4); //referencing loading icon
        listViewFeed = (ListView) view.findViewById(R.id.listviewfeed3);


        listFeedItems = new ArrayList<>();
        adapterFeed = new ArrayAdapter<FeedItem>(
                getActivity(), R.layout.rowlayout_articles, R.id.labelarticles, listFeedItems);
        listViewFeed.setAdapter(adapterFeed);
        listViewFeed.setOnItemClickListener(listViewFeedOnItemClickListener);

        getBabyId();



        return view;
    }

    AdapterView.OnItemClickListener listViewFeedOnItemClickListener =
            new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    FeedItem clickedFeedItem = (FeedItem) parent.getItemAtPosition(position);
                    String url = clickedFeedItem.getLink();
                    Uri uri = Uri.parse(url);
                    new CustomTabsIntent.Builder()
                            .build()
                            .launchUrl(getActivity(), uri);
                }
            };

    public void getBabyId(){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                // Log.d(TAG, "Value is: " + value);
                for (final DataSnapshot child : dataSnapshot.getChildren()) {
                    // Log.i("TAG", "child key = " + child.getKey());
                    String key = child.getKey();
                    //String babyname = dataSnapshot.getValue(String.class);
                    final DatabaseReference newRef = myRef.child(key);
                    final ValueEventListener valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            babyid = child.child("baby_id").getValue(String.class);
                            if (babyid != null) {
                                lastlastref = myRef.child(babyid);
                                lastref = lastlastref.child("baby_features").child("immunization_records");
                                Query lastQuery = lastref.orderByKey().limitToLast(1);
                                lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot child: dataSnapshot.getChildren()){
                                            vaccine = child.child("vaccine").getValue(String.class);
                                            //  startSearch();
                                            if (vaccine != null){
                                                lastref.keepSynced(true);
                                                resultTextView.setVisibility(View.GONE);
                                                String searchString = vaccine + " vaccine baby articles";
                                                String urlString = "https://www.googleapis.com/customsearch/v1?q=" + searchString + "&key=" + apikey + "&cx=" + cx + "&alt=json";
                                                progressBar.setVisibility(View.VISIBLE);
                                                new JsonTask(listFeedItems, listViewFeed).execute(urlString);

                                            }
                                            else{
                                                resultTextView.setVisibility(View.VISIBLE);
                                            }
                                        }

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
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


    }


    /*
 JsonTask:
 AsyncTask to download and parse JSON Feed of blogspot in background
  */
    private class JsonTask extends AsyncTask<String, FeedItem, String> {

        List<FeedItem> jsonTaskList;
        ListView jsonTaskListView;

        public JsonTask(List<FeedItem> targetList, ListView targetListView) {
            super();
            jsonTaskList = targetList;
            jsonTaskListView = targetListView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            jsonTaskList.clear();
            jsonTaskListView.invalidateViews();
        }

        @Override
        protected String doInBackground(String... params) {

                try {
                    final String queryResult = sendQuery(params[0]);
                    parseQueryResult(queryResult);
                } catch (IOException e) {
                    e.printStackTrace();

                    final String eString = e.toString();

                    if (getActivity()!=null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(),
                                        eString,
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    //DIRI KA MAGCHANGE BANDA

                } catch (JSONException e) {
                    e.printStackTrace();

                    final String eString = e.toString();
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(),
                                        eString,
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            return null;
        }

        @Override
        protected void onProgressUpdate(FeedItem... values) {
            FeedItem newItem = values[0];
            jsonTaskList.add(newItem);
            jsonTaskListView.invalidateViews();
        }

        private String sendQuery(String query) throws IOException {
            String queryReturn = "";
            URL queryURL = new URL(query);

            HttpURLConnection httpURLConnection = (HttpURLConnection)queryURL.openConnection();

            if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStreamReader inputStreamReader =
                        new InputStreamReader(httpURLConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(
                        inputStreamReader, 8192);
                String line = null;
                while((line = bufferedReader.readLine()) != null){
                    queryReturn += line;
                }

                bufferedReader.close();
            }


            return queryReturn;
        }


        private void parseQueryResult(String json) throws JSONException{
            JSONObject jsonObject = new JSONObject(json);
            final JSONArray jsonArray_entry = jsonObject.getJSONArray("items");
            if (getActivity() !=null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (jsonArray_entry == null) {
                            Toast.makeText(getActivity(),
                                    "jsonArray_entry == null",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(),
                                    String.valueOf(jsonArray_entry.length()),
                                    Toast.LENGTH_LONG).show();
                            for (int i = 0; i < jsonArray_entry.length(); i++) {
                                try {
                                    JSONObject thisEntry = (JSONObject) jsonArray_entry.get(i);
                                    String thisEntryTitleString = thisEntry.getString("title");
                                    String thisEntryTitleLink = thisEntry.getString("link");
                                    String thisEntryTitleSnippet = thisEntry.getString("snippet");

                                    FeedItem thisElement = new FeedItem(
                                            thisEntryTitleString,
                                            thisEntryTitleLink,
                                            thisEntryTitleSnippet);
                                    publishProgress(thisElement);
                                    progressBar.setVisibility(View.GONE);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                                    adb.setTitle("Check your online connection.");
                                    adb.setIcon(R.drawable.ic_perm_scan_wifi_black_24dp);
                                    adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                                    adb.show();
                                }

                            }
                        }

                    }
                });
            }
        }

    }


}
