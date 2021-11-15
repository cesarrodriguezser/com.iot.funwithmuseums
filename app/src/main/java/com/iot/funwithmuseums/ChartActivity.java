package com.iot.funwithmuseums;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.RadioGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.iot.funwithmuseums.database.ViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChartActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{

    BarChart barChart;
    BarData data;
    BarDataSet dataSet;
    RadioGroup radioGroup;
    ViewModel myViewmodel;
    Observer<List<Item>> observer;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        barChart = findViewById(R.id.barChart);
        radioGroup = findViewById(R.id.radioshow);
        radioGroup.setOnCheckedChangeListener(this);

        myViewmodel = ViewModelProviders.of(this).get(ViewModel.class);

        XAxis x = barChart.getXAxis();
        YAxis y = barChart.getAxisLeft();



        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setGranularity(1.0f);
        barChart.getAxisRight().setEnabled(false);

        barChart.setXAxisRenderer(new CustomXAxisRenderer(barChart.getViewPortHandler(), barChart.getXAxis(),
                barChart.getTransformer(YAxis.AxisDependency.LEFT)));
        barChart.getLegend().setEnabled(false);
        Description desc = new Description();
        desc.setText("");
        barChart.setDescription(desc);
        barChart.setFitBars(true);
        x.setLabelRotationAngle(-10);

        observer = new Observer<List<Item>>() {

            @Override
            public void onChanged(List<Item> items) {
                resetChart();
                ArrayList<BarEntry> entries = new ArrayList<>();
                String[] savetexts = new String[items.size()];
                for (int i = 0; i < items.size(); i++) {
                    entries.add( new BarEntry(i, items.get(i).getStepAvg())) ;
                    savetexts[i] = items.get(i).getDisplayText();
                }
                BarDataSet barDataSet = new BarDataSet(entries,"");
                barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                BarData barData = new BarData(barDataSet);

                barChart.setData(barData);
                x.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return savetexts[(int) value];
                    }
                });
                barChart.setVisibleXRangeMaximum(5);

                switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                    case Configuration.UI_MODE_NIGHT_YES:
                        x.setTextColor(Color.WHITE);
                        y.setTextColor(Color.WHITE);
                        barData.setValueTextColor(Color.WHITE);
                        break;
                    case Configuration.UI_MODE_NIGHT_NO:
                        x.setTextColor(Color.BLACK);
                        y.setTextColor(Color.BLACK);
                        barData.setValueTextColor(Color.BLACK);
                        break;
                }

                barData.notifyDataChanged();
                barChart.notifyDataSetChanged();

                barChart.invalidate();

            }
        };


    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.all:
                myViewmodel.getAllItems().observe(this, observer);
                myViewmodel.getAllItems().removeObservers(this);
                break;
            case R.id.most:
                myViewmodel.getAllItems().observe(this, new Observer<List<Item>>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onChanged(List<Item> items) {
                        resetChart();
                        ArrayList<BarEntry> entries = new ArrayList<>();
                        String[] savetext = new String[10];
                        Collections.sort(items, Comparator.comparing(Item::getStepAvg).reversed());
                        int j = 0;
                        for (int i = 0; i < 10; i++) {
                                entries.add(new BarEntry(j, items.get(i).getStepAvg()));
                                savetext[j] = items.get(i).getDisplayText();
                                j++;
                        }

                        BarDataSet barDataSet = new BarDataSet(entries,"");
                        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                        BarData barData = new BarData(barDataSet);
                        barChart.setData(barData);
                        barChart.getXAxis().setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getFormattedValue(float value) {
                                return savetext[(int) value];
                            }
                        });
                        barChart.setVisibleXRangeMaximum(5);
                        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                            case Configuration.UI_MODE_NIGHT_YES:
                                barChart.getXAxis().setTextColor(Color.WHITE);
                                barChart.getAxisLeft().setTextColor(Color.WHITE);
                                barData.setValueTextColor(Color.WHITE);
                                break;
                            case Configuration.UI_MODE_NIGHT_NO:
                                barChart.getXAxis().setTextColor(Color.BLACK);
                                barChart.getAxisLeft().setTextColor(Color.BLACK);
                                barData.setValueTextColor(Color.BLACK);
                                break;
                        }
                        barData.notifyDataChanged();
                        barChart.notifyDataSetChanged();

                        barChart.invalidate();
                    }
                });
                break;
            case R.id.favs:
                myViewmodel.getFavorites().observe(this, observer);
                myViewmodel.getFavorites().removeObservers(this);
                break;
            default:
                break;
        }

    }
    private void resetChart(){
        barChart.fitScreen();
        if (barChart.getData() != null)
            barChart.getData().clearValues();
        barChart.getXAxis().setValueFormatter(null);
        barChart.notifyDataSetChanged();
        barChart.clear();
        barChart.invalidate();

    }
}