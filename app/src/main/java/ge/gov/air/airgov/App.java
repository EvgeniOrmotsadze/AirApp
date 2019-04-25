package ge.gov.air.airgov;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.gms.location.FusedLocationProviderClient;

import java.util.ArrayList;

public class App extends AppCompatActivity  implements OnChartValueSelectedListener {

    private LineChart chart;

    private FusedLocationProviderClient fusedLocationClient;

    private LinearLayout stations;

    private  DrawerLayout drawerLayout;

    private TextView mainTextView;
    private TextView mainTextViewCube;

    private boolean clickRadar;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setTitle(null);
        ActionBar actionbar = getSupportActionBar();

        actionbar.setHomeAsUpIndicator(R.mipmap.white_menu_icon);
        actionbar.setDisplayHomeAsUpEnabled(true);

        stations = (LinearLayout) findViewById(R.id.station_layout);
        stations.setVisibility(View.GONE);

        mainTextView = (TextView)findViewById(R.id.main_text_view);
        mainTextViewCube = (TextView)findViewById(R.id.main_text_view_cube);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navView = (NavigationView) findViewById(R.id.navigation);



        chart = (LineChart) findViewById(R.id.lineChart1);

        chart.getDescription().setEnabled(false);

        // enable touch gestures
      //  chart.setTouchEnabled(true);

       // chart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setHighlightPerDragEnabled(false);
        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(true);

        // set an alternative background color
        chart.setBackgroundColor(ContextCompat.getColor(this, R.color.lightGreen));
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.setBorderColor(ContextCompat.getColor(this, R.color.chartFillColor));


        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 5));
        entries.add(new Entry(1, 1));
        entries.add(new Entry(2, 2));
        entries.add(new Entry(3, 4));
        entries.add(new Entry(4, 8));
        entries.add(new Entry(5, 2));
        entries.add(new Entry(6, 14));
        entries.add(new Entry(7, 6));
        entries.add(new Entry(8, 9));
        entries.add(new Entry(9, 12));
        entries.add(new Entry(10, 16));
        entries.add(new Entry(11, 3));
        entries.add(new Entry(12, 9));
        entries.add(new Entry(13, 19));
        entries.add(new Entry(14, 11));
        entries.add(new Entry(15, 3));
        entries.add(new Entry(16, 10));
        entries.add(new Entry(17, 3));
        entries.add(new Entry(18, 13));
        entries.add(new Entry(19, 21));
        entries.add(new Entry(20, 5));
        entries.add(new Entry(21, 15));
        entries.add(new Entry(22, 3));
        entries.add(new Entry(23, 9));

        LineDataSet dataSet = new LineDataSet(entries, "Time");

        dataSet.setColor(ContextCompat.getColor(this,R.color.chartFillColor));
        dataSet.setFillColor(ContextCompat.getColor(this,R.color.chartFillColor));
        dataSet.setDrawFilled(true);
        dataSet.setDrawValues(false);
        dataSet.setCircleRadius(3f);
        dataSet.setLineWidth(2f);
        dataSet.setDrawCircles(true);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawCircles(true);
        dataSet.setDrawHorizontalHighlightIndicator(false);
        dataSet.setDrawVerticalHighlightIndicator(false);
        dataSet.setDrawHighlightIndicators(true);

        //****
        // Controlling X axis
        XAxis xAxis = chart.getXAxis();
        // Set the xAxis position to bottom. Default is top
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        //Customizing x axis value
        final String[] months = new String[]{"00:00", "01:00", "02:00", "03:00","04:00","05:00","06:00","07:00","08:00",
                "09:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00",
                "21:00","22:00","23:00"};

        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return months[(int) value];
            }
        };
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);
        xAxis.setTextColor(ContextCompat.getColor(this,R.color.chartFillColor));
        xAxis.setGridColor(ContextCompat.getColor(this,R.color.chartFillColor));
        xAxis.setDrawAxisLine(false);
        //***
        // Controlling right side of y axis
        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setEnabled(false);

        //***
        // Controlling left side of y axis
        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setGranularity(1f);
        yAxisLeft.setTextColor(ContextCompat.getColor(this,R.color.chartFillColor));
        yAxisLeft.setGridColor(ContextCompat.getColor(this,R.color.chartFillColor));
        yAxisLeft.setDrawAxisLine(true);
        // Setting Data
        LineData data = new LineData(dataSet);
        chart.setData(data);
     //   ChartMarker chartMarker = new ChartMarker(this);
      //  chart.setMarker(chartMarker);
        chart.setDrawMarkers(true);
      //  chart.setVisibleXRange(0,10);
        chart.animateX(1000, Easing.EasingOption.EaseInBounce);
       // chart.moveViewToX(10);
        chart.invalidate();

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.d("logger ", e.toString());
    }

    @Override
    public void onNothingSelected() {

    }


//        fusedLocationClient.getLastLocation()
//                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//
//                        if (location != null) {
//                           Toast.makeText(getApplicationContext(),location.toString(),Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });






    private class CustomWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!DetectConnection.checkInternetConnection(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_SHORT).show();
            } else {
                view.loadUrl(url);
            }
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        Drawable drawable = item.getIcon();

        switch(itemId) {
            // Android home
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.showList:
                Intent intent = new Intent(App.this, StationActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
                return true;

        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action_bar, menu);
        for(int i = 0; i < menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if(drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            }
        }
        Drawable drawable = menu.findItem(R.id.radar).getIcon();
        drawable.mutate();
        drawable.setColorFilter(getResources().getColor(R.color.selected), PorterDuff.Mode.SRC_ATOP);
        return true;
    }

    public void showStations(View view){
        if(stations.getVisibility() == View.VISIBLE){
            stations.setVisibility(View.GONE);
            mainTextView.setVisibility(View.VISIBLE);
            mainTextViewCube.setVisibility(View.VISIBLE);
            LinearLayout showDataLayout = (LinearLayout) findViewById(R.id.showDataLayout);
            LinearLayout.LayoutParams showDataParam = (LinearLayout.LayoutParams)showDataLayout.getLayoutParams();
            showDataParam.weight = 2;
            showDataLayout.setLayoutParams(showDataParam);

            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
            linearLayout.setWeightSum(3);
        }else {
            stations.setVisibility(View.VISIBLE);
            mainTextView.setVisibility(View.GONE);
            mainTextViewCube.setVisibility(View.GONE);
            LinearLayout showDataLayout = (LinearLayout) findViewById(R.id.showDataLayout);
            LinearLayout.LayoutParams showDataParam = (LinearLayout.LayoutParams)showDataLayout.getLayoutParams();
            showDataParam.weight = 1;
            showDataLayout.setLayoutParams(showDataParam);
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
            linearLayout.setWeightSum(2);
        }
    }

    public void refreshData(View view){
        Toast.makeText(getApplicationContext(), "refresh", Toast.LENGTH_SHORT).show();
    }
}
