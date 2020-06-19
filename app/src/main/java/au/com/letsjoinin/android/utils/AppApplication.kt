package au.com.letsjoinin.android.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_AUTO
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.ios.IosEmojiProvider

//import com.facebook.FacebookSdk;
//import com.facebook.appevents.AppEventsLogger;

class AppApplication : MultiDexApplication() {


    companion object {
    }

    override fun onCreate() {
        super.onCreate()
        try {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_AUTO)
            EmojiManager.install(IosEmojiProvider())
            PreferenceManager.init(applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun attachBaseContext(base: Context) {


        try {
            MultiDex.install(this@AppApplication)
            super.attachBaseContext(base)
        } catch (e: Exception) {
            val vmName = System.getProperty("java.vm.name")
            if (!vmName!!.startsWith("Java")) {
                throw e
            }
        }

    }


}
