package com.example.maria.remindmewhere.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.maria.remindmewhere.R;
import com.example.maria.remindmewhere.model.Reminder;

import java.util.ArrayList;

/**
 * Created by maria on 4/2/17.
 */

public class ReminderAdapter extends ArrayAdapter<Reminder> {
    public ReminderAdapter(Context context, ArrayList<Reminder> reminders) {
        super(context, 0, reminders);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Reminder reminder = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_rem_row, parent, false);
        }
        TextView remName = (TextView) convertView.findViewById(R.id.lla_reminders_list_item);
        remName.setText(reminder.getName());

        // Return the completed view to render on screen
        return convertView;
    }
}
