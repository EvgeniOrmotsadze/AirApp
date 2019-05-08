package ge.gov.air.airgov;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
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
        RelativeLayout singleRow;
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
            viewHolder.km = (TextView) vi.findViewById(R.id.km);
            viewHolder.singleRow = vi.findViewById(R.id.single_row);
            holders.add(viewHolder);
            vi.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) vi.getTag();
            holders.add(viewHolder);
        }


        viewHolder.singleRow.setBackgroundColor(Color.parseColor(list.get(pos).getColor()));
        viewHolder.stations.setText(list.get(pos).getText());
        viewHolder.km.setText(list.get(pos).getLocation());
        if(list.get(pos).getId().equals(id))
            vi.setBackgroundColor(Color.parseColor(list.get(pos).getColor()));
        return vi;
    }

}
