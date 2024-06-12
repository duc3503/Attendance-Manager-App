package com.example.attendancemanager.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.util.Pair;

import com.example.attendancemanager.R;
import com.example.attendancemanager.model.User;
import com.example.attendancemanager.model.User;
import com.example.attendancemanager.model.VNCharacterUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class UserAdapter extends BaseAdapter {
    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    private String filteredText = "";
    public List<Pair<User, Boolean>> userList;
    public ArrayList<Pair<User, Boolean>> arraylist;
    private Boolean isFiltered = false;

    public UserAdapter(Context context, List<Pair<User, Boolean>> userList) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        this.userList = new LinkedList<Pair<User, Boolean>>();
        if (userList != null) {
            this.userList = userList;
        }
        this.arraylist = new ArrayList<Pair<User, Boolean>>();
        if (this.userList.size() > 0)
            this.arraylist.addAll(this.userList);
    }
    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public User getItem(int position) {
        return userList.get(position).first;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        TextView mssv, name;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        UserAdapter.Holder holder = new UserAdapter.Holder();
        view = inflater.inflate(R.layout.item_user, null);
        // Locate the TextViews in listview_item.xml
        holder.mssv = (TextView) view.findViewById(R.id.mssv);
        holder.name = (TextView)view.findViewById(R.id.name);

        // for text marquee
        holder.name.setSelected(true);
        holder.mssv.setSelected(true);

        // Set the results into TextViews
        holder.name.setText(userList.get(position).first.getName());
        holder.mssv.setText(userList.get(position).first.getID());

        // Set background color of selected user
        if (userList.get(position).second == true) {
            view.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#90EE90")));
        } else {
            view.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#d3d4d3")));
        }

        // check if userList is filtered
        if (isFiltered) {
            // Remove accents and lowercase the city name for searching
            String modifiedName = VNCharacterUtils.removeAccent(userList.get(position).first.getName()).toLowerCase(Locale.getDefault());
            String modifiedMssv = VNCharacterUtils.removeAccent(userList.get(position).first.getID()).toLowerCase(Locale.getDefault());
            if (modifiedName.contains(filteredText)) {
                Spannable WordtoSpan = new SpannableString(userList.get(position).first.getName());
                WordtoSpan.setSpan(new ForegroundColorSpan(Color.RED), modifiedName.indexOf(filteredText),
                        modifiedName.indexOf(filteredText) + filteredText.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.name.setText(WordtoSpan);
            }
            if (modifiedMssv.contains(filteredText)) {
                Spannable WordtoSpan = new SpannableString(userList.get(position).first.getID());
                WordtoSpan.setSpan(new ForegroundColorSpan(Color.RED), modifiedMssv.indexOf(filteredText),
                        modifiedMssv.indexOf(filteredText) + filteredText.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.mssv.setText(WordtoSpan);
            }
        }

        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = VNCharacterUtils.removeAccent(charText).toLowerCase(Locale.getDefault());
        userList.clear();
        if (charText.length() == 0) {
            userList.addAll(arraylist);
            isFiltered = false;
        } else {
            for (Pair<User, Boolean> User : arraylist) {
                if (VNCharacterUtils.removeAccent(User.first.getID()).toLowerCase(Locale.getDefault()).contains(charText) ||
                        VNCharacterUtils.removeAccent(User.first.getName()).toLowerCase(Locale.getDefault()).contains(charText)) {
                    filteredText = charText;
                    userList.add(User);
                    isFiltered = true;
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setArraylist(List<Pair<User, Boolean>> list) {
        arraylist.clear();
        arraylist.addAll(list);
    }
}
