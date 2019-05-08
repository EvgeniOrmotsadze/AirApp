package ge.gov.air.airgov;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class MainAdapter extends BaseAdapter {

        Activity activity;
        ArrayList<Substance> list = null;
        LayoutInflater layoutInflater = null;
        boolean isGeorgian;

        public MainAdapter(Activity activity, ArrayList<Substance> customListDataModelArray,boolean isGeorgian){
            this.activity=activity;
            this.list = customListDataModelArray;
            layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.isGeorgian = isGeorgian;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Substance getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }


        private static class ViewHolder{
            TextView key;
            TextView value;
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
                vi = layoutInflater.inflate(R.layout.main_single_row,null);
                viewHolder.key = vi.findViewById(R.id.main_single_row_key);
                viewHolder.value = vi.findViewById(R.id.main_single_row_value);
                holders.add(viewHolder);
                vi.setTag(viewHolder);
            }else {
                viewHolder= (ViewHolder) vi.getTag();
                holders.add(viewHolder);
            }

          //  Log.d("valuesO",list.get(pos))

           if(list.get(pos).getArrayList().size() > 0 ) {
               NumberFormat formatter = new DecimalFormat("##.##");
               Log.d("keys",formatter.format(list.get(pos).getValue())+"");
               viewHolder.key.setText(formatter.format(list.get(pos).getValue()) + "");

               viewHolder.key.setBackgroundColor(Color.parseColor(list.get(pos).getColor()));

           }else {
               viewHolder.key.setText(R.string.no_data);
               viewHolder.key.setBackgroundColor(activity.getResources().getColor(R.color.showIcon));
           }
           String txt = list.get(pos).getName() +  " " +(isGeorgian? list.get(pos).getUnit_ge() : list.get(pos).getUnit_en());
           viewHolder.value.setText(txt);
           return vi;
        }

}
