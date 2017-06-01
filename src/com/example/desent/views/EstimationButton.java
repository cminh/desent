package com.example.desent.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageButton;
import com.example.desent.R;

/**
 * Created by celine on 04/04/17.
 */
public class EstimationButton extends ImageButton {

    protected String name;
    protected int categoryIndex; //0: transportation, 1: energy //TODO: not elegant
    protected String caption;

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public EstimationButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs){
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.EstimationButton, 0, 0);
        try{
            name = a.getString(R.styleable.EstimationButton_name);
            categoryIndex = a.getInt(R.styleable.EstimationButton_category_index, 0);
        } finally{
            a.recycle();
        }
    }

    public String getName () { return this.name; }

    public void setName(String name){ this.name = name; }

    public int getCategoryIndex() {
        return categoryIndex;
    }

    public void setCategoryIndex(int categoryIndex) {
        this.categoryIndex = categoryIndex;
    }
}
