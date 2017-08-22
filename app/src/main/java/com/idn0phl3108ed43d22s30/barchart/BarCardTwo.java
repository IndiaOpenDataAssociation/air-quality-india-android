package com.idn0phl3108ed43d22s30.barchart;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.RelativeLayout;

import com.db.chart.Tools;
import com.db.chart.model.BarSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.BarChartView;
import com.db.chart.view.ChartView;
import com.db.chart.view.animation.Animation;
import com.idn0phl3108ed43d22s30.CardController;
import com.idn0phl3108ed43d22s30.R;

public class BarCardTwo extends CardController {

    private final Context mContext;

    private final BarChartView mChart;

    private String m = "#000";

    private boolean isPublic;

    private String[] mLabels = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X"};

    private float[][] mValues = {{100f, 50.1f, 255.5f, 100.10f, 100f, 50.1f, 255.5f, 100.10f, 100f, 50.1f, 255.5f, 100.10f, 100f, 50.1f, 255.5f, 100.10f, 100f, 50.1f, 255.5f, 100.10f, 100f, 50.1f, 255.5f, 100.10f}};

    public BarCardTwo(RelativeLayout card, Context context, String[] labels, float[][] values, boolean isPublic) {
        super(card);
        mChart = (BarChartView) card.findViewById(R.id.chart4);
        mContext = context;
        mValues = values;
        mLabels = labels;
        this.isPublic = isPublic;
    }

    private Paint paint = new Paint();

    @Override
    protected void show(Runnable action) {
        super.show(action);

        //to display grid
        paint.setColor(Color.parseColor("#F7F7F8"));
        paint.setStrokeWidth(3);

        // Data
        BarSet barSet = new BarSet(mLabels, mValues[0]);
        barSet = new BarSet(mLabels, mValues[0]);

        //to set color between private and public
        if (isPublic) {
            //public
            if (mValues.equals(300)) {
                barSet.setColor(Color.parseColor("#ff0000"));
            }
            barSet.setColor(Color.parseColor("#72c16d"));
        } else {
            //private
            barSet.setColor(Color.parseColor("#00B3BF"));
        }
        //  barSet.setColor(Color.parseColor("#72c16d"));

        mChart.addData(barSet);
        mChart.setBarSpacing(Tools.fromDpToPx(5));
        mChart.setRoundCorners(Tools.fromDpToPx(2));

        // Chart
        mChart.setXAxis(false)
                .setYAxis(false)
                .setAxisBorderValues(0, 1000)
                .setXLabels(AxisController.LabelPosition.OUTSIDE)
                .setYLabels(AxisController.LabelPosition.OUTSIDE)
                .setLabelsColor(Color.parseColor("#808080"))
                .setAxisColor(Color.parseColor("#808080"))
                .setStep(200)
                .setGrid(ChartView.GridType.FULL, paint);

        int[] order = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25};
        final Runnable auxAction = action;
        Runnable chartOneAction = new Runnable() {
            @Override
            public void run() {

                auxAction.run();
                //  showTooltip();
            }
        };

        mChart.show(new Animation()
                .setEndAction(chartOneAction));
    }

    @Override
    public void update() {
        super.update();
        mChart.dismissAllTooltips();
        if (firstStage) mChart.updateValues(0, mValues[0]);
        else mChart.updateValues(0, mValues[0]);
        mChart.notifyDataUpdate();
    }


    @Override
    public void dismiss(Runnable action) {

        super.dismiss(action);
        mChart.dismissAllTooltips();
        mChart.dismiss(new Animation()
                .setEndAction(action));
    }

}
