package com.qtk.kotlintest.calender.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qtk.kotlintest.databinding.TimeLayoutBinding
import com.qtk.kotlintest.widget.scroll_picker.adapter.ScrollPickerAdapter
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TimeSelectAdapter(
    initTime: String,
    private val onTimeSelect: (String) -> Unit
): RecyclerView.Adapter<TimeSelectAdapter.TimeViewHolder>() {
    private var time = intArrayOf()
    init {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val calender = Calendar.getInstance()
        try {
            sdf.parse(initTime)?.let {
                calender.time = it
            }
        }catch (ignore: Exception){}
        time = intArrayOf(
            calender[Calendar.HOUR_OF_DAY],
            calender[Calendar.MINUTE]
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeViewHolder {
        val binding = TimeLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TimeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimeViewHolder, position: Int) {
        holder.bindView()
    }

    override fun getItemCount(): Int = 1

    inner class TimeViewHolder(private val binding: TimeLayoutBinding): RecyclerView.ViewHolder(
        binding.root
    ) {
        private val initialHour: Int = time[0]
        private val initialMinute: Int = time[1]
        private var hour: String = if(time[0].toString().length == 1) "0${time[0]}" else "${time[0]}"
        private var minute: String = if(time[1].toString().length == 1) "0${time[1]}" else "${time[1]}"
        private val hours = arrayListOf<String>()
        private val minutes = arrayListOf<String>()
        private var hourPosition = 0
        private var minutePosition = 0

        private val coroutineScope = MainScope()
        private val hourState = MutableStateFlow("")
        private val minuteState = MutableStateFlow("")

        init {
            for (i in 1..23) {
                if (initialHour == i) hourPosition = i - 1
                hours.add(if(i.toString().length == 1) "0$i" else "$i")
            }
            for (i in 0..59) {
                if (initialMinute == i) minutePosition = i
                minutes.add(if(i.toString().length == 1) "0$i" else "$i")
            }
            coroutineScope.launch {
                hourState
                    .filter {
                        !TextUtils.equals(hour, it)
                    }
                    .collectLatest {
                        hour = it
                        onTimeSelect("${hour}:$minute")
                    }
            }
            coroutineScope.launch {
                minuteState
                    .filter {
                        !TextUtils.equals(minute, it)
                    }
                    .collectLatest {
                        minute = it
                        onTimeSelect("${hour}:$minute")
                    }
            }
        }

        fun bindView() {
            with(binding) {
                val hourAdapter = ScrollPickerAdapter.ScrollPickerAdapterBuilder<String>(root.context)
                    .setDataList(hours)
                    .selectedItemOffset(4)
                    .visibleItemNumber(9)
                    .selectedItemPosition(hourPosition)
                    .setItemViewProvider(null)
                    .setOnScrolledListener {
                        hourState.value = it.tag as String
                    }.build()
                val minuteAdapter = ScrollPickerAdapter.ScrollPickerAdapterBuilder<String>(root.context)
                    .setDataList(minutes)
                    .selectedItemOffset(4)
                    .visibleItemNumber(9)
                    .selectedItemPosition(minutePosition)
                    .setItemViewProvider(null)
                    .setOnScrolledListener {
                        minuteState.value = it.tag as String
                    }.build()
                with(hourPv) {
                    layoutManager = LinearLayoutManager(root.context)
                    adapter = hourAdapter
                }
                with(minutePv) {
                    layoutManager = LinearLayoutManager(root.context)
                    adapter = minuteAdapter
                }
            }
        }
    }
}