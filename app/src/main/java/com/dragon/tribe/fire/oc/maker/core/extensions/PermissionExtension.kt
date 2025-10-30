package com.dragon.tribe.fire.oc.maker.core.extensions

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import com.dragon.tribe.fire.oc.maker.R

import com.dragon.tribe.fire.oc.maker.core.utils.SystemUtils.setLocale
import kotlin.apply
import kotlin.collections.all

internal fun Context.checkPermissions(listPermission: Array<String>): Boolean {
    return listPermission.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }
}

internal fun Activity.requestPermission(permissions: Array<String>, requestCode: Int) {
    ActivityCompat.requestPermissions(this, permissions, requestCode)
}

@RequiresApi(Build.VERSION_CODES.M)
internal fun Activity.goToSettings() {
    setLocale(this)
    val dialog = AlertDialog.Builder(this)
        .setTitle(R.string.go_to_setting_title)
        .setMessage(R.string.go_to_setting_message)
        .setPositiveButton(R.string.settings) { dialog, _ ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${this@goToSettings.packageName}")
            }
            this.startActivity(intent)
            dialog.dismiss()
            hideNavigation()
        }
        .setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
            hideNavigation()
        }
        .setCancelable(false)
        .create()

    dialog.show()
    val positiveButton: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
    val negativeButton: Button = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
    positiveButton.setTextColor("#6B1B10".toColorInt())
    negativeButton.setTextColor(getColor(R.color.black))
}