package com.example.celine.desent.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import com.example.celine.desent.utils.Utility;

import java.util.ArrayList;

/**
 * Created by celine on 24/04/17.
 */
public class BarChart extends Chart {

    protected ArrayList<ArrayList<Float>> values;

    protected int barWidth;
    protected int maxHeight;
    protected float maxValue;
    protected int mVerticalPadding = Utility.dpToPx(10);
    protected int mMarginLeft;

    public BarChart(Context context, AttributeSet attr) {
        super(context, attr);
    }

    protected void drawBar(Canvas canvas, int x, float y, float height) {
        canvas.drawRect(x, maxHeight-y-height + mVerticalPadding, x+barWidth, maxHeight-y + mVerticalPadding, mValuePaint);
    }

    protected float valueToHeight(Float value){
        return (3*maxValue < 4*limitValue) ? (int) (3*maxHeight*value)/(4*limitValue) : (int) (maxHeight*value/maxValue);
    }

    @Override
    protected void init(Context context, AttributeSet attr){

        mValuePaint.setAntiAlias(true);
        mValuePaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void setUpBounds(Canvas canvas){

        mBackgroundPaint.setColor(mDarkGrey);

        mLimitPaint.setAntiAlias(true);
        mLimitPaint.setStyle(Paint.Style.FILL);
        mLimitPaint.setColor(limitColor);
        mLimitPaint.setStrokeWidth(Utility.dpToPx(2));

        mValuePaint.setAntiAlias(true);
        mValuePaint.setStyle(Paint.Style.FILL);

        mTextPaint.setColor(mDarkGrey); //TODO: move maybe
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setTextSize(Utility.spToPx(12));

        String sLimitValue = Utility.floatToStringNDecimals(limitValue, decimalsNumber);
        mTextPaint.getTextBounds(sLimitValue, 0, String.valueOf(sLimitValue).length(), mTextBounds);
        mMarginLeft = mTextBounds.right + Utility.dpToPx(10);

        barWidth = (this.getWidth() - mMarginLeft)/11; //TODO: change later
        maxHeight = this.getHeight() - 2*mVerticalPadding;

    }

    @Override
    protected void drawBackground(Canvas canvas) {

        canvas.drawLine(mMarginLeft, mVerticalPadding, getWidth(), mVerticalPadding, mBackgroundPaint); //TODO: change paint
        canvas.drawLine(mMarginLeft, getHeight()-mVerticalPadding, getWidth(), getHeight()-mVerticalPadding, mBackgroundPaint);
    }

    @Override
    protected void drawCaption(Canvas canvas) {
        canvas.drawText(Utility.floatToStringNDecimals((float) 0, decimalsNumber), 0, mVerticalPadding + maxHeight - valueToHeight((float) 0) + (mTextBounds.bottom-mTextBounds.top)/2, mTextPaint);
        canvas.drawText(Utility.floatToStringNDecimals(limitValue, decimalsNumber), 0, mVerticalPadding + maxHeight -  valueToHeight(limitValue)+ (mTextBounds.bottom-mTextBounds.top)/2, mTextPaint);

        if (3*maxValue < 4*limitValue)
            canvas.drawText(Utility.floatToStringNDecimals((4*limitValue)/3, decimalsNumber), 0, mVerticalPadding + maxHeight - valueToHeight((4*limitValue)/3)+ (mTextBounds.bottom-mTextBounds.top)/2, mTextPaint);
        else
            canvas.drawText(Utility.floatToStringNDecimals(maxValue, decimalsNumber), 0, mVerticalPadding + maxHeight - valueToHeight(maxValue)+ (mTextBounds.bottom-mTextBounds.top)/2, mTextPaint);
    }

    @Override
    protected void drawValues(Canvas canvas) {

        int x = mMarginLeft +  barWidth/2;
        float y, h;

        for (int i = 0; i < values.get(0).size(); i++) {

            y = 0;
            for (int j = 0; j < values.size(); j++){
                h = valueToHeight(values.get(j).get(i));
                mValuePaint.setColor(colors.get(j));
                drawBar(canvas, x, y, h);
                y = y+h;
            }
            x = x + (3*barWidth)/2;

        }

        canvas.drawLine(mMarginLeft, maxHeight - valueToHeight(limitValue) + mVerticalPadding, getWidth(), maxHeight - valueToHeight(limitValue) + mVerticalPadding, mLimitPaint);

    }

    @Override
    protected void updateValues() {

        maxValue = 0;
        float temp;

        for (int i = 0; i < values.get(0).size(); i++) {

            temp = 0;
            for (int j = 0; j < values.size(); j++) {
                temp = temp + values.get(j).get(i);
            }

            if (temp > maxValue)
                maxValue = temp;
        }

    }

    public void setValues(ArrayList<ArrayList<Float>> values) {
        this.values = values;
        updateValues();
    }

    public ArrayList<ArrayList<Float>> getValues(){return this.values; }

}
