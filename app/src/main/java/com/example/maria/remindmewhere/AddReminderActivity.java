package com.example.maria.remindmewhere;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.maria.remindmewhere.database.DataSourceReminder;
import com.example.maria.remindmewhere.model.Location;
import com.example.maria.remindmewhere.model.Reminder;
import com.example.maria.remindmewhere.utils.Constants;

public class AddReminderActivity extends AppCompatActivity {

    public static final String ACTIVITY_NAME_TAG = "AddReminderActivity";

    private DataSourceReminder dsReminder;
    private Reminder mReminder;
    private Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        mReminder = null;
        mLocation = null;

        dsReminder = new DataSourceReminder(this);
        dsReminder.open();

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            if(extras.containsKey(Constants.REMINDER_KEY)) {
                mReminder = (Reminder) extras.get(Constants.REMINDER_KEY);
                Log.d(ACTIVITY_NAME_TAG,mReminder.toString());
            }
            mLocation = (Location) extras.get(Constants.LOCATION_KEY);
            Log.d(ACTIVITY_NAME_TAG,mLocation.toString());
        }

        if(mReminder != null) {
            fillFormData();
        }
    }

    public void saveReminder (View view){
        if(mLocation != null){
            EditText nameEditText = (EditText) findViewById(R.id.ara_name_input);
            String name = nameEditText.getText().toString();
            EditText descEditText = (EditText) findViewById(R.id.ara_description_input);
            String description = descEditText.getText().toString();

            if(TextUtils.isEmpty(name)){
                Toast.makeText(this,Constants.NAME_ERROR_MSG,Toast.LENGTH_LONG).show();
            } else if(TextUtils.isEmpty(description)){
                Toast.makeText(this,Constants.DESCRIPTION_ERROR_MSG,Toast.LENGTH_LONG).show();
            } else if(mReminder != null){
                //Editing version
                mReminder.setName(name);
                mReminder.setDescription(description);
                mReminder = dsReminder.update(mReminder);
                Intent result = new Intent();
                result.putExtra(Constants.LOCATION_KEY,mLocation);
                setResult(RESULT_OK,result);
                finish();
            }else {
                Reminder newReminder = new Reminder(null,description,name,mLocation.getId());
                newReminder = dsReminder.create(newReminder);
                Log.d(ACTIVITY_NAME_TAG, newReminder.toString());
                Intent result = new Intent();
                result.putExtra(Constants.LOCATION_KEY,mLocation);
                setResult(RESULT_OK,result);
                finish();
            }
        }else{
            Toast.makeText(this,Constants.SAVE_REMINDER_ERROR_MSG,Toast.LENGTH_LONG).show();
        }
    }

    public void deleteReminder (View view){
        if(mReminder != null){
            dsReminder.delete(mReminder);
            Intent result = new Intent();
            result.putExtra(Constants.LOCATION_KEY,mLocation);
            setResult(RESULT_OK,result);
            finish();
        }else {
            Toast.makeText(this,Constants.DELETE_REMINDER_ERROR_MSG,Toast.LENGTH_LONG).show();
        }
    }

    private void fillFormData() {
        if(mReminder != null){
            EditText nameEditText = (EditText) findViewById(R.id.ara_name_input);
            EditText descEditText = (EditText) findViewById(R.id.ara_description_input);

            nameEditText.setText(mReminder.getName());
            descEditText.setText(mReminder.getDescription());
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
