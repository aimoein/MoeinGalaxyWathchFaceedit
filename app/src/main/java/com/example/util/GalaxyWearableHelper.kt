package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast

object GalaxyWearableHelper {

    // Known package names for Samsung Galaxy Wearable & Watch Manager plugins
    private val WEARABLE_PACKAGES = listOf(
        "com.samsung.android.app.watchmanager",
        "com.samsung.android.geargplugin",
        "com.samsung.android.gearnplugin",
        "com.samsung.android.waterplugin",
        "com.samsung.android.modena.plugin",
        "com.google.android.wearable.app"
    )

    fun isGalaxyWearableInstalled(context: Context): Boolean {
        val pm = context.packageManager
        for (pkg in WEARABLE_PACKAGES) {
            try {
                pm.getPackageInfo(pkg, 0)
                return true
            } catch (_: Exception) {
                // Check next package
            }
        }
        return false
    }

    /**
     * Attempts multiple robust methods to launch Samsung Galaxy Wearable.
     * Guaranteed not to throw silent false negatives on Android 11+ / One UI.
     */
    fun openGalaxyWearable(context: Context): Boolean {
        val pm = context.packageManager

        // Method 1: Check launch intent for all known Samsung Wearable packages
        for (pkg in WEARABLE_PACKAGES) {
            try {
                val launchIntent = pm.getLaunchIntentForPackage(pkg)
                if (launchIntent != null) {
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(launchIntent)
                    return true
                }
            } catch (e: Exception) {
                // Continue trying other methods
            }
        }

        // Method 2: Resolve by Action & Category
        for (pkg in WEARABLE_PACKAGES) {
            try {
                val intent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                    setPackage(pkg)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                val resolveInfo = pm.queryIntentActivities(intent, 0)
                if (resolveInfo.isNotEmpty()) {
                    val activityInfo = resolveInfo[0].activityInfo
                    intent.setClassName(activityInfo.packageName, activityInfo.name)
                    context.startActivity(intent)
                    return true
                }
            } catch (e: Exception) {
                // Continue
            }
        }

        // Method 3: Direct Component intent for Samsung Gear WatchManager
        try {
            val componentIntent = Intent().apply {
                setClassName(
                    "com.samsung.android.app.watchmanager",
                    "com.samsung.android.app.watchmanager.setupwizard.SplashScreenActivity"
                )
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(componentIntent)
            return true
        } catch (_: Exception) {
            // Continue
        }

        // Method 4: Fallback to Connected Devices / Bluetooth Settings
        try {
            val settingsIntent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(settingsIntent)
            Toast.makeText(
                context,
                "تنظیمات بلوتوث و ساعت باز شد. می‌توانید برنامه Galaxy Wearable را از لیست دستگاه‌ها انتخاب فرمایید.",
                Toast.LENGTH_LONG
            ).show()
            return true
        } catch (_: Exception) {
            // Continue
        }

        // Method 5: Open in Samsung Galaxy Store or Google Play Store
        try {
            val storeIntent = Intent(Intent.ACTION_VIEW, Uri.parse("samsungapps://ProductDetail/com.samsung.android.app.watchmanager")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(storeIntent)
            return true
        } catch (_: Exception) {
            try {
                val playIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.samsung.android.app.watchmanager")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(playIntent)
                return true
            } catch (_: Exception) {
                Toast.makeText(
                    context,
                    "لطفاً برنامه Galaxy Wearable را به صورت مستقیم از منوی برنامه‌های گوشی خود باز نمایید.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        return false
    }

    /**
     * Checks if current device is a Wear OS watch (e.g. Galaxy Watch 4, 5, 6, 7).
     */
    fun isWatchDevice(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(android.content.pm.PackageManager.FEATURE_WATCH)
    }

    /**
     * Requests setting this watch face or opening the watch face wallpaper picker.
     */
    fun setAsWatchFace(context: Context): Boolean {
        val watchFaceComponent = android.content.ComponentName(context, "com.example.wear.SolarWatchFaceService")

        // Method 1: Android Live Wallpaper Change Intent with Component
        try {
            val intent = Intent(android.app.WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
                putExtra(android.app.WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, watchFaceComponent)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            return true
        } catch (_: Exception) {}

        // Method 2: Wear OS Set Watch Face Intent
        try {
            val intent = Intent("com.google.android.wearable.watchface.action.SET_WATCH_FACE").apply {
                putExtra("com.google.android.wearable.watchface.extra.WATCH_FACE_COMPONENT", watchFaceComponent)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            return true
        } catch (_: Exception) {}

        // Method 3: Standard Wallpaper Chooser
        try {
            val intent = Intent(Intent.ACTION_SET_WALLPAPER).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            return true
        } catch (_: Exception) {}

        return false
    }
}
