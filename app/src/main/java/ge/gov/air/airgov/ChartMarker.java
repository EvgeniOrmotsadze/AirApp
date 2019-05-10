package ge.gov.air.airgov;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

public class ChartMarker extends MarkerView {

    public ChartMarker(Context context){
        super(context, R.layout.chart_marker);
    }


    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        Log.d("loggers ", e.toString());
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth()/2),-(getHeight()/2));
    }

    @Override
    public void draw(Canvas canvas, float posX, float posY) {
        super.draw(canvas, posX, posY);
        getOffsetForDrawingAtPoint(posX,posY);
    }
}
