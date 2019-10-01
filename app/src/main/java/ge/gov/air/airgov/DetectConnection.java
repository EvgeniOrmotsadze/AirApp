package ge.gov.air.airgov;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;


public class DetectConnection extends AppCompatActivity {

    private Handler handler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.load_activity);
        TextView textView = findViewById(R.id.internet_conn);
        textView.setVisibility(View.VISIBLE);

        if(handler == null)
            handler = new Handler();

        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                recreate();
            }
        }, 10);


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
