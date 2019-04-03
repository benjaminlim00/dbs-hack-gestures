package com.example.benjamin.dbsgestures;

import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class VerifyGesture extends AppCompatActivity {
    DatabaseReference mRootDatabaseRef = FirebaseDatabase.getInstance().getReference();
    String CHILD_NODE_PART1 = "jsonData";
    String CHILD_NODE_TRAINING= "training_set";
    GestureLibrary lib;
    String strData;
    String trainingData;
    int failcount = 3;
    ArrayList<String> dataSubmit = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        lib = GestureLibraries.fromRawResource(this, R.raw.gesture);
        if (!lib.load()) {
            finish();
        }
        final GestureOverlayView gesture = findViewById(R.id.gesture);

        gesture.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float pressure = event.getPressure();
//                float area = event.getSize();
//                Log.d("tag", "onTouch: pressed!" + pressure);



                float x = event.getX();
                float y = event.getY();


                strData = pressure + "," + "(" + x + "," + y + ")";


                dataSubmit.add(strData);
//                Log.d("tag", "data: " + dataSubmit);

                return true;
            }

        });


        gesture.addOnGesturePerformedListener(new GestureOverlayView.OnGesturePerformedListener() {
            @Override
            public void onGesturePerformed(GestureOverlayView gestureOverlayView, Gesture gesture) {
                ArrayList<Prediction> predictionArrayList = lib.recognize(gesture);
                for (Prediction prediction : predictionArrayList) {
                    if (prediction.score > 1.2) {
                        //overwrite one row of data, first we have to clear the old data
                        mRootDatabaseRef.child(CHILD_NODE_PART1).setValue("cleared");
                        for (int i=0;i<dataSubmit.size();i++) {
                            Log.d("tag", dataSubmit.get(i));
                            mRootDatabaseRef.child(CHILD_NODE_PART1).child(Integer.toString(i)).setValue(dataSubmit.get(i));
                        }

                        //append to list of data for training
                        String uniqueID = UUID.randomUUID().toString();

                        for (int i=0;i<dataSubmit.size();i++) {
                            mRootDatabaseRef.child(CHILD_NODE_TRAINING).child(uniqueID).child(Integer.toString(i)).setValue(dataSubmit.get(i));
                        }

                        Toast.makeText(VerifyGesture.this, "Successfully verified!", Toast.LENGTH_LONG).show();
                        dataSubmit.clear(); //clear the data. BUG FIX


                        Intent i = new Intent(VerifyGesture.this, VerifiedDone.class);
                        startActivity(i);

                        //now we countdown 3 sec and app destroy




                    } else {
                        dataSubmit.clear();
                        if (failcount == 1) {
                            Toast.makeText(VerifyGesture.this, "Failed, please try again!" + "\n( " + failcount + " try left )", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(VerifyGesture.this, "Failed, please try again!" + "\n( " + failcount + " tries left )", Toast.LENGTH_LONG).show();
                        }

                        failcount -= 1;

                        if (failcount == -1) {
                            Toast.makeText(VerifyGesture.this, "Exceeded number of tries", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(VerifyGesture.this, VerifiedFail.class);
                            startActivity(i);
                        }

                    }
                }
            }
        });
        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();



    }



    //need this to handle json for pushing to firebase
    public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if(json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }
}


