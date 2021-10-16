@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.nek12.androidutils.safenavcontroller

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigator

class SafeNavController(
    private val navController: NavController
) {

    fun navigate(@IdRes from: Int, @IdRes to: Int) = navigate(from, to, null)

    fun navigate(@IdRes from: Int, @IdRes to: Int, bundle: Bundle?) =
        navigate(from, to, bundle, null, null)

    fun navigate(
        @IdRes from: Int, @IdRes to: Int, bundle: Bundle?,
        navOptions: NavOptions?, navigatorExtras: Navigator.Extras?
    ) {
        if (navController.currentDestination?.id == from) {
            navController.navigate(to, bundle, navOptions, navigatorExtras)
        }
    }

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
