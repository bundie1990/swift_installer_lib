package com.brit.swiftinstaller

import android.app.Application
import com.brit.swiftinstaller.utils.InstallerServiceHelper
import com.brit.swiftinstaller.utils.UpdateChecker

class SwiftInstaller : Application() {

    companion object {
        private lateinit var sSwiftInstaller: SwiftInstaller

        fun getInstance(): SwiftInstaller {
            return sSwiftInstaller
        }
    }

    override fun onCreate() {
        super.onCreate()
        sSwiftInstaller = this

        InstallerServiceHelper.connectService(this)
    }

}