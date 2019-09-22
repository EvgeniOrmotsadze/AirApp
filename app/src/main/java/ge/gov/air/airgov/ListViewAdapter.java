package ge.gov.air.airgov;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    Activity activity;
    ArrayList<StationModel> list = new ArrayList<>();
    LayoutInflater layoutInflater = null;
    boolean isGeorgian;
    Context context;
    String id;

    public ListViewAdapter(Activity activity, ArrayList<StationModel> customListDataModelArray,boolean isGeorgian,String id){
        this.activity=activity;
        context = activity.getApplicationContext();
        this.list = customListDataModelArray;
        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.isGeorgian = isGeorgian;
        this.id = id;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public StationModel getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    private static class ViewHolder{
        TextView stations;
        TextView km;
        TextView airQualityList;
        View listColorView;
        RelativeLayout singleRow;
        TextView selectItem;
    }

    ViewHolder viewHolder = null;

    ArrayList<ViewHolder> holders = new ArrayList<>();

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi=convertView;
        final int pos = position;
        if(vi == null){

            // create  viewholder object for list_rowcell View.
            viewHolder = new ViewHolder();
            // inflate list_rowcell for each row
            vi = layoutInflater.inflate(R.layout.single_row,null);
            viewHolder.stations = (TextView) vi.findViewById(R.id.first);
            viewHolder.km = (TextView) vi.findViewById(R.id.distanceList);
            viewHolder.airQualityList = vi.findViewById(R.id.airQualityList);
            viewHolder.singleRow = vi.findViewById(R.id.single_row);
            viewHolder.listColorView = vi.findViewById(R.id.listColorView);
            viewHolder.selectItem = vi.findViewById(R.id.selectItem);
            holders.add(viewHolder);
            vi.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) vi.getTag();
            holders.add(viewHolder);
        }


        viewHolder.stations.setText(list.get(pos).getText());
        viewHolder.km.setText(list.get(pos).getLocation());
        viewHolder.airQualityList.setText(list.get(pos).getQuality());


        if(list.get(pos).getId().equals(id)) {

            Drawable vDrawable = context.getResources().getDrawable(R.drawable.station_text_shape);
            vDrawable.setColorFilter(new
                    PorterDuffColorFilter(context.getResources().getColor(R.color.showStation), PorterDuff.Mode.MULTIPLY));
            vi.setBackground(vDrawable);
            viewHolder.selectItem.setVisibility(View.VISIBLE);
        }else {
            Drawable vDrawable = context.getResources().getDrawable(R.drawable.station_text_shape);
            vDrawable.setColorFilter(new
                    PorterDuffColorFilter(context.getResources().getColor(R.color.showStationLight), PorterDuff.Mode.MULTIPLY));
            vi.setBackground(vDrawable);
            viewHolder.selectItem.setVisibility(View.GONE);
        }



        Drawable mDrawable = context.getResources().getDrawable(R.drawable.station_text_shape);
        mDrawable.setColorFilter(new
                PorterDuffColorFilter(Color.parseColor(list.get(pos).getColor()), PorterDuff.Mode.MULTIPLY));

        viewHolder.listColorView.setBackground(mDrawable);
        viewHolder.airQualityList.setTextColor(Color.parseColor(list.get(pos).getColor()));

        return vi;
    }

}
