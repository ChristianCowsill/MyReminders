package com.cowsill.myreminders;

import android.content.Intent;
import android.support.annotation.Nullable;
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

        // Instantiate UI
        lvReminderList = findViewById(R.id.ReminderList);
        btnAddReminder = findViewById(R.id.btnAddReminder);

        mReminderList = new ArrayList<>();
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

}
