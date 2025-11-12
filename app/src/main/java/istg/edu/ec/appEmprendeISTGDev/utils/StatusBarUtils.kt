package istg.edu.ec.appEmprendeISTGDev.utils

import android.app.Activity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat

fun Activity.setStatusBarColor(colorResId: Int, lightIcons: Boolean) {
    window.statusBarColor = ContextCompat.getColor(this, colorResId)

    val decorView = window.decorView
    val controller = WindowInsetsControllerCompat(window, decorView)
    controller.isAppearanceLightStatusBars = lightIcons
}
