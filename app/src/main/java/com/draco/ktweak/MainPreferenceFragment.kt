package com.draco.ktweak

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class MainPreferenceFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main, rootKey)

        /* Update the version code string */
        val version = findPreference<Preference>(getString(R.string.pref_version))
        val flavor = if (BuildConfig.DEBUG) "debug" else "release"
        version!!.summary = "${BuildConfig.VERSION_NAME}-${flavor}"
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        if (preference != null) when (preference.key) {
            getString(R.string.pref_developer) -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=Tyler+Nijmeh"))
                startActivity(intent)
            }

            getString(R.string.pref_version) -> {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                startActivity(intent)
            }

            getString(R.string.pref_contact) -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:tylernij@gmail.com"))
                startActivity(intent)
            }
        }

        return super.onPreferenceTreeClick(preference)
    }
}