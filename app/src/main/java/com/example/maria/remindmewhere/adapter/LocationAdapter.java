package com.example.maria.remindmewhere.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.maria.remindmewhere.R;
import com.example.maria.remindmewhere.model.Location;

import java.util.ArrayList;

/**
 * Created by maria on 4/2/17.
 */

public class LocationAdapter extends ArrayAdapter<Location> {
    public LocationAdapter(Context context, ArrayList<Location> locations) {
        super(context, 0, locations);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Location location = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_loc_row, parent, false);
        }
        TextView locName = (TextView) convertView.findViewById(R.id.ha_locations_list_item);
        locName.setText(location.getName());

        // Return the completed view to render on screen
        return convertView;
    }
}
