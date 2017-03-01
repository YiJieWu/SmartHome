package com.example.yijiewu.smarthome;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by yijiewu on 2/20/17.
 */

public class MyApplication extends Application {
    private BeaconManager beaconManager;
    @Override
    public void onCreate() {
        super.onCreate();
        //For the Living Room
        //mint
        final Region region1 = new Region(
                "beacon 09",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                30194,9178);
        //For the Room Kitchen
        //blueberry
        final  Region region2 = new Region(
                "beacon 10",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                7122, 62286);
        //For the Room Bedroom
        //beacon Ice
        final  Region region3 = new Region(
                "beacon 11",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                38813, 15738);
        //For the Room Bathroom
        final  Region region4 = new Region(
                "beacon 12",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                2087, 49384);
        //Create the beacon Manager
        beaconManager = new BeaconManager(getApplicationContext());
        //Connect the beacon manager to different beacons
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startMonitoring(region1);
                beaconManager.startMonitoring(region2);
                beaconManager.startMonitoring(region3);
                beaconManager.startMonitoring(region4);
            }
        });

        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            //When user enter the region
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                if (region.getIdentifier().equals("beacon 09")) {
                    // do something
                    showNotification("Warning", "You are in the Living Room!!!");
                    new SendData().execute("nine");

                } else if(region.getIdentifier().equals("beacon 10")){
                    showNotification("Warning", "You are in the Kitchen!!!");
                    new SendData().execute("ten");

                }else if(region.getIdentifier().equals("beacon 11")) {
                    showNotification("Warning", "You are in the Bedroom!!!");
                    new SendData().execute("eleven");

                }else if(region.getIdentifier().equals("beacon 12")){
                    showNotification("Warning", "You are in the Bathroom!!!");
                    new SendData().execute("twelve");
                }else{
                    //do Nothing
                }
            }

            @Override
            public void onExitedRegion(Region region) {

            }
        });
    }

    public void showNotification(String title, String message) {
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[] { notifyIntent }, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

    //send data to the server, the input for the do in background will be the number identifier of the estimote
    public class SendData extends AsyncTask<String,Void, Void> {
        @Override
        protected Void doInBackground(String... beacon_num) {
            Log.d("send data", "IN THE BACKEND");
            Log.d("beacon num is", beacon_num[0]);


            OutputStreamWriter writer = null;
            BufferedReader reader;
            //get and format the time stamp
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            String currentDateTimeString=sdf.format(new Date());

            try {
                // Create data variable for sent values to server
                String query_string = null;

                if(beacon_num[0].equals("nine")){
                    query_string = URLEncoder.encode("device", "UTF-8")
                            + "=" + URLEncoder.encode("Living Room Estimote 09", "UTF-8");

                    //I am just going to use 1 represent it's in beacon1's range
                    query_string += "&" + URLEncoder.encode("action", "UTF-8") + "="
                            + URLEncoder.encode("Enter Living Room", "UTF-8");

                    query_string += "&" + URLEncoder.encode("timestamp", "UTF-8") + "='"
                            + URLEncoder.encode(currentDateTimeString, "UTF-8") + "'";

                    query_string += "&" + URLEncoder.encode("action_code", "UTF-8") + "='"
                            + URLEncoder.encode("09", "UTF-8") + "'";

                }else if(beacon_num[0].equals("ten")){
                    query_string = URLEncoder.encode("device", "UTF-8")
                            + "=" + URLEncoder.encode("Kitchen Estimote 10", "UTF-8");

                    //I am just going to use 1 represent it's in beacon1's range
                    query_string += "&" + URLEncoder.encode("action", "UTF-8") + "="
                            + URLEncoder.encode("Enter Kitchen", "UTF-8");

                    query_string += "&" + URLEncoder.encode("timestamp", "UTF-8") + "='"
                            + URLEncoder.encode(currentDateTimeString, "UTF-8") + "'";

                    query_string += "&" + URLEncoder.encode("action_code", "UTF-8") + "='"
                            + URLEncoder.encode(String.valueOf(10), "UTF-8") + "'";

                }else if(beacon_num[0].equals("eleven")){
                    query_string = URLEncoder.encode("device", "UTF-8")
                            + "=" + URLEncoder.encode("Bedroom Estimote 11", "UTF-8");

                    //I am just going to use 1 represent it's in beacon1's range
                    query_string += "&" + URLEncoder.encode("action", "UTF-8") + "="
                            + URLEncoder.encode("Enter Bedroom", "UTF-8");

                    query_string += "&" + URLEncoder.encode("timestamp", "UTF-8") + "='"
                            + URLEncoder.encode(currentDateTimeString, "UTF-8") + "'";

                    query_string += "&" + URLEncoder.encode("action_code", "UTF-8") + "='"
                            + URLEncoder.encode(String.valueOf(11), "UTF-8") + "'";
                }else if(beacon_num[0].equals("twelve")){
                    query_string = URLEncoder.encode("device", "UTF-8")
                            + "=" + URLEncoder.encode("Bathroom Estimote 12", "UTF-8");

                    //I am just going to use 1 represent it's in beacon1's range
                    query_string += "&" + URLEncoder.encode("action", "UTF-8") + "="
                            + URLEncoder.encode("Enter Bathroom", "UTF-8");


                    query_string += "&" + URLEncoder.encode("timestamp", "UTF-8") + "='"
                            + URLEncoder.encode(currentDateTimeString, "UTF-8") + "'";

                    query_string += "&" + URLEncoder.encode("action_code", "UTF-8") + "='"
                            + URLEncoder.encode(String.valueOf(12), "UTF-8") + "'";
                }



                Log.d("Query", query_string);


                String text = "";

                // Defined URL  where to send data
                URL url = null;
                url = new URL("http://murphy.wot.eecs.northwestern.edu/~sme0261/SQLGateway.py");

                // Send POST data request
                URLConnection conn = null;
                conn = (URLConnection) url.openConnection();
                conn.setDoOutput(true);
                writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(query_string);
                writer.flush();

                // Get the server response

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;
                Log.d("before getting  data", "get d from server");

                while ((line = reader.readLine()) != null) {
                    // Append server response in string
                    sb.append(line + "\n");
                    Log.d("getting d String is",line );

                }
                text = sb.toString();
                Log.d("sendData11111", text);


                if(text.contains("overeatingnow")){
                    showNotification("Are you about to eat?","Watch out! You are at risk of overeating today. Consider lowering your intake for the rest of the day.");
                }

                Log.d("sendData", text);
            }catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
