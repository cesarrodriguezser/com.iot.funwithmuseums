package com.iot.funwithmuseums;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bendaschel.sevensegmentview.SevenSegmentView;
import com.iot.funwithmuseums.database.ViewModel;

public class VisitActivity extends AppCompatActivity implements SensorEventListener {

    TextView tvName;
    TextView tvSteps;
    Button bStart;
    Button bEnd;
    Intent inputIntent;
    private SensorManager sensorManager;
    private Sensor stepSensor;
    int steps = 0;
    private String Mname;
    ViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit);


        viewModel = ViewModelProviders.of(this).get(ViewModel.class);

        tvName = findViewById(R.id.textView);
        bStart = findViewById(R.id.bStart);
        bEnd = findViewById(R.id.bEnd);
        bEnd.setEnabled(false);

        //Getting the Intent
        inputIntent = getIntent();

        //Getting the Values coming from First Activity extracting them from the Intent received
        Mname = inputIntent.getStringExtra("museumName");

        // Get the reference to the sensor manager and the sensor:
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        tvName.setText(Mname);
    }

    public void StartVisit(View view){

        bStart.setEnabled(false);
        bEnd.setEnabled(true);

        sensorManager.registerListener(VisitActivity.this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    private void updateSegmentDisplay(int steps) {
        final int[] segmentIds = {R.id.display0, R.id.display1, R.id.display2, R.id.display3, R.id.display4};
        int pow10 = 1;

        for (int id : segmentIds) {
            SevenSegmentView segment = findViewById(id);

            int digit = (steps % (pow10 * 10)) / pow10;
            segment.setCurrentValue(digit);
            pow10 *= 10;
        }
    }

    public void EndVisit(View view){

        sensorManager.unregisterListener(VisitActivity.this, stepSensor);

        LiveData<Item> liveData = viewModel.getByName(Mname);
        liveData.observe(this, new Observer<Item>() {
            @Override
            public void onChanged(Item item) {
                item.setStepAvg(item.getStepAvg() == 0? steps:((item.getStepAvg() + steps)/2));
                viewModel.UpdateItem(item);
                liveData.removeObservers(VisitActivity.this);
                finish();
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if(sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR){
            //STEP SENSOR
            steps++;
            updateSegmentDisplay(steps);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}