package ge.gov.air.airgov;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

public class StationActivity extends AppCompatActivity {

    private  DrawerLayout drawerLayout;

    private ListView mListView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.station_list_activity);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setTitle(null);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setHomeAsUpIndicator(R.mipmap.white_menu_icon);
        actionbar.setDisplayHomeAsUpEnabled(true);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mListView = (ListView) findViewById( R.id.my_list_view );

        ArrayList<String> names = new ArrayList<String>();
        names.add("Tbilisi - Tsereteli - 0.4 km");
        names.add("Tbilisi - Vashlijvari - 0.5 km");
        names.add("Tbilisi - Vashlijvari - 0.5 km");
        names.add("Tbilisi - Kazbegi - 0.6 km");
        names.add("Tbilisi - Vashlijvari - 0.5 km");
        names.add("Tbilisi - Vashlijvari - 0.5 km");
        names.add("Tbilisi - Vashlijvari - 0.5 km");



        //Initialize the adapter, and set it to the listview
        ListViewAdapter adapter = new ListViewAdapter(StationActivity.this,names);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // TODO Auto-generated method stub
                LinearLayout layout = view.findViewById(R.id.hidden);
                if(layout.getVisibility() == View.VISIBLE) {
                    layout.setVisibility(View.GONE);
                }else {
                    layout.setVisibility(View.VISIBLE);
                }

            }
        });

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
        }
        return true;
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

        Drawable drawable = menu.findItem(R.id.showList).getIcon();
        drawable.mutate();
        drawable.setColorFilter(getResources().getColor(R.color.selected), PorterDuff.Mode.SRC_ATOP);
        return true;
    }

}
