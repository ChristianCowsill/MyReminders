package com.cowsill.myreminders;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class AddEditReminder extends AppCompatActivity {

    // Create UI objects
    private EditText etName;
    private EditText etLocation;
    private EditText etMessage;
    private Button btnSave;
    private Button btnCancel;

    private boolean mAddOrEdit; // Add = true, edit = false
    private ArrayList<MyReminder> mReminderList;
    private int index;

    Intent backToMainActivity;

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

        // Get information from intent
        mAddOrEdit = getIntent().getBooleanExtra(Constants.ADD_OR_EDIT_EXTRA, false);
        mReminderList = getIntent().getParcelableArrayListExtra(Constants.LIST_EXTRA);

        // If we are editing the reminder, get the object from the array and populate
        // the editTexts
        if(mAddOrEdit == false){
            index = getIntent().getIntExtra(Constants.INDEX_EXTRA, 0);
            MyReminder reminder = mReminderList.get(index);

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

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToMain();
            }
        });
    }

    private void saveReminder() {

        // If all the fields are filled in
        if(!(etName.getText().toString().isEmpty() ||
                etLocation.getText().toString().isEmpty() ||
                etMessage.getText().toString().isEmpty())){

            String name = etName.getText().toString();
            String location = etLocation.getText().toString();
            String message = etMessage.getText().toString();

            MyReminder reminder = new MyReminder(name, location, message);
            mReminderList.add(reminder);

            returnToMain();

        } else {
            Toast.makeText(
                    this,
                    "Sorry, you must enter a name, location and message for the reminder.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void returnToMain(){

        backToMainActivity = new Intent();
        backToMainActivity.putParcelableArrayListExtra(Constants.LIST_EXTRA, mReminderList);
        setResult(RESULT_OK, backToMainActivity);
        finish();
    }
}
