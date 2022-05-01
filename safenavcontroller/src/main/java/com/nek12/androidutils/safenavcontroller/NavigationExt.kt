@file:Suppress("unused")

package com.nek12.androidutils.safenavcontroller

import android.os.Bundle
import android.util.Log
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController

fun Fragment.popBackStack() = findNavController().popBackStack()

fun Fragment.findSafeNavController(@IdRes currentFragmentNavId: Int): SafeNavController =
    SafeNavController(currentFragmentNavId, findNavController())

fun Fragment.navigateUp() = findNavController().navigateUp()

fun Fragment.tryNavigate(directions: NavDirections, navOptions: NavOptions? = null) =
    findNavController().tryNavigate(directions, navOptions)

fun Fragment.tryNavigate(directions: NavDirections, extras: Navigator.Extras) =
    findNavController().tryNavigate(directions, extras)

fun Fragment.tryNavigate(
    @IdRes to: Int,
    args: Bundle? = null,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null,
) = findNavController().tryNavigate(to, args, navOptions, navigatorExtras)

fun Fragment.canNavigateUp() = findNavController().canNavigateUp

fun NavController.tryNavigate(directions: NavDirections, navOptions: NavOptions? = null): Boolean {
    return tryLogging { navigate(directions, navOptions) }
}

fun NavController.tryNavigate(directions: NavDirections, extras: Navigator.Extras): Boolean {
    return tryLogging { navigate(directions, extras) }
}

/**
 * @return true if navigation was successful
 */
fun NavController.tryNavigate(
    @IdRes to: Int,
    args: Bundle? = null,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null,
): Boolean {
    return tryLogging { navigate(to, args, navOptions, navigatorExtras) }
}
val NavController.canNavigateUp get() = previousBackStackEntry != null


private fun tryLogging(block: () -> Unit): Boolean =
    runCatching { block() }.onFailure { Log.e("tryNavigate", "Unable to navigate", it) }.isSuccess
