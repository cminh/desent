package com.example.desent.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.desent.models.Indicator;
import com.example.desent.R;
import com.example.desent.views.LineChart;

/**
 * Created by celine on 29/04/17.
 */
public class MonthFragment extends Fragment {

    protected Indicator indicator;
    protected LineChart lineChart;

    public Indicator getIndicator() {
        return indicator;
    }

    public void setIndicator(Indicator indicator) {
        this.indicator = indicator;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_month, container, false);
            return view;
    }

    public void refresh(){
        lineChart.setValues(indicator.getMonthlyValues());
        lineChart.setLimitValue(indicator.getLimitValue());
        lineChart.setColors(indicator.getColors());
        lineChart.setDecimalsNumber(indicator.getDecimalsNumber());
        lineChart.setLimitColor(indicator.getLimitColor());
        lineChart.invalidate();
    }

    public void setUp() {
        lineChart = (LineChart) getView().findViewById(R.id.lineChart);
        lineChart.setColors(indicator.getColors());
        lineChart.setValues(indicator.getMonthlyValues());
        lineChart.setLimitValue(indicator.getLimitValue());
        lineChart.setDecimalsNumber(indicator.getDecimalsNumber());
        lineChart.setLimitColor(indicator.getLimitColor());
    }
}
