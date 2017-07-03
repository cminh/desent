package com.example.celine.desent.models;

import com.example.celine.desent.views.EstimationButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by celine on 24/04/17.
 */
public class Indicator {

    protected InputStream inputStream;
    protected String name;
    protected ArrayList<String> columnNames;

    //Charts configs
    protected ArrayList<Integer> colors;
    protected int limitColor;

    protected int maxValue;
    protected int limitValue;
    protected String unit;
    protected int decimalsNumber;

    //TODO: adapt to real data
    protected ArrayList<Float> dailyValues = new ArrayList<Float>();
    protected ArrayList<ArrayList<Float>> weeklyValues = new ArrayList<ArrayList<Float>>();
    protected ArrayList<ArrayList<Float>> monthlyValues = new ArrayList<ArrayList<Float>>();
    //protected ArrayList<ArrayList<Integer>> yearlyValues = new ArrayList<ArrayList<Integer>>();


    public int getLimitValue() {
        return limitValue;
    }

    public void setLimitValue(int limitValue) {
        this.limitValue = limitValue;
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

    public Indicator(InputStream inputStream, String name, String unit, ArrayList<String> columnNames){
        this.inputStream = inputStream;
        this.name = name;
        this.unit = unit;
        this.columnNames = columnNames;
    }

    public Indicator(InputStream inputStream, String name, String unit, String columnName){
        this.inputStream = inputStream;
        this.name = name;
        this.unit = unit;
        this.columnNames = new ArrayList<String>();
        columnNames.add(columnName);
    }

    public void setColor(int color) {
        this.colors = new ArrayList<Integer>();
        this.colors.add(color);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColors(ArrayList<Integer> colors) {this.colors = colors;}

    public ArrayList<Integer> getColors() {
        return colors;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public InputStream getInputStream() {

        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public ArrayList<Float> calculateAverage(ArrayList<ArrayList<Float>> values) {

        ArrayList<Float> weekAverage = new ArrayList<Float>();
        float temp;
        int n;

        for (int i=0; i<values.size(); i++){

            temp = 0;
            n = values.get(i).size();
            for (int j=0; j<n; j++){
                temp = temp + values.get(i).get(j);
            }

            weekAverage.add(temp/n);
        }

        return weekAverage;
    }

    public ArrayList<Float> calculateWeekAverage() {
        return calculateAverage(weeklyValues);
    }

    public ArrayList<Float> calculateMonthAverage() {
        return calculateAverage(monthlyValues);
    }

    public void estimateDailyValues(Date date, EstimationButton estimationButton) {
        estimateDailyValues(date, estimationButton.getName(), estimationButton.getCategoryIndex());
    }

    public void estimateDailyValues(Date date, String columnName, int categoryIndex) {
        int columnIndex;
        int value = -1;
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //TODO: move, maybe

        try{

            String line;
            while((!((line = reader.readLine()).startsWith(name))) || (line == null)){
            }
            line = reader.readLine();;
            ArrayList<String> raw = new ArrayList<String>(Arrays.asList(line.split(",")));

            if (raw.contains(columnName)) {
                columnIndex = raw.indexOf(columnName);

                while(!((line = reader.readLine()).startsWith(dateFormat.format(date))) || (line == null)) {
                }
                raw = new ArrayList<String>(Arrays.asList(line.split(",")));
                dailyValues.set(categoryIndex, Float.parseFloat(raw.get(columnIndex)));
            }

            else if ((categoryIndex < columnNames.size()) && (raw.contains(columnNames.get(categoryIndex)))){
                columnIndex = raw.indexOf(columnNames.get(categoryIndex));

                while(!((line = reader.readLine()).startsWith(dateFormat.format(date))) || (line == null)) {
                }
                raw = new ArrayList<String>(Arrays.asList(line.split(",")));
                dailyValues.set(categoryIndex, Float.parseFloat(raw.get(columnIndex))); //TODO: throw exception
            }


        } catch (IOException e) {
            e.printStackTrace(); //TODO: see errors
        } finally {
            try {
                inputStream.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void readDailyValues(Date date){
        dailyValues.clear();
        ArrayList<Integer> columnIndexes = new ArrayList<Integer>();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //TODO: move, maybe

        try{

            String line;
            while((!((line = reader.readLine()).startsWith(name))) || (line == null)){
            }
            line = reader.readLine();;
            ArrayList<String> raw = new ArrayList<String>(Arrays.asList(line.split(",")));

            for (String columnName: columnNames) {
                if (raw.contains(columnName))
                    columnIndexes.add(raw.indexOf(columnName));
            }
            while(!((line = reader.readLine()).startsWith(dateFormat.format(date))) || (line == null)) {
            }
            raw = new ArrayList<String>(Arrays.asList(line.split(",")));

            for (Integer columnIndex: columnIndexes) {
                dailyValues.add(Float.parseFloat(raw.get(columnIndex)));
            }

        } catch (IOException e) {
            e.printStackTrace(); //TODO: see errors
        } finally {
            try {
                inputStream.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void estimateWeeklyValues(Date date, EstimationButton estimationButton){
        estimateWeeklyValues(date, estimationButton.getName(), estimationButton.getCategoryIndex());
    }

    public void estimateWeeklyValues(Date date, String columnName, int categoryIndex){
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //TODO: move, maybe
        int columnIndex;
        try{
            String line;
            while((!((line = reader.readLine()).startsWith(name))) || (line == null)){
            }
            line = reader.readLine();
            ArrayList<String> raw = new ArrayList<String>(Arrays.asList(line.split(",")));

            if (raw.contains(columnName)) {
                columnIndex = raw.indexOf(columnName);

                while(!((line = reader.readLine()).startsWith(dateFormat.format(date))) || (line == null)) {
                }
                for (int i=0; i<7; i++){ //TODO: prevent missing data
                    raw = new ArrayList<String>(Arrays.asList(line.split(",")));
                    weeklyValues.get(categoryIndex).set(i, Float.parseFloat(raw.get(columnIndex)));
                    line = reader.readLine();
                }
            } else if ((categoryIndex < columnNames.size()) && (raw.contains(columnNames.get(categoryIndex)))) {
                columnIndex = raw.indexOf(columnNames.get(categoryIndex));

                while(!((line = reader.readLine()).startsWith(dateFormat.format(date))) || (line == null)) {
                }
                for (int i=0; i<7; i++){ //TODO: prevent missing data
                    raw = new ArrayList<String>(Arrays.asList(line.split(",")));
                    weeklyValues.get(categoryIndex).set(i, Float.parseFloat(raw.get(columnIndex)));
                    line = reader.readLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void readWeeklyValues(Date date) {
        weeklyValues.clear();
        ArrayList<Integer> columnIndexes = new ArrayList<Integer>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //TODO: move, maybe
        try{
            String line;
            while((!((line = reader.readLine()).startsWith(name))) || (line == null)){
            }
            line = reader.readLine();
            ArrayList<String> raw = new ArrayList<String>(Arrays.asList(line.split(",")));

            for (String columnName: columnNames) {
                if (raw.contains(columnName)) {
                    columnIndexes.add(raw.indexOf(columnName));
                    weeklyValues.add(new ArrayList<Float>());
                }
            }
            while(!((line = reader.readLine()).startsWith(dateFormat.format(date))) || (line == null)) {
            }
            for (int i=0; i<7; i++){ //TODO: prevent missing data
                raw = new ArrayList<String>(Arrays.asList(line.split(",")));
                for (int j=0; j < columnIndexes.size(); j++)
                    weeklyValues.get(j).add(Float.parseFloat(raw.get(columnIndexes.get(j))));
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void estimateMonthlyValues(Date date, EstimationButton estimationButton) {
        estimateMonthlyValues(date, estimationButton.getName(), estimationButton.getCategoryIndex());
    }

    public void estimateMonthlyValues(Date date, String columnName, int categoryIndex){
        int columnIndex;
        int i = 0;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        try{
            String line;
            while((!((line = reader.readLine()).startsWith(name))) || (line == null)){
            }
            line = reader.readLine();
            ArrayList<String> raw = new ArrayList<String>(Arrays.asList(line.split(",")));

            if (raw.contains(columnName)) {
                columnIndex = raw.indexOf(columnName);

                while(!((line = reader.readLine()).startsWith(dateFormat.format(date))) || (line == null)) {
                }
                while((line.startsWith(dateFormat.format(date))) && (line != null)) {
                    raw = new ArrayList<String>(Arrays.asList(line.split(",")));
                    monthlyValues.get(categoryIndex).set(i, Float.parseFloat(raw.get(columnIndex)));//TODO: improve, errors
                    line = reader.readLine();
                    i++;
                }
            } else if ((categoryIndex < columnNames.size()) && (raw.contains(columnNames.get(categoryIndex)))) {
                columnIndex = raw.indexOf(columnNames.get(categoryIndex));

                while(!((line = reader.readLine()).startsWith(dateFormat.format(date))) || (line == null)) {
                }
                while((line.startsWith(dateFormat.format(date))) && (line != null)) {
                    raw = new ArrayList<String>(Arrays.asList(line.split(",")));
                    monthlyValues.get(categoryIndex).set(i, Float.parseFloat(raw.get(columnIndex)));//TODO: improve, errors
                    line = reader.readLine();
                    i++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void readMonthlyValues(Date date){
        monthlyValues.clear();
        ArrayList<Integer> columnIndexes = new ArrayList<Integer>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        try{
            String line;
            while((!((line = reader.readLine()).startsWith(name))) || (line == null)){
            }
            line = reader.readLine();
            ArrayList<String> raw = new ArrayList<String>(Arrays.asList(line.split(",")));

            for (String columnName: columnNames) {
                if (raw.contains(columnName)) {
                    columnIndexes.add(raw.indexOf(columnName));
                    monthlyValues.add(new ArrayList<Float>());
                }
            }
            while(!((line = reader.readLine()).startsWith(dateFormat.format(date))) || (line == null)) {
            }
            while((line.startsWith(dateFormat.format(date))) && (line != null)) {
                raw = new ArrayList<String>(Arrays.asList(line.split(",")));
                for (int j = 0; j < columnIndexes.size(); j++)
                    monthlyValues.get(j).add(Float.parseFloat(raw.get(columnIndexes.get(j))));
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setDailyValues (ArrayList<Float> dailyValues){
        this.dailyValues = dailyValues;
    }

    public ArrayList<Float> getDailyValues(){
        return this.dailyValues;
    }

    public void setWeeklyValues (ArrayList<ArrayList<Float>> weeklyValues){
        this.weeklyValues = weeklyValues;
    }

    public ArrayList<ArrayList<Float>> getWeeklyValues(){
        return this.weeklyValues;
    }

    public void setMonthlyValues (ArrayList<ArrayList<Float>> monthlyValues){
        this.monthlyValues = monthlyValues;
    }

    public ArrayList<ArrayList<Float>> getMonthlyValues (){
        return this.monthlyValues;
    }

}
