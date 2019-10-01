package ge.gov.air.airgov;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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

public class MapActivity  extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

    private static final LatLng PERTH = new LatLng(-31.952854, 115.857342);
    private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);
    private static final LatLng BRISBANE = new LatLng(-27.47093, 153.0235);


    private GoogleMap mMap;
    private String data;

    private boolean isGeorgian;

    private Location currentLocation;
    private ArrayList<String> stationsList = new ArrayList<>();

    ArrayList<StationModel> stationListModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);
        data = this.getIntent().getStringExtra("data");

        SharedPreferences prefs = getSharedPreferences("pref", MODE_PRIVATE);
        String restoredText = prefs.getString("lang", null);
        if(restoredText != null) {
            if (restoredText.equals("ka")) {
                isGeorgian = true;
            } else {
                isGeorgian = false;
            }
        }

        double lat = this.getIntent().getDoubleExtra("lat",0);
        double longt = this.getIntent().getDoubleExtra("long",0);

        currentLocation = new Location("");
        currentLocation.setLatitude((lat));
        currentLocation.setLongitude((longt));

        try {
            stationListModel = getStationList(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        for(StationModel stationModel : stationListModel) {
            LatLng sydney = new LatLng(stationModel.getLoc().getLatitude(), stationModel.getLoc().getLongitude());
            int width = 100;
            int height = 100;
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);

            MarkerOptions title = new MarkerOptions().position(sydney)
                    .title(stationModel.getText()).icon(BitmapDescriptorFactory.fromBitmap(GetBitmapClippedCircle(bitmap,stationModel)));

            Marker marker = googleMap.addMarker(title);
            marker.setTag(stationModel);
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),13));
        MarkerInfoWindowAdapter markerInfoWindowAdapter = new MarkerInfoWindowAdapter(getApplicationContext(),isGeorgian);
        googleMap.setInfoWindowAdapter(markerInfoWindowAdapter);
        googleMap.setOnInfoWindowClickListener(this);
    }

    private Bitmap GetBitmapClippedCircle(Bitmap bitmap,StationModel stationModel) {
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        final Bitmap outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        final Path path = new Path();
        path.addCircle(
                (float)(width / 2)
                , (float)(height / 2)
                , (float) Math.min(width, (height / 2))
                , Path.Direction.CCW);

        final Canvas canvas = new Canvas(outputBitmap);
        canvas.clipPath(path);
        canvas.drawColor(Color.parseColor(stationModel.getColor()));
        canvas.drawBitmap(bitmap, 0, 0, null);
        return outputBitmap;
    }

    private String id;

    @Override
    public boolean onMarkerClick(Marker marker) {

        return true;
    }


    private ArrayList<StationModel> getStationList(String data) throws JSONException {
        ArrayList<StationModel> list = new ArrayList<>();

        JSONArray jsonArray = new JSONArray(data);

        for (int i = 0; i < jsonArray.length(); i++) {
            String st = jsonArray.get(i).toString();
            stationsList.add(st);
            Substance sub = readStationsForColor(st);
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

            stationModel.setColor(sub.getColor());
            stationModel.setItem(sub.getName());
            stationModel.setQuality(sub.getAirQuality());
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

            String airQuality = isGeorgian? "ძალიან კარგი" : "Good";

            if(lastData > goodFrom && lastData <= goodTo){
                subsColor = arrIndexObj.getString("good_color");
                airQuality = isGeorgian? "ძალიან კარგი" : "Good";
            }else if(lastData > fair_from && lastData <= fair_to){
                subsColor = arrIndexObj.getString("fair_color");
                airQuality = isGeorgian? "კარგი" : "Fair";
            }else if(lastData > moderate_from && lastData <= moderate_to){
                subsColor = arrIndexObj.getString("moderate_color");
                airQuality = isGeorgian? "საშუალო" : "Moderate";
            }else if(lastData > poor_from &&  lastData <= poor_to){
                subsColor = arrIndexObj.getString("poor_color");
                airQuality = isGeorgian? "ცუდი" : "Poor";
            }else if(lastData > very_poor_from && lastData <= very_poor_to){
                subsColor = arrIndexObj.getString("very_poor_color");
                airQuality = isGeorgian? "ძალიან ცუდი" : "Very Poor";
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
            substance1.setAirQuality(airQuality);
        }

        substance1.setColor(subsColor);


        return substance1;
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

    @Override
    public void onInfoWindowClick(Marker marker) {

        StationModel st = (StationModel)marker.getTag();

        Intent intent = new Intent(MapActivity.this, App.class);
        try {
            intent.putExtra("currentStation",getStationById(st.getId()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startActivity(intent);
    }
}