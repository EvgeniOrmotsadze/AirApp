package ge.gov.air.airgov;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class App extends AppCompatActivity implements LocationListener {

    private LineChart chart;

    protected LocationManager locationManager;



    private LinearLayout stations;
    private LinearLayout showTextLayout;
    private LinearLayout actionbarLayout;

    private  DrawerLayout drawerLayout;

    private TextView mainTextView;
    private TextView mainTextViewCube;
    private TextView airQualityTextView;
    private TextView elementName;
    private TextView stationDistance;
    private TextView stationAddress;
    private TextView timeAgo;




    private ListView mListView;

    private ArrayList<String> stationList = new ArrayList<>();

    private ArrayList<String> stationEquipmentList = new ArrayList<>();
    private ArrayList<Substance> stationSubstance = new ArrayList<>();

    private Substance currentSubstance;

    private ArrayList<PointXY> chartDataList;

    private String data;

    private String currentLayoutColor;
    private String textOfCenter;
    private String airQuality;
    private double currentValueScreen;
    private String currentValueUnit;
    private double currentDistanceInMeter;

    private String currentStation;

    private Toolbar myToolbar;
    private ActionBar actionbar;

    private Location lastKnownLocation;
    private boolean isGeorgian;

    public static final String GEO = "ka";
    public static final String ENG = "en";


    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"MissingPermission", "NewApi"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("pref", MODE_PRIVATE);
        String restoredText = prefs.getString("lang", null);
        if(restoredText != null) {
                if(restoredText.equals("ka")){
                    isGeorgian = true;
                }else {
                    isGeorgian = false;
                }
            changeLanguage(restoredText);
        }

        setContentView(R.layout.activity_main);
        getLocation();

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setTitle(null);

        myToolbar.setBackgroundColor(getResources().getColor(R.color.white));
        actionbar = getSupportActionBar();
        Drawable menuIcon = getResources().getDrawable(R.mipmap.white_menu_icon);
        menuIcon.mutate();
        menuIcon.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(menuIcon);


        actionbarLayout = findViewById(R.id.actionBar);

        stations = (LinearLayout) findViewById(R.id.station_layout);
        stations.setVisibility(View.GONE);

        showTextLayout = (LinearLayout) findViewById(R.id.showTextLayout);

        mainTextView = (TextView)findViewById(R.id.main_text_view);
        mainTextViewCube = (TextView)findViewById(R.id.main_text_view_cube);
        airQualityTextView = (TextView) findViewById(R.id.air_condition);
        elementName = (TextView) findViewById(R.id.element_name);
        stationAddress = findViewById(R.id.station_address);
        stationDistance = findViewById(R.id.station_distance);
        timeAgo = findViewById(R.id.time_ago_v);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, myToolbar, R.string.common_google_play_services_update_button, R.string.bottom_sheet_behavior);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        setNavigationViewListner();


        mListView = (ListView) findViewById( R.id.main_page_list_view );


        data = this.getIntent().getStringExtra("data");


        if(data == null){
            SharedPreferences prefs2 = getSharedPreferences("pref", MODE_PRIVATE);
            data = prefs2.getString("data",null);
        }

        try {
            readStations(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }



        currentSubstance = getMaxPopulationSubstance();

        displayData();


        final MainAdapter adapter = new MainAdapter(App.this,stationSubstance,isGeorgian);


        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                for(int i = 0; i < adapter.getCount(); i++){
                    mListView.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                }
                if(adapter.getItem(position).getArrayList().size() > 0) {
                    view.setBackgroundColor(Color.parseColor(adapter.getItem(position).getColor()));
                    mListView.setItemChecked(position, true);
                    setCurrentSubstanceData(position);
                } else {
                    view.setBackgroundColor(getResources().getColor(R.color.showIcon));
                    currentLayoutColor  = "#A3AAA6";
                    airQuality = "";
                    mListView.setItemChecked(position, true);
                    airQualityTextView.setText(airQuality);
                    chartDataList  = new ArrayList<>();
                    textOfCenter = adapter.getItem(position).getName();

                }
                displayData();
                displayChart();
            }
        });

        displayChart();
       // NavigationView navView = (NavigationView) findViewById(R.id.navigation);

    }


    @SuppressLint("ResourceType")
    private void displayChart() {

            chart = (LineChart) findViewById(R.id.lineChart1);
            chart.getDescription().setEnabled(false);

            chart.setScaleEnabled(true);
            chart.setDrawGridBackground(false);
            chart.setHighlightPerDragEnabled(false);
            // if disabled, scaling can be done on x- and y-axis separately
            chart.setPinchZoom(true);
            chart.setDoubleTapToZoomEnabled(false);
            chart.setDragDecelerationEnabled(true);
            chart.setDragDecelerationFrictionCoef(0.88f);
            // set an alternative background color
            chart.setBackgroundColor(Color.parseColor(currentLayoutColor));
            chart.getAxisLeft().setDrawGridLines(false);
            chart.getXAxis().setDrawGridLines(false);
            chart.setBorderColor(ContextCompat.getColor(this, R.color.chartFillColor));

            ArrayList<Entry> entries = new ArrayList<>();
            final String[] months = new String[chartDataList.size()];

            if(isGeorgian)
                changeLanguage("en");
            for (int i = 0; i < chartDataList.size(); i++) {
                DecimalFormat df = new DecimalFormat("##.##");
                String formatted = df.format(chartDataList.get(i).getVal());
                double formattedDouble = 0;
                try {
                     formattedDouble = Double.parseDouble(formatted);
                }catch (NumberFormatException ex){
                    df = new DecimalFormat("#,##");
                    formatted = df.format(chartDataList.get(i).getVal());
                    formattedDouble = Double.parseDouble(formatted);
                }
                entries.add(new Entry(i, (float) formattedDouble));
                months[i] = (chartDataList.get(i).getDate().substring(11,16));
            }
            if(isGeorgian)
                changeLanguage("ka");

            if(months.length > 0)
                setTimeAgoOnTextView(months[months.length - 1]);

            LineDataSet dataSet = new LineDataSet(entries, ""); // times

            dataSet.setColor(ContextCompat.getColor(this, R.color.chartFillColor));
            dataSet.setFillColor(ContextCompat.getColor(this, R.color.chartFillColor));
            dataSet.setDrawFilled(true);
            dataSet.setDrawValues(false);
            dataSet.setLineWidth(3f);
            dataSet.setDrawCircles(false);
            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            dataSet.setDrawHighlightIndicators(false);

            //****
            // Controlling X axis
            XAxis xAxis = chart.getXAxis();
            // Set the xAxis position to bottom. Default is top
            xAxis.setPosition(XAxis.XAxisPosition.TOP);
            //Customizing x axis value



            IAxisValueFormatter formatter = new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return months[(int) value];
                }
            };
            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
            xAxis.setValueFormatter(formatter);
            xAxis.setTextColor(ContextCompat.getColor(this, R.color.chartFillColor));
            xAxis.setGridColor(ContextCompat.getColor(this, R.color.chartFillColor));
            xAxis.setDrawAxisLine(false);

                //***
            // Controlling right side of y axis
            YAxis yAxisRight = chart.getAxisRight();
            yAxisRight.setEnabled(false);

            // Controlling left side of y axis
            YAxis yAxisLeft = chart.getAxisLeft();
            yAxisLeft.setGranularity(1f);
            yAxisLeft.setTextColor(ContextCompat.getColor(this, R.color.chartFillColor));
            yAxisLeft.setGridColor(ContextCompat.getColor(this, R.color.chartFillColor));
            yAxisLeft.setDrawAxisLine(true);
            // Setting Data
            LineData data = new LineData(dataSet);
            if(chartDataList.size() > 0) {
                chart.setData(data);
                ChartMarker chartMarker = new ChartMarker(this);
                chart.setMarker(chartMarker);
                chart.setDrawMarkers(true);
                //set default highlight
                Highlight h = new Highlight((float)chartDataList.size()-1, (float) chartDataList.get(chartDataList.size()-1).getVal(), 0);
                chart.highlightValue(h);

            }else {
                chart.setData(null);
            }



            chart.animateX(1000, Easing.EasingOption.EaseInBounce);
            chart.invalidate();

            chart.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent me) {
                    float tappedX = me.getX();
                    float tappedY = me.getY();
                    MPPointD point = chart.getTransformer(YAxis.AxisDependency.LEFT).getValuesByTouchPoint(tappedX, tappedY);
                    int current = (int) point.x;
                    if(current >= 0 && current < chartDataList.size()) {
                        performedClicking(current);
                        Highlight h = new Highlight((float) point.x, (float) chartDataList.get(current).getVal(), 0);
                        chart.highlightValue(h);
                        displayData();
                    }
                    return true;
                }
            });



        //    setOnGestureTouch();
           // setListenerOnChart();
    }



    private void setListenerOnChart(){
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                performedClicking((int)e.getX());
                displayData();
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }


    private void performedClicking(int x){
        int current = x;
        currentValueScreen = chartDataList.get(current).getVal();
        setTimeAgoOnTextView(chartDataList.get(current).getDate().substring(11,16));
        if(currentValueScreen > currentSubstance.getGoodFrom() && currentValueScreen <= currentSubstance.getGoodTo()){
            currentLayoutColor = currentSubstance.getGoodColor();
            airQuality = isGeorgian? "ძალიან კარგი" : "Good";
        }else if(currentValueScreen > currentSubstance.getFairFrom() && currentValueScreen <= currentSubstance.getFairTo()){
            currentLayoutColor = currentSubstance.getFairColor();
            airQuality = isGeorgian? "კარგი" : "Fair";
        }else if(currentValueScreen > currentSubstance.getModerateFrom() && currentValueScreen <= currentSubstance.getModerateTo()){
            currentLayoutColor = currentSubstance.getModerateColor();
            airQuality = isGeorgian? "საშუალო" : "Moderate";
        }else if(currentValueScreen > currentSubstance.getPoorFrom() && currentValueScreen <= currentSubstance.getPoorTo()){
            currentLayoutColor = currentSubstance.getPoorColor();
            airQuality =  isGeorgian? "ცუდი" : "Poor";
        }else if(currentValueScreen > currentSubstance.getVeryPoorFrom() && currentValueScreen <= currentSubstance.getVeryPoorTo()){
            currentLayoutColor = currentSubstance.getVeryPoorColor();
            airQuality = isGeorgian? "ძალიან ცუდი" : "Very Poor";
        }

    }

    private void setTimeAgoOnTextView(String time){
        timeAgo.setText(getString(R.string.time) + " " + time);
    }


    private void displayData() {
   //     myToolbar.setBackgroundColor(Color.parseColor(currentLayoutColor));

        Log.d("curColo",currentLayoutColor);

        showTextLayout.setBackgroundColor(Color.parseColor(currentLayoutColor));
        airQualityTextView.setText(airQuality);
        mainTextViewCube.setText(currentValueUnit);
        mainTextView.setText(new DecimalFormat("##.##").format(currentValueScreen));
        elementName.setText(textOfCenter);

        if(currentDistanceInMeter > 10000){
            stationDistance.setText(new DecimalFormat("##.#").format(currentDistanceInMeter/1000) + (isGeorgian?" კმ":" km"));
        }else {
            stationDistance.setText(((int) currentDistanceInMeter) + (isGeorgian? " მ" :" m"));
        }
        try {

            stationAddress.setText(new JSONObject(currentStation).getString(isGeorgian? "st_full_address_ge" :"st_full_address_en"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(chart != null)
            chart.setBackgroundColor(Color.parseColor(currentLayoutColor));
    }

    private void setCurrentSubstanceData(int position){
        currentSubstance = stationSubstance.get(position);
        currentLayoutColor = currentSubstance.getColor();
        textOfCenter = currentSubstance.getName();
        currentValueScreen = currentSubstance.getValue();

        currentValueUnit = isGeorgian? currentSubstance.getUnit_ge() : currentSubstance.getUnit_en();
        Log.d("cursub",currentSubstance.getArrayList().size() + " ");
        chartDataList = currentSubstance.getArrayList();
        Log.d("cursub",chartDataList.size() + " ");
        airQuality = currentSubstance.getAirQuality();
    }

    private Substance getMaxPopulationSubstance() {
        Substance max = stationSubstance.get(0);
        Log.d("maxSub", max.toString());
        currentLayoutColor = max.getColor();
        textOfCenter = max.getName();
        currentValueScreen = max.getValue();
        currentValueUnit = isGeorgian?max.getUnit_ge():max.getUnit_en();
        chartDataList = max.getArrayList();
        airQuality = max.getAirQuality();

        for(int i = 0; i < stationSubstance.size(); i++){
            if(substanceCompare(stationSubstance.get(i),max) > 0) {
                max = stationSubstance.get(i);
                textOfCenter = max.getName();
                currentLayoutColor = max.getColor();
                currentValueScreen = max.getValue();
                currentValueUnit = isGeorgian?max.getUnit_ge():max.getUnit_en();
                chartDataList = max.getArrayList();
                airQuality = max.getAirQuality();
            }
        }

        return max;
    }


    public int substanceCompare(Substance one,Substance second){
        double value = one.getValue();
        int firstWeight = 0;
        if(value >= one.getGoodFrom() && value <= one.getGoodTo()){
            firstWeight = 0;
        }else if(value > one.getFairFrom()  && value <= one.getFairTo()){
            firstWeight = 1;
        }else if(value > one.getModerateFrom() && value <= one.getModerateTo()){
            firstWeight = 2;
        }else if(value > one.getPoorFrom() && value <= one.getPoorTo()){
            firstWeight = 3;
        }else{
            firstWeight = 4;
        }

        //second
        double valueSec = second.getValue();
        int secondWeight = 0;
        if(valueSec >= second.getGoodFrom() && valueSec <= second.getGoodTo()){
            secondWeight = 0;
        }else if(valueSec > second.getFairFrom()  && valueSec <= second.getFairTo()){
            secondWeight = 1;
        }else if(valueSec > second.getModerateFrom() && valueSec <= second.getModerateTo()){
            secondWeight = 2;
        }else if(valueSec > second.getPoorFrom() && valueSec <= second.getPoorTo()){
            secondWeight = 3;
        }else{
            secondWeight = 4;
        }

        if(firstWeight > secondWeight){
            return 1;
        }else if(firstWeight == secondWeight){
            if(one.getPercentage() > second.getPercentage()){
                return 1;
            }else {
                return 0;
            }
        }else {
            return 0;
        }
    }

    private void readStations(String data) throws JSONException {

        JSONArray jsonArray = new JSONArray(data);

        for (int i = 0; i < jsonArray.length(); i++) {
            String st = jsonArray.get(i).toString();
            stationList.add(st);
        }

        currentStation = this.getIntent().getStringExtra("currentStation");


        if(currentStation == null || currentStation.equals("")){
            if(lastKnownLocation != null){
                currentStation = getNearestStation();
            }else {
                currentStation = stationList.get(0);
            }
        }else{
            Log.d("currentS",currentStation);
            if(lastKnownLocation != null){
                calculateDistanceCurrentLoc();
            }
        }


        JSONObject jsonObject = new JSONObject(currentStation);
        JSONArray eqip = jsonObject.getJSONArray("stationequipment_set");
        readStationEquipment(eqip);

    }

    private void calculateDistanceCurrentLoc() throws JSONException {
        JSONObject object = new JSONObject(currentStation);
        Location loc1 = new Location("");
        loc1.setLatitude(object.getDouble("lat"));
        loc1.setLongitude(object.getDouble("long"));
        currentDistanceInMeter = loc1.distanceTo(lastKnownLocation);
    }

    private String getNearestStation() throws JSONException {
        String station = stationList.get(0);

        JSONObject object = new JSONObject(station);
        Location loc1 = new Location("");
        loc1.setLatitude(object.getDouble("lat"));
        loc1.setLongitude(object.getDouble("long"));

        float distanceInMeters = loc1.distanceTo(lastKnownLocation);


        for(int i = 1; i < stationList.size(); i++){
            object = new JSONObject(stationList.get(i));
            loc1 = new Location("");
            loc1.setLatitude(object.getDouble("lat"));
            loc1.setLongitude(object.getDouble("long"));

            if(distanceInMeters > loc1.distanceTo(lastKnownLocation)){
                distanceInMeters = loc1.distanceTo(lastKnownLocation);
                station = stationList.get(i);
            }
        }

        currentDistanceInMeter = distanceInMeters;
        return station;
    }


    private void readStationEquipment(JSONArray jsonArray) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            String st = jsonArray.get(i).toString();
            stationEquipmentList.add(st);
            stationSubstance.add(getStationSubstance(st));
        }
    }




    private Substance getStationSubstance(String data) throws JSONException {

        JSONObject json = new JSONObject(data);
        JSONObject substance = json.getJSONObject("substance");
        JSONArray lastDataHour = json.getJSONArray("data1hour_set");

        String name = substance.getString("name");
        String unit_ge = substance.getString("unit_ge");
        String unit_en = substance.getString("unit_en");

        Substance substance1 = new Substance();
        substance1.setName(name);
        substance1.setUnit_ge(unit_ge);
        substance1.setUnit_en(unit_en);

        double percentOfValue = 0;
        ArrayList<PointXY> listData = new ArrayList<>();


        JSONArray airIndexArr = substance.getJSONArray("airqualityindexlevel_set");
        JSONObject arrIndexObj = airIndexArr.getJSONObject(0);

        double goodFrom = arrIndexObj.getDouble("good_from");
        double goodTo = arrIndexObj.getDouble("good_to");

        double fair_from = arrIndexObj.getDouble("fair_from");
        double fair_to = arrIndexObj.getDouble("fair_to");

        double moderate_from = arrIndexObj.getDouble("moderate_from");
        double moderate_to = arrIndexObj.getDouble("moderate_to");

        double poor_from = arrIndexObj.getDouble("poor_from");
        double poor_to = arrIndexObj.getDouble("poor_to");

        double very_poor_from = arrIndexObj.getDouble("very_poor_from");
        double very_poor_to = arrIndexObj.getDouble("very_poor_to");

        substance1.setGoodFrom(goodFrom);
        substance1.setGoodTo(goodTo);
        substance1.setFairFrom(fair_from);
        substance1.setFairTo(fair_to);
        substance1.setModerateFrom(moderate_from);
        substance1.setModerateTo(moderate_to);
        substance1.setPoorFrom(poor_from);
        substance1.setPoorTo(poor_to);
        substance1.setVeryPoorTo(very_poor_to);
        substance1.setVeryPoorFrom(very_poor_from);



        if(lastDataHour.length() > 0) {
            Log.d("gaagr","yes");
            for(int i = 0; i < lastDataHour.length(); i++){

                PointXY pointXY = new PointXY();
                pointXY.setVal(lastDataHour.getJSONObject(i).getDouble("value"));
                pointXY.setDate(lastDataHour.getJSONObject(i).getString("date_time"));
                listData.add(pointXY);
            }

            Log.d("gaagr","yes");
            Collections.sort(listData, new Comparator<PointXY>() {
                DateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
                @Override
                public int compare(PointXY o1, PointXY o2) {
                    try {
                        return f.parse(o1.getDate()).compareTo(f.parse(o2.getDate()));
                    }catch (ParseException e){

                    }
                    return 0;
                }
            });

            double lastData = listData.get(listData.size() - 1).getVal();
            currentValueScreen = lastData;
            Log.d("currV",currentValueScreen + "");

            substance1.setValue(lastData);


            if(lastData > goodFrom && lastData < goodTo){
                airQuality = isGeorgian? "ძალიან კარგი" : "Good";
                currentLayoutColor = arrIndexObj.getString("good_color");
                percentOfValue = lastData * 100/goodTo;
            }else if(lastData > fair_from && lastData < fair_to){
                airQuality = isGeorgian? "კარგი" : "Fair";
                currentLayoutColor = arrIndexObj.getString("fair_color");
                percentOfValue = lastData * 100/fair_to;
            }else if(lastData > moderate_from && lastData < moderate_to){
                airQuality = isGeorgian? "საშუალო" : "Moderate";
                currentLayoutColor = arrIndexObj.getString("moderate_color");
                percentOfValue = lastData * 100/ moderate_to;
            }else if(lastData > poor_from &&  lastData < poor_to){
                airQuality = isGeorgian? "ცუდი" : "Poor";
                currentLayoutColor = arrIndexObj.getString("poor_color");
                percentOfValue = lastData * 100/poor_to;
            }else if(lastData > very_poor_from && lastData < very_poor_to){
                airQuality = isGeorgian? "ძალიან ცუდი" : "Very Poor";
                currentLayoutColor = arrIndexObj.getString("very_poor_color");
                percentOfValue = lastData * 100/very_poor_to;
            }
        }
        substance1.setAirQuality(airQuality);
        substance1.setGoodColor(arrIndexObj.getString("good_color"));
        substance1.setFairColor(arrIndexObj.getString("fair_color"));
        substance1.setModerateColor(arrIndexObj.getString("moderate_color"));
        substance1.setPoorColor(arrIndexObj.getString("poor_color"));
        substance1.setVeryPoorColor(arrIndexObj.getString("very_poor_color"));
        substance1.setPercentage(percentOfValue);
        substance1.setColor(currentLayoutColor);
        substance1.setArrayList(listData);
        substance1.setData(data);
        return substance1;
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
                intent.putExtra("data",data);
                intent.putExtra("color",currentLayoutColor);
                intent.putExtra("currentStation",currentStation);
                if(lastKnownLocation != null) {
                    intent.putExtra("lat", lastKnownLocation.getLatitude());
                    intent.putExtra("long", lastKnownLocation.getLongitude());
                }
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
                drawable.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
            }
        }
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
            RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(500);
            rotate.setInterpolator(new LinearInterpolator());
            ImageView image= (ImageView) findViewById(R.id.showStation);
            image.startAnimation(rotate);
            rotate.setFillAfter(true);

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

            RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(500);
            rotate.setInterpolator(new LinearInterpolator());
            ImageView image= (ImageView) findViewById(R.id.showStation);
            image.startAnimation(rotate);
            rotate.setFillAfter(true);

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
    public void onLocationChanged(Location location) {
        Log.d("location","Current Location: " + location.getLatitude() + ", " + location.getLongitude());
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {


    }


    private void setNavigationViewListner() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId() == R.id.language) {
                    String displayLanguage = Locale.getDefault().getDisplayLanguage();

                    SharedPreferences prefs = getSharedPreferences("pref", MODE_PRIVATE);
                    String restoredText = prefs.getString("lang", null);
                    if (restoredText == null) {
                        restoredText = "en";
                    }

                    Log.d("ena",restoredText);
                    SharedPreferences.Editor editor = getSharedPreferences("pref", MODE_PRIVATE).edit();
                    if (restoredText.equals("en")){
                        changeLanguage("ka");
                        editor.putString("lang", "ka");
                        editor.apply();
                    }else {
                        changeLanguage("en");
                        editor.putString("lang", "en");
                        editor.apply();
                    }
                    reload();

                }else if(item.getItemId() == R.id.item_share) {
                    drawerLayout.closeDrawer(Gravity.LEFT, false);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            View screenView = findViewById(R.id.showTextLayout).getRootView();
                            screenView.setDrawingCacheEnabled(true);
                            Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
                            screenView.setDrawingCacheEnabled(false);

                            try {
                                File cachePath = new File(getApplication().getCacheDir(), "images");
                                cachePath.mkdirs(); // don't forget to make the directory
                                FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                stream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            File imagePath = new File(getApplication().getCacheDir(), "images");
                            File newFile = new File(imagePath, "image.png");
                            Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), "ge.gov.air.airgov", newFile);

                            if (contentUri != null) {
                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                                shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
                                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                                startActivity(Intent.createChooser(shareIntent, "Choose an app"));
                            }
                        }
                    }, 500);


                }else if(item.getItemId() == R.id.recomendation){
                    Intent intent = new Intent(App.this, WebViewAct.class);
                    String url = isGeorgian ? "http://air.gov.ge/pages/18/12" : "http://air.gov.ge/en/pages/18/12";
                    intent.putExtra("url", url+"?no_header_footer=true");
                    startActivity(intent);
                }else if(item.getItemId() == R.id.airQualityInx){
                    Intent intent = new Intent(App.this, WebViewAct.class);
                    String url = isGeorgian ? "http://air.gov.ge/pages/11/11" : "http://air.gov.ge/en/pages/11/11";
                    intent.putExtra("url", url +"?no_header_footer=true");
                    startActivity(intent);
                }else  if(item.getItemId() == R.id.pollutant){
                    Intent intent = new Intent(App.this, WebViewAct.class);
                    String url = isGeorgian ? "http://air.gov.ge/pages/13/13" : "http://air.gov.ge/en/pages/13/13";
                    intent.putExtra("url", url + "?no_header_footer=true");
                    startActivity(intent);
                }
                return true;
            }
        });
    }


    public void changeLanguage(String lang){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    public void reload() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("rsumed","res");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SharedPreferences prefs2 = getSharedPreferences("pref", MODE_PRIVATE);
        Log.d("restarted","res");
        boolean previousWasWeb = prefs2.getBoolean("wasWebView", false);
        if (!previousWasWeb){
            Intent intent = new Intent(App.this, LoadActivity.class);
            startActivity(intent);
        }else {
            SharedPreferences.Editor editor = getSharedPreferences("pref", MODE_PRIVATE).edit();
            editor.putBoolean("wasWebView", false);
            editor.apply();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(App.this, LoadActivity.class);
        startActivity(intent);
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }
}
