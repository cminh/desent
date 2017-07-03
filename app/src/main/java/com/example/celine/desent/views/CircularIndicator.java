package com.example.celine.desent.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import com.example.celine.desent.utils.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by celine on 10/03/17.
 */
public class CircularIndicator extends Chart {

    protected ArrayList<Float> values;

    public enum Format {
        TEXT_ONLY,
        IMG_ONLY,
        CIRCLE_TEXT,
        CIRCLE_IMG,
        CIRCLE_IMG_TEXT;
    }

    Format format;

    //Rectangles
    protected RectF mCircleBounds;
    protected RectF mImageBounds;
    protected Rect mBigTextBounds;

    //Sizes
    private int mBigTextSizeFactor = 7;
    private int mTextSizeFactor = 9;
    protected int mBackgroundThicknessFactor = 30;
    protected int mProgressThicknessFactor = 25;
    protected int mImageMarginFactor = 7;


    //Angles
    protected int startAngle;
    protected int sweepAngle; //TODO: mayday, mayday

    //Paints
    private Paint mProgressPaint = new Paint();
    private Paint bigTextPaint = new Paint();

    //Text
    protected String unit;
    protected int maxValue;
    protected float totalValue;

    //Image
    private Bitmap image;
    protected String imgName;
    protected int imgState = 1;
    protected int numberOfStates;

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public void setMaxValue(int maxValue) { this.maxValue = maxValue; }

    public int getMaxValue() {return this.maxValue; }

    public void setColors(List<Integer> colors) {this.colors = colors; }

    public List<Integer> getColors() {return this.colors; }

    public void setValues(ArrayList<Float> values) {
        this.values = values;
        updateValues();
    }

    public ArrayList<Float> getValues(){return this.values; }

    public int getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(int startAngle) {
        this.startAngle = startAngle;
    }

    public int getSweepAngle() {
        return sweepAngle;
    }

    public void setSweepAngle(int sweepAngle) {
        this.sweepAngle = sweepAngle;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public int getNumberOfStates() {
        return numberOfStates;
    }

    public void setNumberOfStates(int numberOfStates) {
        this.numberOfStates = numberOfStates;
    }

    public CircularIndicator(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    protected int valueToAngle(Float value){
        return (int) (sweepAngle * value) / maxValue ;
    }

    public void updateTotalValue(){
        totalValue = 0;
        for (int i=0; i<values.size(); i++){
            totalValue = totalValue + values.get(i);
        }
    }

    public void updateImgState(){
        if (maxValue != 0) {
            int state = (int) ((numberOfStates - 1) * totalValue) / maxValue + 1;
            imgState = (state < numberOfStates) ? state : numberOfStates;
            if (imgName != null) {
                Resources res = getResources();
                image = BitmapFactory.decodeResource(res, res.getIdentifier(imgName + String.valueOf(imgState), "drawable", getContext().getPackageName()));
            }
        }
    }

    public void updateImg(){
        if (imgName != null){
            Resources res = getResources();
            image = BitmapFactory.decodeResource(res, res.getIdentifier(imgName, "drawable", getContext().getPackageName()));
        }
    }

    protected void drawValues(Canvas canvas){

        float tempStartAngle = startAngle;
        float tempSweepAngle;

        if ((format != Format.TEXT_ONLY) && (format != Format.IMG_ONLY) && (maxValue !=0)) {
            for (int i = 0; i < values.size(); i++) {
                tempSweepAngle = valueToAngle(values.get(i));
                mProgressPaint.setColor(colors.get(i));
                if (tempStartAngle+tempSweepAngle < startAngle+sweepAngle)
                    canvas.drawArc(mCircleBounds, tempStartAngle, tempSweepAngle, false, mProgressPaint);
                else
                    canvas.drawArc(mCircleBounds, tempStartAngle, sweepAngle-tempStartAngle+startAngle, false, mProgressPaint);
                tempStartAngle = tempStartAngle + tempSweepAngle;
            }
        }

        if ((format == Format.CIRCLE_IMG_TEXT) && (imgName != null)){
            updateImgState();
            canvas.drawBitmap(image, null, mImageBounds, mBackgroundPaint);
        } else if (format == Format.CIRCLE_IMG) {
            updateImg();
            canvas.drawBitmap(image, null, mImageBounds, mBackgroundPaint);

        } else if (format == Format.IMG_ONLY){
            updateImg();
            canvas.drawBitmap(image, null, mCircleBounds, mBackgroundPaint);
        }
    }

    @Override
    protected void updateValues() {
        this.updateTotalValue();
        if (((format == Format.CIRCLE_IMG) || (format == Format.CIRCLE_IMG_TEXT)) &&(maxValue != 0))
            this.updateImgState();
    }

    @Override
    protected void init(Context context, AttributeSet attrs){

        if (((format == Format.IMG_ONLY) || (format == Format.CIRCLE_IMG) || (format == Format.CIRCLE_IMG_TEXT)) && (imgName != null)) {
            Resources res = getResources();
            if (numberOfStates != 0)
                image = BitmapFactory.decodeResource(res, res.getIdentifier(imgName + String.valueOf(imgState), "drawable", context.getPackageName()));
            else
                image = BitmapFactory.decodeResource(res, res.getIdentifier(imgName, "drawable", context.getPackageName()));
        }
    }

    protected void setUpBounds(Canvas canvas){

        //Bounds
        int x = this.getWidth();
        int y = this.getHeight();
        int r = (x<y) ? x : y;

        int backgroundThickness = r/mBackgroundThicknessFactor;
        int progressThickness = r/mProgressThicknessFactor;
        int imageMargin = r/mImageMarginFactor;

        mCircleBounds = new RectF((x-r)/2 + progressThickness, (y-r)/2 + progressThickness, (x+r)/2 - progressThickness, (y+r)/2-progressThickness);
        mImageBounds = new RectF((x-r)/2 + imageMargin, (y-r)/2 + imageMargin,  (x+r)/2 - imageMargin,  (y+r)/2 - imageMargin);
        mBigTextBounds = new Rect();

        //Paints

        mBackgroundPaint.setColor(mLightGrey);
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setStyle(Style.STROKE);
        mBackgroundPaint.setStrokeWidth(backgroundThickness);

        //mProgressPaint.setColor(colors.get(0));
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStyle(Style.STROKE);
        mProgressPaint.setStrokeWidth(progressThickness);

        bigTextPaint.setColor(mDarkGrey);
        bigTextPaint.setAntiAlias(true);
        bigTextPaint.setStyle(Style.FILL);
        bigTextPaint.setTextAlign(Paint.Align.CENTER);
        bigTextPaint.setTextSize(x/ mBigTextSizeFactor);
        bigTextPaint.setFakeBoldText(true);
        bigTextPaint.getTextBounds(String.valueOf(totalValue), 0, String.valueOf(totalValue).length(), mBigTextBounds);

        mTextPaint.setColor(mDarkGrey);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Style.FILL);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(x/mTextSizeFactor); //TODO: make it more responsive
        mTextPaint.getTextBounds(String.valueOf(unit), 0, String.valueOf(unit).length(), mTextBounds);

    }

    @Override
    protected void drawBackground(Canvas canvas) {
        if ((format == Format.CIRCLE_TEXT) || (format == Format.CIRCLE_IMG) || (format == Format.CIRCLE_IMG_TEXT))
            canvas.drawArc(mCircleBounds, startAngle, sweepAngle, false, mBackgroundPaint);
    }

    @Override
    protected void drawCaption(Canvas canvas) {

        if (format == Format.CIRCLE_TEXT) {
            canvas.drawText(unit, getWidth()/2, getHeight()/4 - mTextBounds.top/2, mTextPaint);
            canvas.drawText(Utility.floatToStringNDecimals(totalValue, decimalsNumber), getWidth()/2, getHeight()/2 - mBigTextBounds.top/2, bigTextPaint);

        } else if (format == Format.CIRCLE_IMG_TEXT){
            canvas.drawText(Utility.floatToStringNDecimals(totalValue, decimalsNumber) + " " + unit, getWidth()/2, getHeight() + mTextBounds.top/2, bigTextPaint);
        } else if (format == Format.TEXT_ONLY)
            canvas.drawText(Utility.floatToStringNDecimals(totalValue, decimalsNumber) + " " + unit, getWidth()/2, getHeight()/2 - mTextBounds.top/2, bigTextPaint);

    }

    @Override
    protected
    void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int x = getMeasuredWidth();
        int y = getMeasuredHeight();

        if (format != Format.TEXT_ONLY){
            int r = (x<y) ? x : y;
            setMeasuredDimension(r,r);
        }

    }
}
