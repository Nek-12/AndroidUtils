package com.nek12.androidutils.extensions.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * A [BroadcastReceiver] that calls [goAsync] and launches a coroutine to execute long-running tasks.
 * The [receive] method is still executed on main thread, move to another thread as needed.
 *
 * According to Android limitations, you still have about **10 seconds** to finish the execution before
 * the system forcibly kills the receiver. For longer tasks, use WorkManager or a Service
 */
abstract class CoroutineReceiver : BroadcastReceiver() {

    protected val scope = CoroutineScope(SupervisorJob())

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null) return

        val result = goAsync()
        scope.launch {
            try {
                receive(context, intent)
            } finally {
                result.finish()
            }
        }
    }

    /**
     * Still executed on main thread, so be careful
     */
    protected abstract suspend fun receive(context: Context, intent: Intent)
}
