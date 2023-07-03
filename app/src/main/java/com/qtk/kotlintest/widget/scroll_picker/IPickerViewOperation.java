package com.qtk.kotlintest.widget.scroll_picker;

import android.view.View;

public interface IPickerViewOperation {
    int getSelectedItemOffset();

    int getSelectedItemPosition();

    int getVisibleItemNumber();

    int getLineColor();

    void updateView(View itemView, boolean isSelected);
}
