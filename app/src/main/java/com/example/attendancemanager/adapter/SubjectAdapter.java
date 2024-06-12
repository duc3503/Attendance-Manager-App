package com.example.attendancemanager.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.attendancemanager.R;
import com.example.attendancemanager.model.Subject;
import com.example.attendancemanager.model.VNCharacterUtils;
import com.google.firebase.database.DatabaseReference;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class SubjectAdapter extends BaseAdapter {
    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    public List<Subject> subjectList;
    public ArrayList<Subject> arraylist;
    private String filteredText = "";
    private Boolean isFiltered = false;

    public SubjectAdapter(Context context, List<Subject> subjectList) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        this.subjectList = subjectList;
        arraylist = new ArrayList<Subject>();
        arraylist.addAll(subjectList);
    }
    @Override
    public int getCount() {
        return subjectList.size();
    }

    @Override
    public Subject getItem(int position) {
        return subjectList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        TextView time, classroom, name, id, participant;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        SubjectAdapter.Holder holder = new SubjectAdapter.Holder();
        view = inflater.inflate(R.layout.item_subject, null);
        // Locate the TextViews in listview_item.xml
        holder.time = (TextView) view.findViewById(R.id.time);
        holder.name = (TextView)view.findViewById(R.id.name);
        holder.classroom = (TextView)view.findViewById(R.id.classroom);
        holder.id = (TextView)view.findViewById(R.id.id);
        holder.participant = (TextView)view.findViewById(R.id.participant);

        // for text marquee
        holder.time.setSelected(true);
        holder.name.setSelected(true);
        holder.classroom.setSelected(true);
        holder.id.setSelected(true);

        holder.time.setText(subjectList.get(position).getStartTime() + " - " + subjectList.get(position).getEndTime());
        holder.classroom.setText(subjectList.get(position).getRoomID());
        holder.name.setText(subjectList.get(position).getName());
        holder.id.setText(subjectList.get(position).getClassID());
        holder.participant.setText(subjectList.get(position).getNum_of_teachers() + " teachers, " + subjectList.get(position).getNum_of_students() + " students");

        // check if subjectList is filtered
        if (isFiltered) {
            String modifiedName = VNCharacterUtils.removeAccent(subjectList.get(position).getName()).toLowerCase(Locale.getDefault());
            String modifiedId = VNCharacterUtils.removeAccent(subjectList.get(position).getClassID()).toLowerCase(Locale.getDefault());
            if (modifiedName.contains(filteredText)) {
                Spannable WordtoSpan = new SpannableString(subjectList.get(position).getName());
                WordtoSpan.setSpan(new ForegroundColorSpan(Color.RED), modifiedName.indexOf(filteredText),
                        modifiedName.indexOf(filteredText) + filteredText.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.name.setText(WordtoSpan);
            }
            if (modifiedId.contains(filteredText)) {
                Spannable WordtoSpan = new SpannableString(subjectList.get(position).getClassID());
                WordtoSpan.setSpan(new ForegroundColorSpan(Color.RED), modifiedId.indexOf(filteredText),
                        modifiedId.indexOf(filteredText) + filteredText.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.id.setText(WordtoSpan);
            }
        }

        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = VNCharacterUtils.removeAccent(charText).toLowerCase(Locale.getDefault());
        subjectList.clear();
        if (charText.isEmpty()) {
            subjectList.addAll(arraylist);
            isFiltered = false;
        } else {
            for (Subject subject : arraylist) {
                if (VNCharacterUtils.removeAccent(subject.getClassID()).toLowerCase(Locale.getDefault()).contains(charText) ||
                        VNCharacterUtils.removeAccent(subject.getName()).toLowerCase(Locale.getDefault()).contains(charText)) {
                    filteredText = charText;
                    subjectList.add(subject);
                    isFiltered = true;
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setArraylist(List<Subject> list) {
        arraylist.clear();
        arraylist.addAll(list);
    }
}
