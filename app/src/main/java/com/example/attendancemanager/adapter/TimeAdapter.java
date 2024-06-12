package com.example.attendancemanager.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.attendancemanager.R;

import java.util.List;

public class TimeAdapter extends BaseAdapter {
    Context context;
    List<String> timeList;
    LayoutInflater inflater;

    public TimeAdapter(Context applicationContext, List<String> timeList) {
        this.context = applicationContext;
        this.timeList = timeList;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return timeList.size();
    }

    @Override
    public Object getItem(int i) {
        return timeList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public class Holder {
        TextView date;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        TimeAdapter.Holder holder = new TimeAdapter.Holder();
        view = inflater.inflate(R.layout.item_date, null);
        // Locate the TextViews in listview_item.xml
        holder.date = view.findViewById(R.id.date);


        if (position%2 != 0) {
            holder.date.setText(timeList.get(position) + " (OUT)");
            holder.date.setTextColor(Color.RED);
        } else {
            holder.date.setText(timeList.get(position) + " (IN)");
            holder.date.setTextColor(Color.parseColor("#00ab41"));
        }

        return view;
    }

}
