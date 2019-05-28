package com.cowsill.myreminders;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddEditReminder extends AppCompatActivity {

    // Create UI objects
    private EditText etName;
    private EditText etLocation;
    private EditText etMessage;
    private Button btnSave;
    private Button btnCancel;
    private Button btnDelete;

    private boolean mAddOrEdit; // Add = true, edit = false
    private ArrayList<MyReminder> mReminderList;
    private int mIndex;

    Intent backToMainActivity;

    GeofencingClient mGeofencingClient;
    ArrayList<Geofence> mGeofenceList;
    PendingIntent mGeofencePendingIntent;

    private static final String TAG = "AddEditReminder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_reminder);

        // Instantiate UI
        etName = findViewById(R.id.etName);
        etLocation = findViewById(R.id.etLocation);
        etMessage = findViewById(R.id.etMessage);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        btnDelete = findViewById(R.id.btnDeleteReminder);

        // Get information from intent
        mAddOrEdit = getIntent().getBooleanExtra(Constants.ADD_OR_EDIT_EXTRA, false);
        mReminderList = getIntent().getParcelableArrayListExtra(Constants.LIST_EXTRA);

        // If we are editing the reminder, get the object from the array and populate
        // the editTexts
        if (mAddOrEdit == false) {
            mIndex = getIntent().getIntExtra(Constants.INDEX_EXTRA, 0);
            MyReminder reminder = mReminderList.get(mIndex);

            etName.setText(reminder.getName());
            etLocation.setText(reminder.getLocation());
            etMessage.setText(reminder.getMessage());

            etLocation.setEnabled(false);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveReminder();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteReminder();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToMain();
            }
        });
    }

    private void deleteReminder() {

        mReminderList.remove(mIndex);
        returnToMain();
    }

    private void saveReminder() {

        // If all the fields are filled in
        if (!(etName.getText().toString().isEmpty() ||
                etLocation.getText().toString().isEmpty() ||
                etMessage.getText().toString().isEmpty())) {

            String name = etName.getText().toString();
            String location = etLocation.getText().toString();
            String message = etMessage.getText().toString();

            // Use AsyncTask to check validity of address with geocoder/conver to coordinates.  If valid, run this code:
            GeocoderTask geocoderTask = new GeocoderTask();
            geocoderTask.execute(name, location, message);


        } else {
            Toast.makeText(
                    this,
                    "Sorry, there must be a name, location and message for the reminder.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void returnToMain() {

        backToMainActivity = new Intent();
        backToMainActivity.putParcelableArrayListExtra(Constants.LIST_EXTRA, mReminderList);
        setResult(RESULT_OK, backToMainActivity);
        finish();
    }

    // AsyncTask will check if reminder location is valid and if so, create new reminder and add to list
    // Finally, it will display result to user via Toast message
    private class GeocoderTask extends AsyncTask<String, Void, Void> {

        List<Address> addressList;
        boolean mIsValidAddress;

        @Override
        protected Void doInBackground(String... strings) {

            String name = strings[0];
            String location = strings[1];
            String message = strings[2];

            Geocoder geocoder = new Geocoder(getApplicationContext());
            MyReminder reminder;

            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addressList.size() == 0 || addressList == null) {
                mIsValidAddress = false;
            } else {

                double latitude = addressList.get(0).getLatitude();
                double longtitude = addressList.get(0).getLongitude();
                mIsValidAddress = true;
                reminder = new MyReminder(name, location, latitude, longtitude, message);

                if (mAddOrEdit) {
                    mReminderList.add(reminder);
                } else {
                    mReminderList.remove(mIndex);
                    mReminderList.add(mIndex, reminder);
                }
                Log.i(TAG, "doInBackground: " + reminder.getName() + ", " + reminder.getLocation() + ", " +
                        reminder.getGeofenceLatitude() + ", " + reminder.getGeofenceLongtitude() + ", " +
                        reminder.getMessage());
                returnToMain();


            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (mIsValidAddress) {
                makeToast("Reminder successfully added/updated");
                GeofenceManager geofenceManager = new GeofenceManager(getApplicationContext(), mReminderList);
                geofenceManager.createGeofences();
            } else {
                makeToast("Sorry - the address provided is not valid");
            }
        }

        private void makeToast(String s) {

            Toast.makeText(
                    getApplicationContext(),
                    s,
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

}
