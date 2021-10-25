@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.nek12.androidutils.extensions.android

import android.app.*
import android.content.Context
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import com.nek12.androidutils.extensions.core.Time
import com.nek12.androidutils.extensions.core.setDayOfWeek
import java.time.DayOfWeek
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
/**
 * A class that simplifies work with [NotificationManager] and [AlarmManager]
 */
open class AlarmHelper(protected val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val calendar: Calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
    }


    /**
     * Create a notification action, i.e. a button to add to the notification.
     */
    fun createAction(text: String, intent: PendingIntent): Notification.Action {
        return Notification.Action.Builder(null, text, intent).build()
    }

    /** schedule **daily** alarm at a [time] **/
    fun setDaily(time: Time, intent: PendingIntent) {
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            getAlarmTimeMillis(time),
            AlarmManager.INTERVAL_DAY,
            intent
        )
    }

    fun setExactSingle(time: Time, intent: PendingIntent) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            getAlarmTimeMillis(time),
            intent
        )
    }

    /** schedule **weekly** alarm at a [time] **/
    fun setWeekly(time: Time, dayOfWeek: DayOfWeek, intent: PendingIntent) {
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            getAlarmTimeMillis(time, dayOfWeek),
            AlarmManager.INTERVAL_DAY * 7,
            intent
        )
    }

    private fun getAlarmTimeMillis(time: Time, dayOfWeek: DayOfWeek? = null): Long {
        calendar.apply {
            dayOfWeek?.let {
                setDayOfWeek(dayOfWeek)
            }
            set(Calendar.HOUR_OF_DAY, time.hour)
            set(Calendar.MINUTE, time.minute)
            set(Calendar.SECOND, time.second)
        }
        return calendar.timeInMillis
    }

    /** create a new notification. The channel must be already created
     * @return [Notification]
     * @see sendNotification
     * **/
    fun createNotification(
        channel_id: String,
        title: String,
        text: String,
        onTap: PendingIntent?,
        @DrawableRes icon: Int,
        vararg actions: Notification.Action,
    ): Notification {
        return Notification.Builder(context, channel_id)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(onTap)
            .setAutoCancel(true)
            .setActions(*actions)
            .build()
    }

    /** send the actual notification (triggers sound etc.)
     * @param id is a unique int ID for each notification that you must define
     * @see createNotification
     * **/
    fun sendNotification(notification: Notification, id: Int) {
        notificationManager.notify(id, notification)
    }

    fun createNotificationChannel(
        id: String,
        name: String,
        description: String,
        importance: Int = NotificationManager.IMPORTANCE_DEFAULT
    ) {
        val channel = NotificationChannel(id, name, importance).apply {
            this.description = description
        }
        notificationManager.createNotificationChannel(channel)
    }

    /** pass here the intent you scheduled for the **AlarmManager** ( NOT the notification ) **/
    fun cancelAlarm(intent: PendingIntent) {
        alarmManager.cancel(intent)
    }

    fun cancelNotification(id: Int) = notificationManager.cancel(id)
}
