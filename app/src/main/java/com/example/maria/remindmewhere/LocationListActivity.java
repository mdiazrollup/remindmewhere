package com.example.maria.remindmewhere;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maria.remindmewhere.adapter.ReminderAdapter;
import com.example.maria.remindmewhere.database.DataSourceReminder;
import com.example.maria.remindmewhere.model.Location;
import com.example.maria.remindmewhere.model.Reminder;
import com.example.maria.remindmewhere.utils.Constants;

import java.util.ArrayList;

public class LocationListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public static final String ACTIVITY_NAME_TAG = "LocationListActivity";
    public static final int REQUEST_CODE_ADD_REMINDER = 20;
    public static final int REQUEST_CODE_ADD_REMINDER_LIST = 21;

    private DataSourceReminder dsReminder;
    private ListView remindersListView;
    private ArrayList<Reminder> reminders;
    private Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);

        mLocation = null;
        dsReminder = new DataSourceReminder(this);
        dsReminder.open();

        getExtraData(getIntent());

        initRemindersList();
    }

    private void getExtraData(Intent intent){
        Bundle extras = intent.getExtras();
        if(extras!=null){
            mLocation = (Location) extras.get(Constants.LOCATION_KEY);
            Log.d(ACTIVITY_NAME_TAG,mLocation.toString());
        }
    }

    private void initRemindersList() {
        if(mLocation != null){
            remindersListView = (ListView)findViewById(R.id.lla_list_view_rem);
            reminders = dsReminder.getRemindersByLocation(mLocation.getId());
            //adapter which will convert each data item into view item.
            ReminderAdapter adapter = new ReminderAdapter(this, reminders);
            //place each view-item inside listview by setting adapter for our listview
            remindersListView.setAdapter(adapter);

            remindersListView.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Reminder mReminder = reminders.get(position);

        Intent intent = new Intent(LocationListActivity.this, AddReminderActivity.class);
        intent.putExtra(Constants.REMINDER_KEY,mReminder);
        intent.putExtra(Constants.LOCATION_KEY,mLocation);
        startActivityForResult(intent,REQUEST_CODE_ADD_REMINDER_LIST);
    }

    public void goToAddReminder(View view){
        Intent intent = new Intent(LocationListActivity.this, AddReminderActivity.class);
        intent.putExtra(Constants.LOCATION_KEY,mLocation);
        startActivityForResult(intent,REQUEST_CODE_ADD_REMINDER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        dsReminder.open();
        if(requestCode == REQUEST_CODE_ADD_REMINDER && resultCode == RESULT_OK){
            getExtraData(data);
            initRemindersList();
        } else if(requestCode == REQUEST_CODE_ADD_REMINDER_LIST && resultCode == RESULT_OK){
            getExtraData(data);
            initRemindersList();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        dsReminder.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dsReminder.open();
    }
}
