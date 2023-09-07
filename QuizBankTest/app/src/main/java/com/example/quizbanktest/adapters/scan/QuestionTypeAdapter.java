package com.example.quizbanktest.adapters.scan;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quizbanktest.R;
import com.example.quizbanktest.activity.scan.QuestionTypeItem;

import java.util.ArrayList;

// Adapter class for spinner control
public class QuestionTypeAdapter extends ArrayAdapter<QuestionTypeItem> {
    private Context context;
    private int spos=0;
    public QuestionTypeAdapter(Context context, ArrayList<QuestionTypeItem> QuestionTypeItemList) {
        super(context, 0, QuestionTypeItemList);
        this.context=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        spos = position;
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(getContext()).inflate(
                R.layout.myspinner, parent, false);
        ImageView imageViewFlag = convertView.findViewById(R.id.image);
        TextView textViewName = convertView.findViewById(R.id.text1);
        QuestionTypeItem currentItem = getItem(position);
        if (currentItem != null) {
            imageViewFlag.setImageResource(currentItem.getQuestionTypeImage());
            textViewName.setText(currentItem.getQuestionTypeName());
            if (position == spos)
            {textViewName.setTextColor  (Color.argb(255, 42, 77, 105));}
        }
        return convertView;
    }
}