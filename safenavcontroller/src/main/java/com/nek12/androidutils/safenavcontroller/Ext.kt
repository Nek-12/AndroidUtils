package com.nek12.androidutils.safenavcontroller

import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController

private const val UNFAVORABLE_NAVIGATION = "use safe navigation instead"

fun Fragment.popBackStack() {
    findNavController().popBackStack()
}

@Deprecated(UNFAVORABLE_NAVIGATION, ReplaceWith("findSafeNavController.navigate()"))
fun NavController.tryNavigate(directions: NavDirections, navOptions: NavOptions? = null): Boolean {
    return runCatching { this.navigate(directions, navOptions) }.isSuccess
}

@Deprecated(UNFAVORABLE_NAVIGATION, ReplaceWith("findSafeNavController.navigate()"))
fun NavController.tryNavigate(directions: NavDirections, extras: Navigator.Extras): Boolean {
    return runCatching { this.navigate(directions, extras) }.isSuccess
}

fun Fragment.findSafeNavController(): SafeNavController = SafeNavController(findNavController())
