package ge.gov.air.airgov;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;


public class DetectConnection extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.load_activity);
        TextView textView = findViewById(R.id.internet_conn);
        textView.setVisibility(View.VISIBLE);

        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    //check if connected!
                    while (!isConnected(DetectConnection.this)) {
                        //Wait to connect
                        Thread.sleep(3000);
                    }

                    Intent i = new Intent(DetectConnection.this, LoadActivity.class);
                    overridePendingTransition(0, 0);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(i);

                } catch (Exception e) {
                }
            }
        };
        t.start();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
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
}
