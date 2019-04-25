package ge.gov.air.airgov;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    Activity activity;
    ArrayList list = new ArrayList<>();
    LayoutInflater layoutInflater = null;

    public ListViewAdapter(Activity activity, ArrayList<String> customListDataModelArray){
        this.activity=activity;
        this.list = customListDataModelArray;
        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    private static class ViewHolder{
        TextView stations;
        LinearLayout layout;
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
            viewHolder.layout = vi.findViewById(R.id.hidden);
            holders.add(viewHolder);
            vi.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) vi.getTag();
            holders.add(viewHolder);
        }

        viewHolder.stations.setText(list.get(pos) + "");

//        if((list.get(pos) + "").contains("Tbilisi - Vashlijvari")){
//            viewHolder.stations.setBackgroundColor(Color.RED);
//        }


        return vi;
    }

}
