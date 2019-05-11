package ge.gov.air.airgov;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class LoadActivity extends AppCompatActivity {

    String data;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load_activity);

        TextView textView = findViewById(R.id.internet_conn);
        textView.setVisibility(View.GONE);

    //    ActivityCompat.requestPermissions(LoadActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);


        SharedPreferences prefs2 = getSharedPreferences("pref", MODE_PRIVATE);
        long lastTime = prefs2.getLong("lastTime",-1);


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

        if(!lst.before(new Date(nowTime.getTime() - (20 * 60000))) && data != null){
            Intent intent = new Intent(LoadActivity.this, App.class);
            //        intent.putExtra("currentStation","");
            startActivity(intent);
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
                Log.d("dates","first " + formatFirst + " " + formatSecond);

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "Without Location App'll show random station", Toast.LENGTH_LONG).show();
                }
                Intent intent = new Intent(LoadActivity.this, App.class);
                intent.putExtra("data", data);
                //         intent.putExtra("currentStation","");
                startActivity(intent);
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
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
            ActivityCompat.requestPermissions(LoadActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }
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

}
