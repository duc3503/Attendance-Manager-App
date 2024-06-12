package com.example.attendancemanager.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.attendancemanager.R;
import com.example.attendancemanager.model.Attendance;
import com.example.attendancemanager.model.Subject;
import com.example.attendancemanager.model.VNCharacterUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AttendanceAdapter extends BaseAdapter {
    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    public List<Attendance> attendanceList;
    public ArrayList<Attendance> arraylist;
    private String filteredText = "";
    private Boolean isFiltered = false;

    public AttendanceAdapter(Context context, List<Attendance> attendanceList) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        this.attendanceList = attendanceList;
        arraylist = new ArrayList<Attendance>();
        arraylist.addAll(attendanceList);
    }
    @Override
    public int getCount() {
        return attendanceList.size();
    }

    @Override
    public Attendance getItem(int position) {
        return attendanceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        TextView mssv, name, time, description;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        AttendanceAdapter.Holder holder = new AttendanceAdapter.Holder();
        view = inflater.inflate(R.layout.item_attendance, null);
        // Locate the TextViews in listview_item.xml
        holder.mssv = view.findViewById(R.id.mssv);
        holder.name = view.findViewById(R.id.name);
        holder.time = view.findViewById(R.id.time);
        holder.description = view.findViewById(R.id.description);

        // for text marquee
        holder.time.setSelected(true);
        holder.name.setSelected(true);


        holder.mssv.setText(attendanceList.get(position).getId());
        holder.name.setText(attendanceList.get(position).getName());
        holder.time.setText("Enter/Exit: " + attendanceList.get(position).getNumOfTime().toString());
        if (attendanceList.get(position).getNumOfTime() <= 0) {
            holder.description.setText("Absent");
            holder.description.setTextColor(Color.RED);
        } else if (attendanceList.get(position).getNumOfTime() % 2 == 0) {
            holder.description.setText("Participated");
            holder.description.setTextColor(Color.parseColor("#00ab41"));
        } else {
            holder.description.setText("Participating");
            holder.description.setTextColor(Color.parseColor("#00ab41"));
        }

        // check if attendanceList is filtered
        if (isFiltered) {
            String modifiedName = VNCharacterUtils.removeAccent(attendanceList.get(position).getName()).toLowerCase(Locale.getDefault());
            String modifiedId = VNCharacterUtils.removeAccent(attendanceList.get(position).getId().toLowerCase(Locale.getDefault()));
            if (modifiedName.contains(filteredText)) {
                Spannable WordtoSpan = new SpannableString(attendanceList.get(position).getName());
                WordtoSpan.setSpan(new ForegroundColorSpan(Color.RED), modifiedName.indexOf(filteredText),
                        modifiedName.indexOf(filteredText) + filteredText.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.name.setText(WordtoSpan);
            }
            if (modifiedId.contains(filteredText)) {
                Spannable WordtoSpan = new SpannableString(attendanceList.get(position).getId());
                WordtoSpan.setSpan(new ForegroundColorSpan(Color.RED), modifiedId.indexOf(filteredText),
                        modifiedId.indexOf(filteredText) + filteredText.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.mssv.setText(WordtoSpan);
            }
        }

        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = VNCharacterUtils.removeAccent(charText).toLowerCase(Locale.getDefault());
        attendanceList.clear();
        if (charText.isEmpty()) {
            attendanceList.addAll(arraylist);
            isFiltered = false;
        } else {
            for (Attendance attendance : arraylist) {
                if (VNCharacterUtils.removeAccent(attendance.getId()).toLowerCase(Locale.getDefault()).contains(charText) ||
                        VNCharacterUtils.removeAccent(attendance.getName()).toLowerCase(Locale.getDefault()).contains(charText)) {
                    filteredText = charText;
                    attendanceList.add(attendance);
                    isFiltered = true;
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setArraylist(List<Attendance> list) {
        arraylist.clear();
        arraylist.addAll(list);
    }
}
