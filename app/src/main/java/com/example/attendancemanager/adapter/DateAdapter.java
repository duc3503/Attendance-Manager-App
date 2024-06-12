package com.example.attendancemanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.attendancemanager.R;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DateAdapter extends BaseAdapter {
    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    public List<String> dateList;
    public ArrayList<String> arraylist;

    public DateAdapter(Context context, List<String> dateList) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        this.dateList = new LinkedList<String>();
        if (dateList != null)
            this.dateList = dateList;
        this.arraylist = new ArrayList<String>();
        if (this.dateList.size() > 0)
            this.arraylist.addAll(this.dateList);
    }
    @Override
    public int getCount() {
        return dateList.size();
    }

    @Override
    public String getItem(int position) {
        return dateList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        TextView date;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        Holder holder = new Holder();
        view = inflater.inflate(R.layout.item_date, null);
        // Locate the TextViews in listview_item.xml
        holder.date = (TextView) view.findViewById(R.id.date);

        // for text marquee
        holder.date.setSelected(true);

        holder.date.setText(dateList.get(position));
        return view;
    }

    // Filter Class
    public void filter(String charText) {
        dateList.clear();
        if (charText.length() == 0) {
            dateList.addAll(arraylist);
        } else {
            for (String date : arraylist) {
                if (date.equals(charText))
                    dateList.add(date);
            }
        }
        notifyDataSetChanged();
    }

    public void setArraylist(List<String> list) {
        arraylist.clear();
        arraylist.addAll(list);
    }

}
