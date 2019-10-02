package ge.gov.air.airgov;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import ge.gov.air.airgov.model.Stations;

public class LoadActivity extends AppCompatActivity implements LocationListener {

    String data;
    protected LocationManager locationManager;
    private Location lastKnownLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load_activity);

        TextView textView = findViewById(R.id.internet_conn);
        textView.setVisibility(View.GONE);


        if(checkLocationPermission()){
            continueAcitvity();
        }


    }

    private void continueAcitvity(){
        SharedPreferences prefs2 = getSharedPreferences("pref", MODE_PRIVATE);
        long lastTime = prefs2.getLong("lastTime",-1);
        data = prefs2.getString("data",null);



        Date dt = new Date();

        if(lastTime == -1){

            lastTime = dt.getTime();
            SharedPreferences.Editor editor = getSharedPreferences("pref", MODE_PRIVATE).edit();
            editor.putLong("lastTime", new Date().getTime());
            editor.apply();
        }

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

        Date lst = new Date(lastTime);
        Date nowTime = new Date();

//        Log.d("timess", lst.toString() + " now :" +new Date(nowTime.getTime() - (20 * 60000)).toString());

        if(!lst.before(new Date(nowTime.getTime() - (20 * 60000))) && data != null){
            chooseActivityForLoading();
        }else {
            SharedPreferences.Editor editor = getSharedPreferences("pref", MODE_PRIVATE).edit();
            editor.putLong("lastTime", new Date().getTime());
            editor.apply();

            if (!isConnected(getApplicationContext())) {
                Intent i = new Intent(LoadActivity.this, DetectConnection.class);
                overridePendingTransition(0, 0);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
            } else {
                Date nowDate = new Date();
                String formatFirst = df.format(nowDate);
                Calendar cal = Calendar.getInstance();
                cal.setTime(nowDate);
                cal.add(Calendar.DATE, -1);
                Date yesterday = cal.getTime();
                String formatSecond = df.format(yesterday);

                try {
                    Log.d("link", "http://air.gov.ge/api/get_data_1hour/?from_date_time=" + formatSecond + "&to_date_time=" + formatFirst + "&station_code=all&municipality_id=all&substance=all&format=json");
                    new HttpAsyncTask().execute("http://air.gov.ge/api/get_data_1hour/?from_date_time=" + formatSecond + "&to_date_time=" + formatFirst + "&station_code=all&municipality_id=all&substance=all&format=json").get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

    private int FASTEST_INTERVAL = 20000; // use whatever suits you

    private long locationUpdatedAt = Long.MIN_VALUE;

    @Override
    public void onLocationChanged(Location location) {
        if(lastKnownLocation == null){
            lastKnownLocation = location;
            locationUpdatedAt = System.currentTimeMillis();
        } else {
            long secondsElapsed = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - locationUpdatedAt);
            if (secondsElapsed >= TimeUnit.MILLISECONDS.toSeconds(FASTEST_INTERVAL)){
                // check location accuracy here
                lastKnownLocation = location;
                locationUpdatedAt = System.currentTimeMillis();
            }
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            data = result;
            SharedPreferences.Editor editor = getSharedPreferences("pref", MODE_PRIVATE).edit();
            editor.putString("data", result);
            editor.apply();
            chooseActivityForLoading();
       //     ActivityCompat.requestPermissions(LoadActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void chooseActivityForLoading() {
        List<Stations> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {
                String st = jsonArray.get(i).toString();
                JSONObject jsonObject = new JSONObject(st);
                Stations stations = new Stations();
                stations.setLat(jsonObject.getDouble("lat"));
                stations.setLongs(jsonObject.getDouble("long"));
                stations.setRepresentDistance(jsonObject.getString("representativeness_distance"));
                list.add(stations);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SharedPreferences prefs2 = getSharedPreferences("pref", MODE_PRIVATE);
        boolean isDefault = prefs2.getBoolean("default_screen",false);
        Log.d("isDefault",isDefault+"");
        Stations stations = getNearestLocation(list);
        if(stations.getDistanceInMeter() > Double.parseDouble(stations.getRepresentDistance()) && !isDefault){
            Intent  intent = new Intent(LoadActivity.this, MapActivity.class);
            intent.putExtra("data",data);
            intent.putExtra("lat", lastKnownLocation.getLatitude());
            intent.putExtra("long", lastKnownLocation.getLongitude());
            startActivity(intent);
        }else {
            Intent intent = new Intent(LoadActivity.this, App.class);
            intent.putExtra("data", data);
            startActivity(intent);
        }
    }

    private Stations getNearestLocation(List<Stations> list) {
        Stations first = list.get(0);
        Location loc1 = new Location("");
        loc1.setLatitude(first.getLat());
        loc1.setLongitude(first.getLongs());
        float distanceInMeters = loc1.distanceTo(lastKnownLocation);

        for(int i = 1; i < list.size(); i++){
            loc1 = new Location("");
            loc1.setLatitude(list.get(i).getLat());
            loc1.setLongitude(list.get(i).getLongs());

            if(distanceInMeters > loc1.distanceTo(lastKnownLocation)){
                distanceInMeters = loc1.distanceTo(lastKnownLocation);
                first = list.get(i);
                first.setDistanceInMeter(distanceInMeters);
            }
        }
        return first;
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        return networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED;
    }

    public static String GET(String url) {

        try {
            URL mUrl = new URL(url);
            HttpURLConnection httpConnection = (HttpURLConnection) mUrl.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Content-length", "0");
            httpConnection.setUseCaches(false);
            httpConnection.setAllowUserInteraction(false);
            httpConnection.setConnectTimeout(100000);
            httpConnection.setReadTimeout(100000);

            httpConnection.connect();

            int responseCode = httpConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                return sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);

            return false;
        } else {
            getLocation();
            return true;
        }
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            List<String> providers = locationManager.getProviders(true);
            Location bestLocation = null;
            for (String provider : providers) {
                Location l = locationManager.getLastKnownLocation(provider);
//                Log.d("location service",l.toString());
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    bestLocation = l;
                }
            }
            lastKnownLocation = bestLocation;

        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    continueAcitvity();
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if(lastKnownLocation == null) {
                            Intent intent = getIntent();
                            overridePendingTransition(0, 0);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(intent);
                            getLocation();
                        }
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }


}
