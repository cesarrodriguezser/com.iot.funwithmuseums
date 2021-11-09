package com.iot.funwithmuseums;

import android.graphics.Canvas;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import com.github.mikephil.charting.utils.Transformer;

public class CustomXAxisRenderer extends XAxisRenderer {
    public CustomXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
        super(viewPortHandler, xAxis, trans);
    }

    @Override
    protected void drawLabel(Canvas c, String formattedLabel, float x, float y, MPPointF anchor, float angleDegrees) {
        int i = 0;
        int j = 0;
        String line[] = formattedLabel.split(" ");
        String cutted[] = new String[]{"","",""};

        for(i = 0; i<line.length;i++){

            if (j<3) {
                if (line[i].equals("Museo")) {
                    cutted[j] = "M.";
                    j++;
                } else if (line[i].equals("Palacio")) {
                    cutted[j] = "P.";
                    j++;
                } else if (line[i].equals("Catedral")) {
                    cutted[j] = "C.";
                    j++;
                } else if (!(line[i].equals("del") ||
                        line[i].equals("de") ||
                        line[i].equals("Real") ||
                        line[i].equals("Nacional")||
                        line[i].equals("y")||
                        line[i].equals("-")||
                        line[i].equals("al")||
                        line[i].equals("la")||
                        line[i].equals("La")||
                        line[i].equals("e"))){
                    cutted[j] = line[i];
                    j++;
                }
            }
        }
        if(cutted[0].equals("M.")||cutted[0].equals("P.")||cutted[0].equals("C.")) {
            Utils.drawXAxisValue(c, cutted[0] + cutted[1], x, y, mAxisLabelPaint, anchor, angleDegrees);
            Utils.drawXAxisValue(c, cutted[2], x
                    , y + mAxisLabelPaint.getTextSize(), mAxisLabelPaint, anchor, angleDegrees);

        }else{
            Utils.drawXAxisValue(c, cutted[0] , x, y, mAxisLabelPaint, anchor, angleDegrees);
            Utils.drawXAxisValue(c, cutted[1], x
                    , y + mAxisLabelPaint.getTextSize(), mAxisLabelPaint, anchor, angleDegrees);
            Utils.drawXAxisValue(c, cutted[2], x
                    , y + 2*mAxisLabelPaint.getTextSize(), mAxisLabelPaint, anchor, angleDegrees);
        }



    }
}