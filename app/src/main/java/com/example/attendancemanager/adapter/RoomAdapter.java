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
import com.example.attendancemanager.model.Room;
import com.example.attendancemanager.model.Subject;
import com.example.attendancemanager.model.VNCharacterUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RoomAdapter extends BaseAdapter {
    Context context;
    List<Room> roomList;
    LayoutInflater inflater;
    ArrayList<Room> arraylist;
    private String filteredText = "";
    private Boolean isFiltered = false;

    public RoomAdapter(Context applicationContext, List<Room> roomList) {
        this.context = applicationContext;
        this.roomList = roomList;
        inflater = (LayoutInflater.from(applicationContext));
        arraylist = new ArrayList<Room>();
        arraylist.addAll(roomList);
    }

    @Override
    public int getCount() {
        return roomList.size();
    }

    @Override
    public Object getItem(int i) {
        return roomList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public class Holder {
        TextView roomID;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        RoomAdapter.Holder holder = new RoomAdapter.Holder();
        view = inflater.inflate(R.layout.item_date, null);
        // Locate the TextViews in listview_item.xml
        holder.roomID = view.findViewById(R.id.date);

        // check if subjectList is filtered
        if (isFiltered) {
            String modifiedId = VNCharacterUtils.removeAccent(roomList.get(position).getRoomID()).toLowerCase(Locale.getDefault());
            if (modifiedId.contains(filteredText)) {
                Spannable WordtoSpan = new SpannableString(roomList.get(position).getRoomID());
                WordtoSpan.setSpan(new ForegroundColorSpan(Color.RED), modifiedId.indexOf(filteredText),
                        modifiedId.indexOf(filteredText) + filteredText.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.roomID.setText(WordtoSpan);
            }
        } else {
            holder.roomID.setText(roomList.get(position).getRoomID());
        }

        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = VNCharacterUtils.removeAccent(charText).toLowerCase(Locale.getDefault());
        roomList.clear();
        if (charText.isEmpty()) {
            roomList.addAll(arraylist);
            isFiltered = false;
        } else {
            for (Room room : arraylist) {
                if (VNCharacterUtils.removeAccent(room.getRoomID()).toLowerCase(Locale.getDefault()).contains(charText)) {
                    filteredText = charText;
                    roomList.add(room);
                    isFiltered = true;
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setArraylist(List<Room> list) {
        arraylist.clear();
        arraylist.addAll(list);
    }
}
