package com.idn0phl3108ed43d22s30.Linechart;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.RelativeLayout;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.BounceEase;
import com.idn0phl3108ed43d22s30.CardController;
import com.idn0phl3108ed43d22s30.R;

/**
 * Created by Rutul on 15-12-2016.
 */
public class LineCardTwo extends CardController {

    private final LineChartView mChart;
    private String m = "#000";

    private boolean isPublic;

    private final Context mContext;


    private String[] mLabels = {};
    private float[][] mValues = {};


    private Runnable mBaseAction;
    private Paint paint = new Paint();

    public LineCardTwo(RelativeLayout card, Context context, String[] labels, float[][] values, boolean isPublic) {

        super(card);
        mContext = context;
        mChart = (LineChartView) card.findViewById(R.id.chart2);
        mValues = values;
        mLabels = labels;
        this.isPublic = isPublic;
    }

    @Override
    public void show(Runnable action) {
        super.show(action);
        // paint.setColor(Color.BLACK);

        paint.setColor(Color.parseColor("#F7F7F8"));
        paint.setStrokeWidth(4);
        // Data
        LineSet dataset = new LineSet(mLabels, mValues[0]);

        dataset = new LineSet(mLabels, mValues[0]);
        if (isPublic) {
            dataset.setColor(Color.parseColor("#72c16d")).
                    //setFill(Color.parseColor("#00b3bf")).
                            setGradientFill(new int[]{Color.parseColor("#72c16d"), Color.parseColor("#10DDF5F6")}, null)
                    .setThickness(5)
                    .setSmooth(true);
        } else {
            dataset.setColor(Color.parseColor("#00B3BF")).
                    //setFill(Color.parseColor("#00b3bf")).
                            setGradientFill(new int[]{Color.parseColor("#99E1E5"), Color.parseColor("#10DDF5F6")}, null)
                    .setThickness(5)
                    .setSmooth(true);
        }

        mChart.addData(dataset);

        // Chart
        mChart.setBorderSpacing(Tools.fromDpToPx(1))
                .setAxisBorderValues(0, 1000)
                .setYLabels(AxisController.LabelPosition.OUTSIDE)
                .setLabelsColor(Color.parseColor("#808080"))
                .setXAxis(false)
                .setYAxis(false)
                .setStep(200)
                .setGrid(ChartView.GridType.FULL, paint);

        mBaseAction = action;
        Runnable chartAction = new Runnable() {
            @Override
            public void run() {
                mBaseAction.run();


            }
        };

        Animation anim = new Animation()
                .setEasing(new BounceEase())
                .setEndAction(chartAction);

        mChart.show(anim);
    }


    @Override
    public void update() {
        super.update();

        mChart.dismissAllTooltips();
        if (firstStage) {
            mChart.updateValues(0, mValues[1]);
            mChart.updateValues(1, mValues[1]);
        } else {
            mChart.updateValues(0, mValues[0]);
            mChart.updateValues(1, mValues[0]);
        }
        mChart.getChartAnimation().setEndAction(mBaseAction);
        mChart.notifyDataUpdate();
    }


    @Override
    public void dismiss(Runnable action) {
        super.dismiss(action);

        mChart.dismissAllTooltips();
        mChart.dismiss(new Animation()
                .setEasing(new BounceEase())
                .setEndAction(action));
    }
}
