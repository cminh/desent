package com.example.desent.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import com.example.desent.utils.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by celine on 24/04/17.
 */
public class LineChart extends Chart {

    protected ArrayList<ArrayList<Float>> values;

    protected float maxValue;
    protected int increment; //TODO: better name
    protected int maxHeight;
    protected int mVerticalPadding = Utility.dpToPx(10);
    protected int mMarginLeft;

    protected List<Float> sumValues = new ArrayList<Float>();

    public LineChart(Context context, AttributeSet attr) {
        super(context, attr);
    }

    @Override
    protected void setUpBounds(Canvas canvas) {

        mBackgroundPaint.setColor(mDarkGrey);

        mLimitPaint.setAntiAlias(true);
        mLimitPaint.setStyle(Paint.Style.FILL);
        mLimitPaint.setColor(limitColor);
        mLimitPaint.setStrokeWidth(Utility.dpToPx(2));

        mValuePaint.setAntiAlias(true);
        mValuePaint.setStyle(Paint.Style.FILL);
        mValuePaint.setColor(mDarkGrey);
        mValuePaint.setStrokeWidth(Utility.dpToPx(2));

        mTextPaint.setColor(mDarkGrey); //TODO: move maybe
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setTextSize(Utility.spToPx(12));

        String sLimitValue = Utility.floatToStringNDecimals(limitValue, decimalsNumber);
        mTextPaint.getTextBounds(sLimitValue, 0, sLimitValue.length(), mTextBounds);
        mMarginLeft = mTextBounds.right + Utility.dpToPx(10);

        if (!(sumValues.isEmpty()))
            increment = (this.getWidth()-mMarginLeft)/(sumValues.size());

        maxHeight = this.getHeight() - 2*mVerticalPadding;

    }

    protected int valueToHeight(Float value){
        return (3*maxValue < 4*limitValue) ? (int) (maxHeight*(1-(3*value)/(4*limitValue))) : (int) (maxHeight*(1-value/maxValue));
    }

    @Override
    protected void drawBackground(Canvas canvas) {

        canvas.drawLine(mMarginLeft, mVerticalPadding, getWidth(), mVerticalPadding, mBackgroundPaint);
        canvas.drawLine(mMarginLeft, getHeight() - mVerticalPadding, getWidth(), getHeight() - mVerticalPadding, mBackgroundPaint);

    }

    @Override
    protected void drawCaption(Canvas canvas) {
        canvas.drawText(Utility.floatToStringNDecimals((float) 0, decimalsNumber), 0, mVerticalPadding + valueToHeight((float) 0) + (mTextBounds.bottom-mTextBounds.top)/2, mTextPaint);
        canvas.drawText(Utility.floatToStringNDecimals(limitValue, decimalsNumber), 0, mVerticalPadding + valueToHeight(limitValue)+ (mTextBounds.bottom-mTextBounds.top)/2, mTextPaint);

        if (3*maxValue < 4*limitValue)
            canvas.drawText(Utility.floatToStringNDecimals((4*limitValue)/3, decimalsNumber), 0, mVerticalPadding + valueToHeight((4*limitValue)/3)+ (mTextBounds.bottom-mTextBounds.top)/2, mTextPaint);
        else
            canvas.drawText(Utility.floatToStringNDecimals(maxValue, decimalsNumber), 0, mVerticalPadding + valueToHeight(maxValue)+ (mTextBounds.bottom-mTextBounds.top)/2, mTextPaint);
    }

    @Override
    protected void drawValues(Canvas canvas) {

        float x, y1, y2;
        x = increment/2;
        y1 = valueToHeight(sumValues.get(0));

        for (int i = 0; i < sumValues.size(); i++) {

            y2 = valueToHeight(sumValues.get(i));
            canvas.drawLine(mMarginLeft + x, mVerticalPadding + y1, mMarginLeft + x+increment, mVerticalPadding + y2, mValuePaint);
            x = x + increment;
            y1 = y2;

        }

        canvas.drawLine(mMarginLeft, mVerticalPadding + valueToHeight(limitValue), getWidth(), mVerticalPadding + valueToHeight(limitValue), mLimitPaint);

    }

    @Override
    protected void updateValues() {

        maxValue = 0;
        sumValues.clear();
        float temp;

        for (int i = 0; i < values.get(0).size(); i++) {

            temp = 0;
            for (int j = 0; j < values.size(); j++) {
                temp = temp + values.get(j).get(i);
            }
            sumValues.add(temp);
            if (temp > maxValue)
                maxValue = temp;
        }
    }

    @Override
    protected void init(Context context, AttributeSet attr) {

    }

    public void setValues(ArrayList<ArrayList<Float>> values) {
        this.values = values;
        updateValues();
    }

    public ArrayList<ArrayList<Float>> getValues(){return this.values; }
}
