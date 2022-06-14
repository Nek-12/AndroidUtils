@file:Suppress("unused")

package com.nek12.androidutils.extensions.material

import android.content.Context
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

const val SNACKBAR_MAX_LINES = 3

/**
 * Show a snackbar and execute specified action.
 * @param action The action to execute on pressing the button
 * @param actionText What text to display on the action button?
 */
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

/**
 * Shows a snackbar with an action (by default "Cancel"), that you can specify actions for:
 * @param action Is going to be called when the user clicks the snackbar action button
 * @param onDismiss what is going to happen if the user does NOT cancel the action. This action will happen after a delay, when the snackbar disappears.
 *
 */
fun View.showLazyActionSnackbar(
    msg: String,
    actionText: String = this.context.getString(android.R.string.cancel),
    duration: Int = Snackbar.LENGTH_SHORT,
    animMode: Int = Snackbar.ANIMATION_MODE_SLIDE,
    action: ((snackbar: Snackbar) -> Unit)? = null,
    onDismiss: (snackbar: Snackbar) -> Unit,
) {
    val snackbar = Snackbar.make(this, msg, duration).apply {
        animationMode = animMode
        anchorView = this@showLazyActionSnackbar
        setAction(actionText) { action?.let { action -> action(this) } }
    }
    snackbar.addCallback(object: Snackbar.Callback() {
        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
            if (event != DISMISS_EVENT_ACTION) // always run the action except when the user clicks undo
                onDismiss(snackbar)
            super.onDismissed(transientBottomBar, event)
        }
    })
    snackbar.show()
}

/**
 * Shows a snackbar with specified message, duration and animMode.
 * The snackbar will appear above the view this function is called on,
 * most commonly binding.root
 * Max number of lines of text is 3.
 */
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

/**
 * Shows a snackbar with specified [msg] text, [duration] and [animMode].
 * The snackbar will appear above the view this function is called on,
 * most commonly binding.root
 * Max number of lines of text is 3.
 */
fun View.showSnackbar(
    @StringRes msg: Int,
    duration: Int = Snackbar.LENGTH_SHORT,
    animMode: Int = Snackbar.ANIMATION_MODE_SLIDE,
) = showSnackbar(context.getString(msg), duration, animMode)

/**
 * Shows a cancelable modal material dialog with specified [message], [title] and [icon].
 * Default icon is material info icon -> (i)
 * You can specify behavior for clicking cancel and ok buttons.
 */
fun Context.showInfoDialog(
    message: String,
    title: String,
    @DrawableRes icon: Int = R.drawable.ic_info_36dp,
    onCancel: ((dialog: DialogInterface) -> Unit)? = null,
    onAgree: (dialog: DialogInterface) -> Unit = {},
) {
    val d = MaterialAlertDialogBuilder(this)
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
 * @see [Context.showInfoDialog]
 */
fun Context.showInfoDialog(
    @StringRes title: Int,
    @StringRes content: Int,
    @DrawableRes icon: Int = R.drawable.ic_info_36dp,
    onCancel: ((dialog: DialogInterface) -> Unit)? = null,
    onAgree: (dialog: DialogInterface) -> Unit = {},
) {
    showInfoDialog(
        getString(title),
        getString(content),
        icon, onCancel, onAgree
    )
}

/**
 * Shows a modal dialog that has "Cancel" and "OK" buttons, but the "Cancel" button does nothing
 */
fun Context.showConfirmationDialog(
    @StringRes title: Int,
    @StringRes content: Int,
    @DrawableRes icon: Int,
    okAction: (dialog: DialogInterface) ->
    Unit
) = showInfoDialog(title, content, icon, {}, okAction)

/**
 * @see [Context.showInfoDialog]
 */
fun Fragment.showInfoDialog(
    @StringRes title: Int,
    @StringRes content: Int,
    @DrawableRes icon: Int = R.drawable.ic_info_36dp,
    onCancel: ((dialog: DialogInterface) -> Unit)? = null,
    onAgree: (dialog: DialogInterface) -> Unit = {},
) {
    requireContext().showInfoDialog(content, title, icon, onCancel, onAgree)
}

/**
 * @see [Context.showInfoDialog]
 */
fun Fragment.showInfoDialog(
    title: String,
    content: String,
    @DrawableRes icon: Int = R.drawable.ic_info_36dp,
    onCancel: ((dialog: DialogInterface) -> Unit)? = null,
    onAgree: (dialog: DialogInterface) -> Unit = {},
) {
    requireContext().showInfoDialog(
        title,
        content,
        icon, onCancel, onAgree
    )
}

/**
 * @param ratio: The percentage of the screen the sheet should take, e.g. 0.6 = 60%
 * **/
fun BottomSheetDialogFragment.setPeekHeightRatio(ratio: Double) {
    val height = requireContext().resources.displayMetrics.heightPixels.toDouble()
    behavior.setPeekHeight((height * ratio).toInt(), true)
}

val BottomSheetDialogFragment.behavior
    get() = (requireDialog() as BottomSheetDialog).behavior

fun BottomSheetDialogFragment.expand() {
    behavior.state = BottomSheetBehavior.STATE_EXPANDED
}

fun BottomSheetDialogFragment.collapse() {
    behavior.state = BottomSheetBehavior.STATE_COLLAPSED
}

fun Fragment.materialColor(@AttrRes attr: Int) = requireContext().materialColor(attr)

fun View.materialColor(@AttrRes attr: Int) = MaterialColors.getColor(this, attr)

/**
 * Make sure you're using context that has theme attribute set (e.g. activity)
 */
fun Context.materialColor(@AttrRes attr: Int, @ColorInt defValue: Int = Color.TRANSPARENT) =
    MaterialColors.getColor(this, attr, defValue)

@get:ColorInt
var MaterialButton.iconTintColor: Int?
    get() = iconTint?.defaultColor
    set(value) {
        iconTint = value?.let { ColorStateList.valueOf(it) }
    }

/**
 * @param gravity: One of [BadgeDrawable.TOP_END], [BadgeDrawable.TOP_START], [BadgeDrawable.BOTTOM_END], [BadgeDrawable.BOTTOM_START]
 */
@ExperimentalBadgeUtils
fun View.addBadge(
    gravity: Int = BadgeDrawable.TOP_END,
    number: Int? = null,
    @ColorInt color: Int = MaterialColors.getColor(this, R.attr.colorPrimary),
) {
    BadgeUtils.attachBadgeDrawable(
        BadgeDrawable.create(context).apply {
            badgeGravity = gravity
            isVisible = true
            number?.let { setNumber(it) }
            backgroundColor = color
        },
        this
    )
}

/**
 * A [FrameLayout] that wraps your layout and hold the behavior of the bottom sheet
 */
val BottomSheetDialogFragment.bottomSheet: FrameLayout
    get() = requireDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet)

/**
 * Whether the height of this bottom sheet is such that when expanded it's going to be full-screen
 */
var BottomSheetDialogFragment.fullscreen: Boolean
    get() = bottomSheet.layoutParams.height == MATCH_PARENT
    set(value) {
        bottomSheet.layoutParams.height = if (value) MATCH_PARENT else WRAP_CONTENT
    }
