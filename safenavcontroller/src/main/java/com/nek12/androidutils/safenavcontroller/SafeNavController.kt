@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.nek12.androidutils.safenavcontroller

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController

private const val UNFAVORABLE_NAVIGATION = "Not a favorable approach, use safe navigation instead"

/**
 * SafeNavController class is a wrapper around navController that takes care of that
 * DestinationNotFound exception for you. It happens when the user makes multiple fast attempts
 * to navigate, especially if there are animations involved. Wrap your nav controller using
 * findNavController() with this class and use it to navigate or use an extension function.
 * If navigation is not possible, the operation will be simply canceled.
 */
class SafeNavController(
    private val navController: NavController
) {

    /**
     * @param from The navigation ID of the current fragment you want to navigate from
     * @param to The id of the fragment to navigate
     */
    fun navigate(@IdRes from: Int, @IdRes to: Int) = navigate(from, to, null)

    /**
     * @param from The navigation ID of the current fragment you want to navigate from
     * @param to The id of the fragment to navigate
     * @param bundle Unsafe navigation arguments to pass
     */
    fun navigate(@IdRes from: Int, @IdRes to: Int, bundle: Bundle?) =
        navigate(from, to, bundle, null, null)

    /**
     * @param from The navigation ID of the current fragment you want to navigate from
     * @param to The id of the fragment to navigate
     */
    fun navigate(
        @IdRes from: Int, @IdRes to: Int, bundle: Bundle?,
        navOptions: NavOptions?, navigatorExtras: Navigator.Extras?
    ) {
        if (navController.currentDestination?.id == from) {
            navController.navigate(to, bundle, navOptions, navigatorExtras)
        }
    }

    /**
     * @param from The navigation ID of the current fragment you want to navigate from
     * @param directions Generated NavDirections class. Pass argunments to them if you wish.
     */
    fun navigate(
        @IdRes from: Int, directions: NavDirections, extras: Navigator.Extras
    ) {
        if (navController.currentDestination?.id == from) {
            navController.navigate(directions, extras)
        }
    }

    fun navigate(
        @IdRes from: Int, directions: NavDirections
    ) {
        if (navController.currentDestination?.id == from) {
            navController.navigate(directions)
        }
    }

    fun navigateUp() = navController.navigateUp()

    fun popBackStack() = navController.popBackStack()
}

@Deprecated(UNFAVORABLE_NAVIGATION, ReplaceWith("SafeNavController"))
fun NavController.tryNavigate(directions: NavDirections, navOptions: NavOptions? = null): Boolean {
    return runCatching { this.navigate(directions, navOptions) }.isSuccess
}

@Deprecated(UNFAVORABLE_NAVIGATION, ReplaceWith("SafeNavController"))
fun NavController.tryNavigate(directions: NavDirections, extras: Navigator.Extras): Boolean {
    return runCatching { this.navigate(directions, extras) }.isSuccess
}

fun Fragment.findSafeNavController(): SafeNavController = SafeNavController(findNavController())
