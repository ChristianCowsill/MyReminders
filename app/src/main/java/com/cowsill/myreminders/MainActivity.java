package com.cowsill.myreminders;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Create UI objects
    ListView lvReminderList;
    Button btnAddReminder;

    // List to hold reminders
    private ArrayList<MyReminder> mReminderList;
    ArrayAdapter<MyReminder> arrayAdapter;

    private boolean mAddOrEdit;  // if True, add.  Else, edit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check for location priviledges
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.LOCATION_REQUEST_CODE
            );
        }

        // Start service and begin getting location updates
        Intent startLocationMonitoring = new Intent(this, LocationMonitoringService.class);
        startService(startLocationMonitoring);

        // Instantiate UI
        lvReminderList = findViewById(R.id.ReminderList);
        btnAddReminder = findViewById(R.id.btnAddReminder);

        // Get list of existing reminders from SharedPreferences
        loadData();

        // Create adapter and bind list
         arrayAdapter = new ArrayAdapter<MyReminder>(
                this,
                android.R.layout.simple_list_item_1,
                mReminderList
        );

        lvReminderList.setAdapter(arrayAdapter);

        // Create long click view listener on list;  opens EDIT, REMOVE, CANCEL dialog
        lvReminderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               launchEditOrRemoveReminderActivity(position);
            }
        });

        btnAddReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addReminder();
            }
        });
    }

    private void launchEditOrRemoveReminderActivity(int position) {

        mAddOrEdit = false;

        Intent launchActivity = new Intent(this, AddEditReminder.class);
        launchActivity.putExtra(Constants.ADD_OR_EDIT_EXTRA, mAddOrEdit);
        launchActivity.putParcelableArrayListExtra(Constants.LIST_EXTRA, mReminderList);
        launchActivity.putExtra(Constants.INDEX_EXTRA, position);

        startActivityForResult(launchActivity, Constants.START_ACTIVITY_REQUEST_CODE);

    }


    private void addReminder() {

        mAddOrEdit = true;

        Intent startNewActivity = new Intent(
                this,
                AddEditReminder.class
        );

        startNewActivity.putExtra(Constants.ADD_OR_EDIT_EXTRA, mAddOrEdit);
        startNewActivity.putParcelableArrayListExtra(Constants.LIST_EXTRA, mReminderList);
        startActivityForResult(startNewActivity, Constants.START_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == Constants.START_ACTIVITY_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                mReminderList = data.getParcelableArrayListExtra(Constants.LIST_EXTRA);
                arrayAdapter.clear();
                arrayAdapter.addAll(mReminderList);
                arrayAdapter.notifyDataSetChanged();

            }
        }
    }

    private void saveData(){

        SharedPreferences sharedPreferences = getSharedPreferences(
                Constants.SHARED_PREFERENCES_NAME,
                Context.MODE_PRIVATE
        );

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mReminderList);
        editor.putString(Constants.JSON_LIST_KEY, json);
        editor.apply();
    }

    private void loadData(){

        SharedPreferences sharedPreferences = getSharedPreferences(
                Constants.SHARED_PREFERENCES_NAME,
                Context.MODE_PRIVATE
        );
        Gson gson = new Gson();
        String json = sharedPreferences.getString(Constants.JSON_LIST_KEY, null);
        Type type = new TypeToken<ArrayList<MyReminder>>() {}.getType();
        mReminderList = gson.fromJson(json, type);

        if(mReminderList == null){
            mReminderList = new ArrayList<>();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveData();
    }
}
