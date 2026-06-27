package me.jo.netscan

import android.app.Application
import me.jo.netscan.data.repository.CustomPortRepository
import me.jo.netscan.data.repository.DeviceCustomizationRepository
import me.jo.netscan.network.NetworkScanner
import me.jo.netscan.theme.ThemeManager

/**
 * Application class for NetworkScanner app.
 *
 * The in-app language is handled by AppCompat's per-app locales
 * (see [me.jo.netscan.ui.SettingsViewModel]); with autoStoreLocales
 * enabled in the manifest, AppCompat persists and restores the selected locale
 * automatically, so no manual Configuration/attachBaseContext handling is needed.
 */
class NetworkScannerApp : Application() {

    /** Shared NetworkScanner instance to avoid duplicate state across ViewModels. */
    val scanner: NetworkScanner by lazy { NetworkScanner(this) }

    /** Repositories backed by SharedPreferences */
    val deviceCustomizationRepository: DeviceCustomizationRepository by lazy {
        DeviceCustomizationRepository(this)
    }

    val customPortRepository: CustomPortRepository by lazy {
        CustomPortRepository(this)
    }

    override fun onCreate() {
        super.onCreate()

        // Initialize theme manager
        ThemeManager.initialize(this)
    }
}
