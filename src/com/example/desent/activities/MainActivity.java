package com.example.desent.activities;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import com.example.desent.views.EstimationButton;
import com.example.desent.models.Indicator;
import com.example.desent.R;
import com.example.desent.views.CircularIndicator;
import com.example.desent.fragments.CategoryFragment;
import com.example.desent.fragments.CircleFragment;
import com.example.desent.fragments.MonthFragment;
import com.example.desent.fragments.WeekFragment;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    Date date = null;
    Spinner timeSpinner;
    Spinner indicatorSpinner;

    //Fragments
    private ArrayList<CircleFragment> circleFragments = new ArrayList<CircleFragment>();
    private WeekFragment weekFragment;
    private MonthFragment monthFragment;
    private CategoryFragment transportationDashboardFragment;
    private CategoryFragment housingDashboardFragment;

    //Indicators
    protected ArrayList<Indicator> indicators = new ArrayList<Indicator>();
    protected Indicator calories;
    protected Indicator expenses;
    protected Indicator carbonFootprint;
    protected Indicator transportation;
    protected Indicator housing;

    //Buttons
    EstimationButton solarPanelButton;
    EstimationButton walkButton;
    EstimationButton cycleButton;
    EstimationButton electricCarButton;
    private TextView captionView;
    private String captionText = "";

    public enum ActiveView {
        DASHBOARD,
        DAY,
        WEEK,
        MONTH
    }

    @Override
    public void onBackPressed(){
        indicatorSpinner.setSelection(0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("indicatorSpinner", indicatorSpinner.getSelectedItemPosition());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUp();
        solarPanelButton.setOnClickListener(estimationButtonHandler);
        walkButton.setOnClickListener(estimationButtonHandler);
        cycleButton.setOnClickListener(estimationButtonHandler);
        electricCarButton.setOnClickListener(estimationButtonHandler);

        timeSpinner.setOnItemSelectedListener(timeSpinnerActivity);
        indicatorSpinner.setOnItemSelectedListener(indicatorSpinnerActivity);

        for (final CircleFragment circleFragment: circleFragments){
            circleFragment.getView().setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    indicatorSpinner.setSelection(1+circleFragments.indexOf(circleFragment));
                }

            });
        }

        goToDashboard();

        }

    AdapterView.OnItemSelectedListener timeSpinnerActivity = new AdapterView.OnItemSelectedListener() {

        public void onItemSelected (AdapterView<?> parent, View view, int pos, long id) {
            switch (pos) {
                case 0:
                    goToDailyView();
                    break;
                case 1:
                    goToWeeklyView();
                    break;
                case 2:
                    goToMonthlyView();
            }

        }

        public void onNothingSelected(AdapterView<?> parent) {
        }

    };

    AdapterView.OnItemSelectedListener indicatorSpinnerActivity = new AdapterView.OnItemSelectedListener() {

        public void onItemSelected (AdapterView<?> parent, View view, int pos, long id) {
            switch (pos) {
                case 0:
                    goToDashboard();
                    timeSpinner.setSelection(0);
                    timeSpinner.setVisibility(View.GONE);
                    break;
                case 1:
                    goToIndicatorView(carbonFootprint);
                    break;
                case 2:
                    goToIndicatorView(calories);
                    break;
                case 3:
                    goToIndicatorView(expenses);
            }

            if((pos>0) && (timeSpinner.getVisibility() == View.GONE)){
                goToDailyView();
                timeSpinner.setVisibility(View.VISIBLE);
            }


        }

        public void onNothingSelected(AdapterView<?> parent) {
        }

    };

    View.OnClickListener estimationButtonHandler = new View.OnClickListener() {

        public void onClick(View v) {

            EstimationButton button = (EstimationButton) v;

            if(button.getId() == R.id.walkingButton){
                if (cycleButton.isActivated()){
                    disableEstimation(cycleButton);
                }
                if (electricCarButton.isActivated()){
                    disableEstimation(electricCarButton);
                }
            }

            if(button.getId() == R.id.cyclingButton){
                if (walkButton.isActivated()){
                    disableEstimation(walkButton);
                }
                if (electricCarButton.isActivated()){
                    disableEstimation(electricCarButton);
                }
            }

            if(button.getId() == R.id.electricCarButton){
                if (walkButton.isActivated()){
                    disableEstimation(walkButton);
                }
                if (cycleButton.isActivated()){
                    disableEstimation(cycleButton);
                }
            }

            if (!button.isActivated()) {
                enableEstimation(button);
            } else
                disableEstimation(button);

            if (!(walkButton.isActivated() || cycleButton.isActivated() || electricCarButton.isActivated())) {

                for (Indicator indicator : indicators) {
                    indicator.estimateDailyValues(date, "Transportation", 0);
                    indicator.estimateWeeklyValues(date, "Transportation", 0);
                    indicator.estimateMonthlyValues(date, "Transportation", 0);
                }

            }

            if (!(solarPanelButton.isActivated())) {

                for (Indicator indicator : indicators) {
                    indicator.estimateDailyValues(date, "Housing", 1);
                    indicator.estimateWeeklyValues(date, "Housing", 1);
                    indicator.estimateMonthlyValues(date, "Housing", 1);
                }
            }


            for (CircleFragment circleFragment: circleFragments)
                circleFragment.refresh();

            transportationDashboardFragment.refresh();
            housingDashboardFragment.refresh();
            weekFragment.refresh();
            monthFragment.refresh();
        }
    };

    public void enableEstimation(EstimationButton estimationButton){

        estimationButton.setActivated(true);

        if (captionText.equals(""))
            captionText = "Your performance " + estimationButton.getCaption();
        else
            captionText = captionText + " and " + estimationButton.getCaption();

        for(Indicator indicator : indicators) {
            indicator.estimateDailyValues(date, estimationButton);
            indicator.estimateWeeklyValues(date, estimationButton);
            indicator.estimateMonthlyValues(date, estimationButton);
        }

        for (CircleFragment circleFragment: circleFragments) {
            circleFragment.refresh();
        }

        ((TextView) findViewById(R.id.caption)).setText(captionText);
        weekFragment.refresh();
        monthFragment.refresh();

    }

    public void disableEstimation(EstimationButton estimationButton){

        estimationButton.setActivated(false);

        captionText = captionText.replace(estimationButton.getCaption(), "");
        if (captionText.contains("and"))
            captionText = captionText.replace(" and ", "");
        else
            captionText = "";
        ((TextView) findViewById(R.id.caption)).setText(captionText);

        weekFragment.refresh();
        monthFragment.refresh();

    }

    protected void goToDashboard() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        for (CircleFragment circleFragment: circleFragments){
            ft.hide(circleFragment);
        }

        ft.commit();

        ft = getFragmentManager().beginTransaction();

        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        //ft.addToBackStack("dashboard");

        for (CircleFragment circleFragment: circleFragments){
            circleFragment.setActiveView(ActiveView.DASHBOARD);
            ft.show(circleFragment);
        }

        timeSpinner.setVisibility(View.GONE);
        transportationDashboardFragment.setActiveView(ActiveView.DASHBOARD);
        housingDashboardFragment.setActiveView(ActiveView.DASHBOARD);
        captionView.setVisibility(View.VISIBLE);
        ft.hide(weekFragment);
        ft.hide(monthFragment);
        ft.show(transportationDashboardFragment);
        ft.show(housingDashboardFragment);

        ft.commit();

    }

    protected void goToIndicatorView(Indicator indicator) {

        FragmentTransaction ft = getFragmentManager().beginTransaction();

        for (CircleFragment circleFragment: circleFragments){
            ft.hide(circleFragment);
        }

        ft.commit();


        ft = getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        for (CircleFragment circleFragment: circleFragments){

            circleFragment.saveInitialHeight();

            if (indicator.getName().equals(circleFragment.getIndicator().getName()))
                ft.show(circleFragment);

        }

        weekFragment.setIndicator(indicator);
        weekFragment.refresh();
        monthFragment.setIndicator(indicator);
        monthFragment.refresh();
        indicatorSpinner.setSelection(indicators.indexOf(indicator)+1);

        if (indicator != calories){
            transportationDashboardFragment.setIndicator(indicator);
            housingDashboardFragment.setIndicator(indicator);
            ft.show(transportationDashboardFragment);
            ft.show(housingDashboardFragment);
        } else {
            ft.hide(transportationDashboardFragment);
            ft.hide(housingDashboardFragment);
        }

        ft.commit();

    }

    protected void goToDailyView() {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_EXIT_MASK);

        captionView.setVisibility(View.VISIBLE);
        ft.hide(weekFragment);
        ft.hide(monthFragment);
        ft.commit();

        for (CircleFragment circleFragment: circleFragments){
            circleFragment.setActiveView(ActiveView.DAY);
        }

        transportationDashboardFragment.setActiveView(ActiveView.DAY);
        housingDashboardFragment.setActiveView(ActiveView.DAY);
    }

    protected void goToWeeklyView() {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        captionView.setVisibility(View.GONE);
        ft.show(weekFragment);
        ft.hide(monthFragment);
        ft.commit();

        for (CircleFragment circleFragment: circleFragments)
            circleFragment.setActiveView(ActiveView.WEEK);

        transportationDashboardFragment.setActiveView(ActiveView.WEEK);
        housingDashboardFragment.setActiveView(ActiveView.WEEK);

    }

    protected void goToMonthlyView() {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        //ft.addToBackStack("month");

        captionView.setVisibility(View.GONE);
        ft.hide(weekFragment);
        ft.show(monthFragment);
        ft.commit();

        for (CircleFragment circleFragment: circleFragments)
            circleFragment.setActiveView(ActiveView.MONTH);

        transportationDashboardFragment.setActiveView(ActiveView.MONTH);
        housingDashboardFragment.setActiveView(ActiveView.MONTH);

    }

    protected void setUp() {

        //Colors
        int mRed = getResources().getColor(R.color.red);
        int mOrange = getResources().getColor(R.color.orange);
        int mGreen = getResources().getColor(R.color.green);
        int mBlue = getResources().getColor(R.color.blue);
        int mDarkGrey = getResources().getColor(R.color.dark_grey);
        int mLightGrey = getResources().getColor(R.color.light_grey);

        //Limit values
        int targetCalories = 1700;
        int limitExpenses = 400;
        int limitCarbonFootprint = 4;

        //Fragments
        CircleFragment caloriesCircleFragment;
        CircleFragment expensesCircleFragment;
        CircleFragment carbonFootprintCircleFragment;

        circleFragments.add(carbonFootprintCircleFragment = (CircleFragment) getFragmentManager().findFragmentById(R.id.dailyCarbonFootprint));
        circleFragments.add(caloriesCircleFragment = (CircleFragment) getFragmentManager().findFragmentById(R.id.dailyCalories));
        circleFragments.add(expensesCircleFragment = (CircleFragment) getFragmentManager().findFragmentById(R.id.dailyExpenses));

        weekFragment = (WeekFragment) getFragmentManager().findFragmentById(R.id.weeklyData);
        monthFragment = (MonthFragment) getFragmentManager().findFragmentById(R.id.monthlyData);

        transportationDashboardFragment = (CategoryFragment) getFragmentManager().findFragmentById(R.id.transportation_dashboard_fragment);
        housingDashboardFragment = (CategoryFragment) getFragmentManager().findFragmentById(R.id.housing_dashboard_fragment);

        captionView = (TextView) findViewById(R.id.caption);
        captionView.setTextColor(mDarkGrey);

        //Date
        InputStream inputStream = getResources().openRawResource(R.raw.data);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = dateFormat.parse("2017-04-01");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Spinners
        timeSpinner = (Spinner) findViewById(R.id.time_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.time_spinner_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(adapter);

        indicatorSpinner = (Spinner) findViewById(R.id.indicator_spinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.indicator_spinner_array, android.R.layout.simple_spinner_item);
        indicatorSpinner.setAdapter(adapter);

        //Indicators
        ArrayList<String> columnNames = new ArrayList<String>();
        columnNames.add("Transportation");
        columnNames.add("Housing"); //Prevent errors for estimations
        indicators.add(carbonFootprint = new Indicator(inputStream, "Carbon footprint", "kgCO2", columnNames));
        indicators.add(calories = new Indicator(inputStream, "Calories", "kCal", columnNames));
        indicators.add(expenses = new Indicator(inputStream, "Expenses", "kr", columnNames));

        transportation = new Indicator(inputStream, "Transportation", "km", "Distance");
        housing = new Indicator(inputStream, "Housing", "kWh", "Energy consumption");

        carbonFootprintCircleFragment.setStartAngle(135);
        carbonFootprintCircleFragment.setSweepAngle(270);
        carbonFootprintCircleFragment.setImgName("earth");
        carbonFootprintCircleFragment.setNumberOfStates(5);

        calories.setColor(mOrange);
        calories.setLimitColor(mLightGrey);

        ArrayList<Integer> energyTransportationColors = new ArrayList<Integer>();
        energyTransportationColors.add(mGreen);
        energyTransportationColors.add(mBlue);
        carbonFootprint.setColors(energyTransportationColors);
        carbonFootprint.setLimitColor(mRed);
        expenses.setColors(energyTransportationColors);
        expenses.setLimitColor(mRed);

        transportation.setColor(mGreen);
        transportation.setDecimalsNumber(1);
        housing.setColor(mBlue);
        housing.setDecimalsNumber(1);

        for(Indicator indicator : indicators){
            indicator.readDailyValues(date);
            indicator.readWeeklyValues(date);
            indicator.readMonthlyValues(date);
        }

        transportation.readDailyValues(date);
        housing.readDailyValues(date);

        //Estimation buttons
        solarPanelButton = (EstimationButton) findViewById(R.id.solarPanelButton);
        solarPanelButton.setCaption("with a solar installation");
        walkButton = (EstimationButton) findViewById(R.id.walkingButton);
        walkButton.setCaption("walking 5km instead of driving");
        cycleButton = (EstimationButton) findViewById(R.id.cyclingButton);
        cycleButton.setCaption("cycling 5km instead of driving");
        electricCarButton = (EstimationButton) findViewById(R.id.electricCarButton);
        electricCarButton.setCaption("with the Nissan Leaf");

        calories.setMaxValue(targetCalories);
        calories.setLimitValue(targetCalories);
        expenses.setMaxValue(limitExpenses);
        expenses.setLimitValue(limitExpenses);
        carbonFootprint.setMaxValue(2*limitCarbonFootprint);
        carbonFootprint.setLimitValue(limitCarbonFootprint);

        calories.setDecimalsNumber(0);
        expenses.setDecimalsNumber(0);
        carbonFootprint.setDecimalsNumber(1);

        //Fragments
        caloriesCircleFragment.setIndicator(calories);
        caloriesCircleFragment.setFormat(CircularIndicator.Format.CIRCLE_TEXT);
        caloriesCircleFragment.setUp();

        expensesCircleFragment.setIndicator(expenses);
        expensesCircleFragment.setFormat(CircularIndicator.Format.CIRCLE_TEXT);
        expensesCircleFragment.setUp();

        carbonFootprintCircleFragment.setIndicator(carbonFootprint);
        carbonFootprintCircleFragment.setFormat(CircularIndicator.Format.CIRCLE_IMG_TEXT);
        carbonFootprintCircleFragment.setUp();

        weekFragment.setIndicator(carbonFootprint);
        weekFragment.setUp();

        monthFragment.setIndicator(carbonFootprint);
        monthFragment.setUp();

        transportationDashboardFragment.setCategory(transportation);
        transportationDashboardFragment.setCategoryIndex(0);
        transportationDashboardFragment.setUp();

        housingDashboardFragment.setCategory(housing);
        housingDashboardFragment.setCategoryIndex(1);
        housingDashboardFragment.setUp();
    }
}