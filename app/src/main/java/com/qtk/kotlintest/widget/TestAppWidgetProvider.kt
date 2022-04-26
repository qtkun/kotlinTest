package com.qtk.kotlintest.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.qtk.kotlintest.R

class TestAppWidgetProvider: AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach {
            val views = RemoteViews(context.packageName, R.layout.test_widget_layout)
            appWidgetManager.updateAppWidget(it, views)
        }
    }
}