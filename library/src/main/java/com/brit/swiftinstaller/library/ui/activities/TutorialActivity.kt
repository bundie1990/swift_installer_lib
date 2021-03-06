/*
 *
 *  * Copyright (C) 2018 Griffin Millender
 *  * Copyright (C) 2018 Per Lycke
 *  * Copyright (C) 2018 Davide Lilli & Nishith Khanna
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.brit.swiftinstaller.library.ui.activities

import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import com.brit.swiftinstaller.library.R
import com.brit.swiftinstaller.library.utils.OverlayUtils
import com.brit.swiftinstaller.library.utils.ShellUtils
import com.brit.swiftinstaller.library.utils.swift
import com.hololo.tutorial.library.TutorialActivity
import kotlinx.android.synthetic.main.no_root.view.*
import org.jetbrains.anko.doAsync

open class TutorialActivity : TutorialActivity() {

    private var notificationManager: NotificationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(
                "boot_enabled",
                "Swift overlays enabling",
                "The notification notifies you when overlays are being enabled on boot")

        createNotificationChannel(
                "app_listener",
                "Listen for new and update overlays",
                "Notifies when a new overlay is available to be installer, or an update is available")

        if (swift.romHandler.requiresRoot() && !ShellUtils.isRootAccessAvailable) {
            val dialog = Dialog(this, R.style.AppTheme)
            val layout = View.inflate(this, R.layout.no_root, null)
            dialog.setContentView(layout)
            dialog.setCancelable(false)
            layout.no_root_msg.text = getString(R.string.no_root_msg)
            layout.no_root_exit.visibility = View.VISIBLE
            layout.no_root_exit.setOnClickListener {
                finishAffinity()
            }
            dialog.show()
            return
        }

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("appHasRunBefore",
                        false)) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            setIndicator(R.drawable.tutorial_indicator)
            setIndicatorSelected(R.drawable.tutorial_indicator_selected)
            swift.romHandler.addTutorialSteps(this)
        }
    }

    override fun finishTutorial() {
        super.finishTutorial()
        val intent = Intent(this, CustomizeActivity::class.java)
        intent.putExtra("parentActivity", "tutorial")
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean("appHasRunBefore", true).apply()
        startActivity(intent)
        finish()
    }

    private fun createNotificationChannel(id: String, name: String, description: String) {

        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(id, name, importance)

        channel.description = description
        notificationManager?.createNotificationChannel(channel)
    }
}