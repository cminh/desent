package com.example.celine.desent.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.celine.desent.models.Indicator;
import com.example.celine.desent.R;
import com.example.celine.desent.views.BarChart;

/**
 * Created by celine on 29/04/17.
 */
public class WeekFragment extends Fragment {

    protected Indicator indicator;
    protected BarChart barChart;

    public Indicator getIndicator() {
        return indicator;
    }

    public void setIndicator(Indicator indicator) {
        this.indicator = indicator;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_week, container, false);
            return view;
    }

    public void refresh(){
        barChart.setValues(indicator.getWeeklyValues());
        barChart.setLimitValue(indicator.getLimitValue());
        barChart.setColors(indicator.getColors());
        barChart.setDecimalsNumber(indicator.getDecimalsNumber());
        barChart.setLimitColor(indicator.getLimitColor());
        barChart.invalidate();
    }

    public void setUp() {
        barChart = (BarChart) getView().findViewById(R.id.barChart);
        barChart.setColors(indicator.getColors());
        barChart.setValues(indicator.getWeeklyValues());
        barChart.setLimitValue(indicator.getLimitValue());
        barChart.setDecimalsNumber(indicator.getDecimalsNumber());
        barChart.setLimitColor(indicator.getLimitColor());
    }
}
