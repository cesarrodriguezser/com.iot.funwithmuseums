package com.iot.funwithmuseums;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import android.os.Build;
import android.os.Bundle;

import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;

public class MQTTActivity extends AppCompatActivity {

    Button bHistory;
    Button bMySubscriptions;
    Button bNewSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity1_mqtt);

        bHistory = findViewById(R.id.buttonHistory);
        bMySubscriptions = findViewById(R.id.buttonMySubscriptions);
        bNewSubscription = findViewById(R.id.buttonNewSubscription);


        bHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent history = new Intent(MQTTActivity.this, History.class);
                startActivity(history);
            }
        });
        bMySubscriptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent favSub = new Intent(MQTTActivity.this, MySubscriptions.class);
                startActivity(favSub);
            }
        });
        bNewSubscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newSub = new Intent(MQTTActivity.this, NewSubscription.class);
                startActivity(newSub);
            }
        });
    }



}





