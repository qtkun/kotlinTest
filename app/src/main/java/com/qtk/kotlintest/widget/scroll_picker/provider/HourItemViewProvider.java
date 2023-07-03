package com.qtk.kotlintest.widget.scroll_picker.provider;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.qtk.kotlintest.R;
import com.qtk.kotlintest.widget.scroll_picker.IViewProvider;

public class HourItemViewProvider<T extends String> implements IViewProvider<T> {
    @Override
    public int resLayout() {
        return R.layout.scroll_picker_hour_item_layout;
    }

    @Override
    public void onBindView(@NonNull View view, @Nullable T text) {
        TextView tv = view.findViewById(R.id.tv_content);
        tv.setText(text);
        view.setTag(text);
        tv.setTextSize(14);
    }

    @Override
    public void updateView(@NonNull View itemView, boolean isSelected) {
        TextView tv = itemView.findViewById(R.id.tv_content);
        tv.setTextSize(isSelected ? 16 : 14);
        tv.setTextColor(Color.parseColor(isSelected ? "#363C5A" : "#A2A5B3"));
        tv.setTypeface(isSelected ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
    }
}
