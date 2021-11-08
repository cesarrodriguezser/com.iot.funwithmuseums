package com.iot.funwithmuseums;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Build;
import android.os.Bundle;
import android.widget.RadioGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.iot.funwithmuseums.database.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChartActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{

    BarChart barChart;
    BarData data;
    BarDataSet dataSet;
    RadioGroup radioGroup;
    private static final List<Item> listOfItems = new ArrayList<>();
    ViewModel myViewmodel;
    Observer<List<Item>> observer;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        barChart = findViewById(R.id.barChart);
        radioGroup = findViewById(R.id.radioshow);


        ArrayList<BarEntry> entries = new ArrayList<>();


        myViewmodel = ViewModelProviders.of(this).get(ViewModel.class);


        observer = new Observer<List<Item>>() {
            @Override
            public void onChanged(List<Item> items) {
                for (int i = 0; i < items.size(); i++) {
                    entries.add(new BarEntry(i, items.get(i).getStepAvg()));
                }
                BarDataSet barDataSet = new BarDataSet(entries, "All museum Steps");
                barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                BarData barData = new BarData(barDataSet);
                barChart.setData(barData);
                XAxis x = barChart.getXAxis();

                x.setPosition(XAxis.XAxisPosition.BOTTOM);
                x.setGranularity(1.0f);
                x.setLabelCount(5);
                x.setLabelRotationAngle(20);
                barChart.getAxisRight().setEnabled(false);
                barChart.setVisibleXRangeMaximum(3);
                barChart.getAxisLeft().setAxisMinimum(0);

                x.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return String.valueOf(items.get((int) value).getId()); // here you can map your values or pass it as empty string
                    }
                });
                barChart.invalidate();

            }
        };


    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.all:
                myViewmodel.getAllItems().observe(this, observer);
                break;
            case R.id.most:
                break;
            case R.id.favs:
                myViewmodel.getFavorites().observe(this, observer);
                break;
            default:
                break;
        }

    }
}