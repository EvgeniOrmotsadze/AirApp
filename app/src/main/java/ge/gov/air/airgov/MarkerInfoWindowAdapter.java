package ge.gov.air.airgov;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private Context context;

    private boolean isGeorgian;

    public MarkerInfoWindowAdapter(Context context,boolean isGeorgian) {
        this.context = context.getApplicationContext();
        this.isGeorgian = isGeorgian;
    }

    @Override
    public View getInfoWindow(Marker arg0) {
        return null;
    }


    @Override
    public View getInfoContents(Marker arg0) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v =  inflater.inflate(R.layout.map_marker_info_window, null);

        StationModel tag = (StationModel) arg0.getTag();
        TextView header = (TextView) v.findViewById(R.id.header_t);
        TextView poor = (TextView) v.findViewById(R.id.poor);
        header.setText(tag.getText());
        poor.setText(isGeorgian? tag.getQuality() + "(" +tag.getItem() + ")" : tag.getQuality() + "(due to " +tag.getItem() + " )");
        return v;
    }


}
