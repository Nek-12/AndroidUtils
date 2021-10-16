@file:Suppress("unused")

package com.nek12.androidutils.extensions.material

import android.content.DialogInterface
import android.view.View
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

const val SNACKBAR_MAX_LINES = 3

fun View.showSnackbar(
    msg: String,
    actionText: String,
    duration: Int = Snackbar.LENGTH_SHORT,
    animMode: Int = Snackbar.ANIMATION_MODE_SLIDE,
    action: (view: View) -> Unit,
) {
    Snackbar.make(this, msg, duration).apply {
        animationMode = animMode
        anchorView = this@showSnackbar
        setAction(actionText, action)
    }.show()
}

fun View.showUndoSnackbar(
    msg: String,
    actionText: String = this.context.getString(android.R.string.cancel),
    duration: Int = Snackbar.LENGTH_SHORT,
    animMode: Int = Snackbar.ANIMATION_MODE_SLIDE,
    onUndo: ((snackbar: Snackbar) -> Unit)? = null,
    onDismiss: (snackbar: Snackbar) -> Unit,
) {
    val snackbar = Snackbar.make(this, msg, duration).apply {
        animationMode = animMode
        anchorView = this@showUndoSnackbar
        setAction(actionText) { onUndo?.let { action -> action(this) } }
    }
    snackbar.addCallback(object : Snackbar.Callback() {
        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
            if (event != DISMISS_EVENT_ACTION) //always run the action except when the user clicks undo
                onDismiss(snackbar)
            super.onDismissed(transientBottomBar, event)
        }
    })
    snackbar.show()
}

fun View.showSnackbar(
    msg: String,
    duration: Int = Snackbar.LENGTH_SHORT,
    animMode: Int = Snackbar.ANIMATION_MODE_SLIDE,
) {
    Snackbar.make(this, msg, duration).apply {
        animationMode = animMode
        anchorView = this@showSnackbar
        view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)?.apply {
            maxLines = SNACKBAR_MAX_LINES
            isSingleLine = false
        }
    }.show()
}


fun View.showSnackbar(
    @StringRes msg: Int,
    duration: Int = Snackbar.LENGTH_SHORT,
    animMode: Int = Snackbar.ANIMATION_MODE_SLIDE,
) = showSnackbar(context.getString(msg), duration, animMode)

fun Fragment.showInfoDialog(
    message: String,
    title: String,
    @DrawableRes icon: Int,
    onCancel: ((dialog: DialogInterface) -> Unit)? = null,
    onAgree: (dialog: DialogInterface) -> Unit = {},
) {
    val d = MaterialAlertDialogBuilder(requireContext())
        .setCancelable(true)
        .setIcon(icon)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(android.R.string.ok) { d, _ -> onAgree(d) }
    onCancel?.let { action ->
        d.setOnCancelListener { action(it) }
            .setNegativeButton(android.R.string.cancel) { d, _ -> action(d) }
    }
    d.show()
}

/**
 * @param ratio: The percentage of the screen the sheet should take, e.g. 0.6 = 60%
 * **/
fun BottomSheetDialogFragment.setPeekHeightRatio(ratio: Double) {
    val dialog = (dialog as BottomSheetDialog)
    val height = requireContext().resources.displayMetrics.heightPixels.toDouble()
    dialog.behavior.setPeekHeight((height * ratio).toInt(), true)
}
