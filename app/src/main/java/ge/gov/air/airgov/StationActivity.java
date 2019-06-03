package ge.gov.air.airgov;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class StationActivity extends AppCompatActivity {

    private  DrawerLayout drawerLayout;

    private ListView mListView;
    private String data;

    private Location currentLocation;
    private ArrayList<String> stationsList = new ArrayList<>();

    private boolean isGeorgian;

    private String id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.station_list_activity);

        SharedPreferences prefs = getSharedPreferences("pref", MODE_PRIVATE);
        String restoredText = prefs.getString("lang", null);
        if(restoredText != null) {
            if (restoredText.equals("ka")) {
                isGeorgian = true;
            } else {
                isGeorgian = false;
            }
        }

        data = this.getIntent().getStringExtra("data");
        String color = this.getIntent().getStringExtra("color");
        String currentStation = this.getIntent().getStringExtra("currentStation");
        id = getCurrentStationId(currentStation);
        double lat = this.getIntent().getDoubleExtra("lat",0);
        double longt = this.getIntent().getDoubleExtra("long",0);


        currentLocation = new Location("");
        currentLocation.setLatitude((lat));
        currentLocation.setLongitude((longt));

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setTitle(null);


        myToolbar.setBackgroundColor(getResources().getColor(R.color.white));

        mListView = findViewById( R.id.my_list_view );

        ArrayList<StationModel> names = null;
        try {
            names = getStationList(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//
        if(currentLocation != null) {
            Collections.sort(names, new Comparator<StationModel>() {
                @Override
                public int compare(StationModel stationModel, StationModel t1) {
                    float v = currentLocation.distanceTo(stationModel.getLoc());
                    float v2 = currentLocation.distanceTo(t1.getLoc());
                    return (int) (v - v2);
                }
            });
        }

        final ListViewAdapter adapter = new ListViewAdapter(StationActivity.this,names,isGeorgian,id);
        mListView.setAdapter(adapter);
      //  mListView.setItemChecked(0,true);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String id = adapter.getItem(position).getId();
                try {
                    String station = getStationById(id);
                    Intent intent = new Intent(StationActivity.this, App.class);
                    intent.putExtra("currentStation",station);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    private String getStationById(String id) throws JSONException {
        for(String st : stationsList){
            String id1 = new JSONObject(st).getString("id");
            if(id1.equals(id)){
                return st;
            }
        }
        return stationsList.get(0);// this almost not execute
    }

    private String getCurrentStationId(String st){
        try {
            String id1 = new JSONObject(st).getString("id");
            return id1;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "-1";
    }

    private ArrayList<StationModel> getStationList(String data) throws JSONException {
        ArrayList<StationModel> list = new ArrayList<>();


        JSONArray jsonArray = new JSONArray(data);

        for (int i = 0; i < jsonArray.length(); i++) {
            String st = jsonArray.get(i).toString();
            stationsList.add(st);
            Substance col = readStationsForColor(st);
            JSONObject jsonObject = new JSONObject(st);
            String settlement_en = jsonObject.getString("settlement_en");
            String settlement_ge = jsonObject.getString("settlement");
            String address_en = jsonObject.getString("address_en");
            String address_ge = jsonObject.getString("address");
            String id = jsonObject.getString("id");

            double lat = jsonObject.getDouble("lat");
            double longt = jsonObject.getDouble("long");
            Location location = new Location("");
            location.setLatitude(lat);
            location.setLongitude(longt);

            StationModel stationModel = new StationModel(id);
            stationModel.setLoc(location);
            stationModel.setText((isGeorgian?settlement_ge:settlement_en)+ ", " + (isGeorgian?address_ge:address_en));
            if(currentLocation.getLatitude() < 1 && currentLocation.getLatitude() < 1){
                stationModel.setLocation("?");
            }else {
                float v = location.distanceTo(currentLocation);
                stationModel.setLocation(new DecimalFormat("##.#").format(v/1000) + (isGeorgian?"კმ":"km"));
            }
            stationModel.setColor(col.getColor());
            stationModel.setQuality(col.getAirQuality());
            list.add(stationModel);
        }
        return list;
    }

    private Substance readStationsForColor(String data) throws JSONException {
        JSONObject jsonObject = new JSONObject(data);
        JSONArray eqip = jsonObject.getJSONArray("stationequipment_set");
        return readStationEquipment(eqip);
    }


    private Substance readStationEquipment(JSONArray jsonArray) throws JSONException {
        ArrayList<Substance> arrayList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            String st = jsonArray.get(i).toString();
            arrayList.add(getStationSubstance(st));
        }


        Substance maxSubstance = arrayList.get(0);
        for(int i = 0; i < arrayList.size(); i++){
            if(substanceCompare(arrayList.get(i), maxSubstance) > 0 ) {
                maxSubstance = arrayList.get(i);
            }
        }
        return maxSubstance;
    }

    public int substanceCompare(Substance one,Substance second) {
        double value = one.getValue();
        int firstWeight = 0;
        if (value >= one.getGoodFrom() && value <= one.getGoodTo()) {
            firstWeight = 0;
        } else if (value > one.getFairFrom() && value <= one.getFairTo()) {
            firstWeight = 1;
        } else if (value > one.getModerateFrom() && value <= one.getModerateTo()) {
            firstWeight = 2;
        } else if (value > one.getPoorFrom() && value <= one.getPoorTo()) {
            firstWeight = 3;
        } else {
            firstWeight = 4;
        }

        //second
        double valueSec = second.getValue();
        int secondWeight = 0;
        if (valueSec >= second.getGoodFrom() && valueSec <= second.getGoodTo()) {
            secondWeight = 0;
        } else if (valueSec > second.getFairFrom() && valueSec <= second.getFairTo()) {
            secondWeight = 1;
        } else if (valueSec > second.getModerateFrom() && valueSec <= second.getModerateTo()) {
            secondWeight = 2;
        } else if (valueSec > second.getPoorFrom() && valueSec <= second.getPoorTo()) {
            secondWeight = 3;
        } else {
            secondWeight = 4;
        }


        if (firstWeight > secondWeight) {
            return 1;
        } else if (firstWeight == secondWeight) {
            if (one.getPercentage() > second.getPercentage()) {
                return 1;
            } else {
                return 0;
            }
        } else {
            return 0;
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

        String subsColor = "#29a884"; //default color

        if(lastDataHour.length() > 0) {

            ArrayList<PointXY> listData = new ArrayList<>();

            for(int i = 0; i < lastDataHour.length(); i++){
                PointXY pointXY = new PointXY();
                pointXY.setVal(lastDataHour.getJSONObject(i).getDouble("value"));
                pointXY.setDate(lastDataHour.getJSONObject(i).getString("date_time"));
                listData.add(pointXY);
            }

            Collections.sort(listData, new Comparator<PointXY>() {
                DateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
                @Override
                public int compare(PointXY o1, PointXY o2) {
                    try {
                        return f.parse(o1.getDate()).compareTo(f.parse(o2.getDate()));
                    } catch (ParseException e) {
                        throw new IllegalArgumentException(e);
                    }
                }
            });




            double lastData = listData.get(listData.size() - 1).getVal();
            substance1.setValue(lastData);
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


            String airQualityStr = "";
            if(lastData > goodFrom && lastData <= goodTo){
                subsColor = arrIndexObj.getString("good_color");
                airQualityStr = isGeorgian? "ძალიან კარგი" : "Good";
                //  percentOfValue = lastData * 100/goodTo;
            }else if(lastData > fair_from && lastData <= fair_to){
                subsColor = arrIndexObj.getString("fair_color");
                airQualityStr = isGeorgian? "კარგი" : "Fair";
                // percentOfValue = lastData * 100/fair_to;
            }else if(lastData > moderate_from && lastData <= moderate_to){
                subsColor = arrIndexObj.getString("moderate_color");
                airQualityStr = isGeorgian? "საშუალო" : "Moderate";
                //percentOfValue = lastData * 100/ moderate_to;
            }else if(lastData > poor_from &&  lastData <= poor_to){
                subsColor = arrIndexObj.getString("poor_color");
                airQualityStr = isGeorgian? "ცუდი" : "Poor";
                //percentOfValue = lastData * 100/poor_to;
            }else if(lastData > very_poor_from && lastData <= very_poor_to){
                subsColor = arrIndexObj.getString("very_poor_color");
                airQualityStr = isGeorgian? "ძალიან ცუდი" : "Very Poor";
                //percentOfValue = lastData * 100/very_poor_to;
            }
            substance1.setGoodFrom(goodFrom);
            substance1.setGoodTo(goodTo);
            substance1.setFairFrom(fair_from);
            substance1.setFairTo(fair_to);
            substance1.setModerateFrom(moderate_from);
            substance1.setModerateTo(moderate_to);
            substance1.setPoorFrom(poor_from);
            substance1.setPoorTo(poor_to);
            substance1.setVeryPoorFrom(very_poor_from);
            substance1.setVeryPoorTo(very_poor_to);
            substance1.setAirQuality(airQualityStr);
        }
        substance1.setColor(subsColor);


        return substance1;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        Drawable drawable = item.getIcon();

        switch (itemId) {
            // Android home
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.radar:
                Intent intent = new Intent(StationActivity.this, App.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
                return true;
            case R.id.station_map:
                intent = new Intent(StationActivity.this, MapActivity.class);
                intent.putExtra("data",data);
                intent.putExtra("lat", currentLocation.getLatitude());
                intent.putExtra("long", currentLocation.getLongitude());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
                return true;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action_bar_stations, menu);
        for(int i = 0; i < menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if(drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
            }
        }

        return true;
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(StationActivity.this, App.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
    }

    public void reload() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

}
