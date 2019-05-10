package ge.gov.air.airgov;

import android.text.Html;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Substance {

    private String name;
    private String unit_ge;
    private String unit_en;
    private double percentage;
    private String airQuality;
    private double value;
    private String color;
    private ArrayList<PointXY> arrayList = new ArrayList<>();
    private String data;
    private double goodTo;
    private double goodFrom;
    private double fairTo;
    private double fairFrom;
    private double moderateTo;
    private double moderateFrom;
    private double poorTo;
    private double poorFrom;
    private double veryPoorTo;
    private double veryPoorFrom;

    private String goodColor;
    private String fairColor;
    private String moderateColor;
    private String poorColor;
    private String veryPoorColor;


    public String getGoodColor() {
        return goodColor;
    }

    public void setGoodColor(String goodColor) {
        this.goodColor = goodColor;
    }

    public String getFairColor() {
        return fairColor;
    }

    public void setFairColor(String fairColor) {
        this.fairColor = fairColor;
    }

    public String getModerateColor() {
        return moderateColor;
    }

    public void setModerateColor(String moderateColor) {
        this.moderateColor = moderateColor;
    }

    public String getPoorColor() {
        return poorColor;
    }

    public void setPoorColor(String poorColor) {
        this.poorColor = poorColor;
    }

    public String getVeryPoorColor() {
        return veryPoorColor;
    }

    public void setVeryPoorColor(String veryPoorColor) {
        this.veryPoorColor = veryPoorColor;
    }

    public double getGoodTo() {
        return goodTo;
    }

    public void setGoodTo(double goodTo) {
        this.goodTo = goodTo;
    }

    public double getGoodFrom() {
        return goodFrom;
    }

    public void setGoodFrom(double goodFrom) {
        this.goodFrom = goodFrom;
    }

    public double getFairTo() {
        return fairTo;
    }

    public void setFairTo(double fairTo) {
        this.fairTo = fairTo;
    }

    public double getFairFrom() {
        return fairFrom;
    }

    public void setFairFrom(double fairFrom) {
        this.fairFrom = fairFrom;
    }

    public double getModerateTo() {
        return moderateTo;
    }

    public void setModerateTo(double moderateTo) {
        this.moderateTo = moderateTo;
    }

    public double getModerateFrom() {
        return moderateFrom;
    }

    public void setModerateFrom(double moderateFrom) {
        this.moderateFrom = moderateFrom;
    }

    public double getPoorTo() {
        return poorTo;
    }

    public void setPoorTo(double poorTo) {
        this.poorTo = poorTo;
    }

    public double getPoorFrom() {
        return poorFrom;
    }

    public void setPoorFrom(double poorFrom) {
        this.poorFrom = poorFrom;
    }

    public double getVeryPoorTo() {
        return veryPoorTo;
    }

    public void setVeryPoorTo(double veryPoorTo) {
        this.veryPoorTo = veryPoorTo;
    }

    public double getVeryPoorFrom() {
        return veryPoorFrom;
    }

    public void setVeryPoorFrom(double veryPoorFrom) {
        this.veryPoorFrom = veryPoorFrom;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getAirQuality() {
        return airQuality;
    }

    public void setAirQuality(String airQuality) {
        this.airQuality = airQuality;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getUnit_ge() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(this.unit_ge, Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            return Html.fromHtml(this.unit_ge).toString();
        }
    }

    public ArrayList<PointXY> getArrayList() {

        Collections.sort(this.arrayList, new Comparator<PointXY>() {
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

        return arrayList;
    }

    public void setArrayList(ArrayList<PointXY> arrayList) {
        this.arrayList = arrayList;
    }


    public void setUnit_ge(String unit_ge) {
        this.unit_ge = unit_ge;
    }

    public String getUnit_en() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(this.unit_en, Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            return Html.fromHtml(this.unit_en).toString();
        }    }

    public void setUnit_en(String unit_en) {
        this.unit_en = unit_en;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }


    @Override
    public String toString() {
        return "Substance{" +
                "name='" + name + '\'' +
                ", unit_ge='" + unit_ge + '\'' +
                ", unit_en='" + unit_en + '\'' +
                ", percentage=" + percentage +
                ", airQuality='" + airQuality + '\'' +
                ", value=" + value +
                ", color='" + color + '\'' +
                ", arrayList=" + arrayList +
                ", data='" + data + '\'' +
                ", goodTo=" + goodTo +
                ", goodFrom=" + goodFrom +
                ", fairTo=" + fairTo +
                ", fairFrom=" + fairFrom +
                ", moderateTo=" + moderateTo +
                ", moderateFrom=" + moderateFrom +
                ", poorTo=" + poorTo +
                ", poorFrom=" + poorFrom +
                ", veryPoorTo=" + veryPoorTo +
                ", veryPoorFrom=" + veryPoorFrom +
                ", goodColor='" + goodColor + '\'' +
                ", fairColor='" + fairColor + '\'' +
                ", moderateColor='" + moderateColor + '\'' +
                ", poorColor='" + poorColor + '\'' +
                ", veryPoorColor='" + veryPoorColor + '\'' +
                '}';
    }
}
