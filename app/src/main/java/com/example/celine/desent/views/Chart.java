package com.example.celine.desent.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import com.example.celine.desent.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by celine on 24/04/17.
 */
public abstract class Chart extends View {

    protected List<Integer> colors = new ArrayList<Integer>();
    protected final int mLightGrey = getResources().getColor(R.color.light_grey);
    protected final int mDarkGrey = getResources().getColor(R.color.dark_grey);
    protected int limitColor;
    protected float limitValue;
    protected int decimalsNumber;
    protected Rect mTextBounds = new Rect();

    //Paints
    protected Paint mValuePaint = new Paint();
    protected Paint mLimitPaint = new Paint();
    protected Paint mBackgroundPaint = new Paint();
    protected Paint mTextPaint = new Paint();

    public Chart(Context context, AttributeSet attr){
        super(context, attr);
        init(context, attr);
    }

    public int getLimitColor() {
        return limitColor;
    }

    public void setLimitColor(int limitColor) {
        this.limitColor = limitColor;
    }

    public int getDecimalsNumber() {
        return decimalsNumber;
    }

    public void setDecimalsNumber(int decimalsNumber) {
        this.decimalsNumber = decimalsNumber;
    }

    public float getLimitValue() {
        return limitValue;
    }

    public void setLimitValue(float limitValue) {
        this.limitValue = limitValue;
    }

    protected abstract void setUpBounds(Canvas canvas);
    protected abstract void drawBackground(Canvas canvas);
    protected abstract void drawCaption(Canvas canvas);
    protected abstract void drawValues(Canvas canvas);
    protected abstract void updateValues();
    protected abstract void init(Context context, AttributeSet attr);

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        setUpBounds(canvas);
        drawBackground(canvas);
        drawCaption(canvas);
        drawValues(canvas);
    }

    public void setColors(List<Integer> colors) {this.colors = colors; }

    public List<Integer> getColors() {return this.colors; }

}
