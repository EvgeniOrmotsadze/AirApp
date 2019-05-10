package ge.gov.air.airgov;

import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;

public class WebViewAct extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view);

        webView = findViewById(R.id.webview);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setTitle(null);
        myToolbar.setBackgroundColor(getResources().getColor(R.color.white));

        SharedPreferences.Editor editor = getSharedPreferences("pref", MODE_PRIVATE).edit();
        editor.putBoolean("wasWebView",true);
        editor.apply();

        String url = this.getIntent().getStringExtra("url");
        webView.loadUrl(url);


    }

    public void backPressed(View view){
        this.onBackPressed();
    }


}
