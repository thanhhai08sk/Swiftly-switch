package org.de_studio.recentappswitcher.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import org.de_studio.recentappswitcher.Cons
import org.de_studio.recentappswitcher.R
import org.de_studio.recentappswitcher.Utility
import org.de_studio.recentappswitcher.edgeService.NewServiceView

/**
 * Created by HaiNguyen on 6/17/17.
 */
class ServiceToggleWidgetProvider: AppWidgetProvider() {
    private val TAG = ServiceToggleWidgetProvider::class.java.simpleName
    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        val serviceRunning = Utility.isMyServiceRunning(NewServiceView::class.java, context)
        appWidgetIds!!.forEach {
            val remoteView = RemoteViews(context!!.packageName, R.layout.widget_service_toggle)
            remoteView.setImageViewResource(R.id.widget_icon, if (serviceRunning) R.drawable.ic_pause_dark else R.drawable.ic_play_dark)
            val broadcastIntent = Intent()
//            broadcastIntent.setClassName(context, NewServiceView::class.java.name)
            broadcastIntent.action =Cons.ACTION_TOGGLE_EDGES
//            broadcastIntent.setClass(context,NewServiceView::class.java)
            val toggleIntent = PendingIntent.getBroadcast(context.applicationContext, 0, broadcastIntent, PendingIntent.FLAG_CANCEL_CURRENT)
            remoteView.setOnClickPendingIntent(R.id.widget_icon,toggleIntent)
            Log.e(TAG, "onUpdate: running = $serviceRunning")
            appWidgetManager!!.updateAppWidget(it,remoteView)
        }

    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
    }
}