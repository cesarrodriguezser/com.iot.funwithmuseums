package com.iot.funwithmuseums;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iot.funwithmuseums.database.ViewModel;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class History extends AppCompatActivity implements ItemViewHolder.ItemClickListener{

    final String serverUri = "tcp://broker.hivemq.com:1883";

    private RecyclerView myRecycleView2;

    private HistoryAdapter myAdapter2;

    private ArrayList<String> listOfItems2 = new ArrayList<>();
    ViewModel myViewmodel;
    Context context;
    Button bDeleteHistory;

    MqttAndroidClient mqttAndroidClient;
    String clientId = "ExampleAndroidClient";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);

        myViewmodel = ViewModelProviders.of(this).get(ViewModel.class);
        myRecycleView2 = findViewById(R.id.history_recycler_view);
        bDeleteHistory = findViewById(R.id.bDeleteHistory);
        loadData();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        myRecycleView2.setLayoutManager(linearLayoutManager);

        myAdapter2 = new HistoryAdapter(listOfItems2, History.this);
        myRecycleView2.setAdapter(myAdapter2);

        clientId = clientId + System.currentTimeMillis();

        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), serverUri, clientId);

        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

                if (reconnect) {
                    Snackbar.make(findViewById(android.R.id.content), "Reconnected to server", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    addToHistory("Connected to: " + serverURI);

                    listOfItems2.add(new String("Reconnected to server"));
                    myAdapter2.notifyItemInserted(listOfItems2.size());
                } else {
                    addToHistory("Connected to: " + serverURI);
                    Snackbar.make(findViewById(android.R.id.content), "Connected to server", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                    saveData();
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                addToHistory("The Connection was lost.");
                Snackbar.make(findViewById(android.R.id.content), "The Connection was lost.", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                saveData();

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {

                Date date = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");

                Snackbar.make(findViewById(android.R.id.content), "Incoming message: " + new String(message.getPayload()) + "\n" + formatter.format(date), Snackbar.LENGTH_LONG).setAction("Action", null).show();

                addToHistory("Incoming message: " + new String(message.getPayload()) + "from: " +  "\n" + formatter.format(date));
                // below line is use to add data to array list.
                listOfItems2.add("Incoming message: " + new String(message.getPayload()) +"\n" + "From: "+ topic + "\n" + "Date: " +formatter.format(date));
                // notifying adapter when new data added.
                myAdapter2.notifyItemInserted(listOfItems2.size());
                saveData();

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
        bDeleteHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listOfItems2.clear();
                myAdapter2.notifyDataSetChanged();
            }
        });
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(true);
        byte[] payload = "Client disconnected".getBytes();

        //to obtain the messages when it enters in the activuty -> qos must be 1
        mqttConnectOptions.setCleanSession(true);



        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                   addToHistory("Failed to connect to: " + serverUri +
                            ". Cause: " + ((exception.getCause() == null) ?
                            exception.toString() : exception.getCause()));
                    Snackbar.make(findViewById(android.R.id.content), "The Connection was lost." , Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    listOfItems2.add("The Connection was lost.");
                    // notifying adapter when new data added.
                    myAdapter2.notifyItemInserted(listOfItems2.size());
                }

            });
        } catch (MqttException e) {
            e.printStackTrace();
            addToHistory(e.toString());
        }
    }

    private void addToHistory(String mainText) {
         System.out.println("LOG: " + mainText);
      }
    private void loadData() {
        // method to load arraylist from shared prefs
        // initializing our shared prefs with name as
        // shared preferences.
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);

        // creating a variable for gson.
        Gson gson = new Gson();

        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        String json = sharedPreferences.getString("courses", null);

        // below line is to get the type of our array list.
        Type type = new TypeToken<ArrayList<String>>() {}.getType();

        // in below line we are getting data from gson8
        // and saving it to our array list
         listOfItems2= gson.fromJson(json, type);

        // checking below if the array list is empty or not
        if (listOfItems2 == null) {
            // if the array list is empty
            // creating a new array list.
            listOfItems2 = new ArrayList<String>();
        }
    }
    private void saveData(){
        // method for saving the data in array list.
        // creating a variable for storing data in
        // shared preferences.
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);

        // creating a variable for editor to
        // store data in shared preferences.
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // creating a new variable for gson.
        Gson gson = new Gson();

        // getting data from gson and storing it in a string.
        String json = gson.toJson(listOfItems2);

        // below line is to save data in shared
        // prefs in the form of string.
        editor.putString("courses", json);

        // below line is to apply changes
        // and save data in shared prefs.
        editor.apply();

        // after saving data we are displaying a toast message.
        //Toast.makeText(this, "Saved Array List to Shared preferences. ", Toast.LENGTH_SHORT).show();

    }
    public void subscribeToTopic() {
        myViewmodel.getFavorites().observe(this, new Observer<List<Item>>() {
            public void onChanged(List<Item> items) {
                for (int i = 0; i < items.size(); i++) {
                    String subscriptionTopic=items.get(i).getTopic();
                    try {
                        mqttAndroidClient.subscribe(subscriptionTopic, 1, null, new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                addToHistory("Subscribed to: " + subscriptionTopic);
                                saveData();
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                //addToHistory("Failed to subscribe");
                                Snackbar.make(findViewById(android.R.id.content), "Failed to subscribe", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                                saveData();
                            }
                        });
                    }catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    public void deleteHistory(){
    }



    public void onItemClick(int position, View v) {
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

}




