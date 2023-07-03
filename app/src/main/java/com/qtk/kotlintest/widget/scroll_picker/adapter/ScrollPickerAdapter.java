package com.qtk.kotlintest.widget.scroll_picker.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qtk.kotlintest.widget.scroll_picker.IPickerViewOperation;
import com.qtk.kotlintest.widget.scroll_picker.IViewProvider;
import com.qtk.kotlintest.widget.scroll_picker.provider.DefaultItemViewProvider;

import java.util.ArrayList;
import java.util.List;

public class ScrollPickerAdapter<T> extends RecyclerView.Adapter<ScrollPickerAdapter.ScrollPickerAdapterHolder> implements IPickerViewOperation {
    private final List<T> mDataList;
    private final Context mContext;
    private OnClickListener mOnItemClickListener;
    private OnScrollListener mOnScrollListener;
    private int mSelectedItemOffset;
    private int mSelectedItemPosition;
    private int mVisibleItemNum = 3;
    private IViewProvider<T> mViewProvider;
    private int mLineColor;
    private int maxItemH = 0;
    private int maxItemW = 0;

    private ScrollPickerAdapter(Context context) {
        mContext = context;
        mDataList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ScrollPickerAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mViewProvider == null) {
            mViewProvider = new DefaultItemViewProvider();
        }
        return new ScrollPickerAdapterHolder(LayoutInflater.from(mContext).inflate(mViewProvider.resLayout(), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ScrollPickerAdapterHolder holder, int position) {
        mViewProvider.onBindView(holder.itemView, mDataList.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public int getSelectedItemOffset() {
        return mSelectedItemOffset;
    }

    @Override
    public int getSelectedItemPosition() {
        return mSelectedItemPosition;
    }

    @Override
    public int getVisibleItemNumber() {
        return mVisibleItemNum;
    }

    @Override
    public int getLineColor() {
        return mLineColor;
    }

    @Override
    public void updateView(View itemView, boolean isSelected) {
        mViewProvider.updateView(itemView, isSelected);
        adaptiveItemViewSize(itemView);
        if (isSelected && mOnScrollListener != null) {
            mOnScrollListener.onScrolled(itemView);
        }
        itemView.setOnClickListener(isSelected ? v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onSelectedItemClicked(v);
            }
        } : null);
    }

    private void adaptiveItemViewSize(View itemView) {
        int h = itemView.getHeight();
        if (h > maxItemH) {
            maxItemH = h;
        }

        int w = itemView.getWidth();
        if (w > maxItemW) {
            maxItemW = w;
        }

        itemView.setMinimumHeight(maxItemH);
        itemView.setMinimumWidth(maxItemW);
    }

    static class ScrollPickerAdapterHolder extends RecyclerView.ViewHolder {
        private final View itemView;

        private ScrollPickerAdapterHolder(@NonNull View view) {
            super(view);
            itemView = view;
        }
    }

    public interface OnClickListener {
        void onSelectedItemClicked(View v);
    }

    public interface OnScrollListener {
        void onScrolled(View currentItemView);
    }

    public static class ScrollPickerAdapterBuilder<T> {
        private final ScrollPickerAdapter<T> mAdapter;

        public ScrollPickerAdapterBuilder(Context context) {
            mAdapter = new ScrollPickerAdapter<>(context);
        }

        public ScrollPickerAdapterBuilder<T> selectedItemOffset(int offset) {
            mAdapter.mSelectedItemOffset = offset;
            return this;
        }

        public ScrollPickerAdapterBuilder<T> selectedItemPosition(int position) {
            mAdapter.mSelectedItemPosition = position;
            return this;
        }

        public ScrollPickerAdapterBuilder<T> setDataList(List<T> list) {
            mAdapter.mDataList.clear();
            mAdapter.mDataList.addAll(list);
            return this;
        }

        public ScrollPickerAdapterBuilder<T> setOnClickListener(OnClickListener listener) {
            mAdapter.mOnItemClickListener = listener;
            return this;
        }

        public ScrollPickerAdapterBuilder<T> visibleItemNumber(int num) {
            mAdapter.mVisibleItemNum = num;
            return this;
        }

        public ScrollPickerAdapterBuilder<T> setItemViewProvider(IViewProvider<T> viewProvider) {
            mAdapter.mViewProvider = viewProvider;
            return this;
        }

        public ScrollPickerAdapterBuilder<T> setDivideLineColor(String colorString) {
            mAdapter.mLineColor = Color.parseColor(colorString);
            return this;
        }

        public ScrollPickerAdapterBuilder<T> setOnScrolledListener(OnScrollListener listener) {
            mAdapter.mOnScrollListener = listener;
            return this;
        }

        public ScrollPickerAdapter<T> build() {
            adaptiveData(mAdapter.mDataList);
            mAdapter.notifyDataSetChanged();
            return mAdapter;
        }

        private void adaptiveData(List<T> list) {
            int visibleItemNum = mAdapter.mVisibleItemNum;
            int selectedItemOffset = mAdapter.mSelectedItemOffset;
            for (int i = 0; i < mAdapter.mSelectedItemOffset; i++) {
                list.add(0, null);
            }

            for (int i = 0; i < visibleItemNum - selectedItemOffset - 1; i++) {
                list.add(null);
            }
        }
    }
}
