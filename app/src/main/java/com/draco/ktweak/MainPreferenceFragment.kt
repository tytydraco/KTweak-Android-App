package com.draco.ktweak

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ProgressBar
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.snackbar.Snackbar
import java.io.File

class MainPreferenceFragment(
    private val progress: ProgressBar
): PreferenceFragmentCompat() {
    private lateinit var ktweak: KTweak

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main, rootKey)

        /* Initialize private class */
        ktweak = KTweak(requireContext())

        /* Update the version code string */
        val version = findPreference<Preference>(getString(R.string.pref_version))
        val flavor = if (BuildConfig.DEBUG) "debug" else "release"
        version!!.summary = "${BuildConfig.VERSION_NAME}-${flavor}"
    }

    private fun setProgressVisibility(visible: Boolean) {
        with(progress.animate()) {
            if (visible) progress.visibility = View.VISIBLE

            alpha(if (visible) 1f else 0f)
            duration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            setListener(object: AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    if (!visible) progress.visibility = View.INVISIBLE
                }
            })
        }
    }

    private fun runKtweak() {
        setProgressVisibility(true)
        ktweak.execute {
            requireActivity().runOnUiThread {
                setProgressVisibility(false)
                when (it) {
                    KTweak.Companion.ExecuteStatus.SUCCESS -> {
                        Snackbar.make(requireView(), "Successfully executed KTweak", Snackbar.LENGTH_SHORT)
                            .setAction("Dismiss") {}
                            .show()
                    }

                    KTweak.Companion.ExecuteStatus.FAILURE -> {
                        Snackbar.make(requireView(), "Failed to execute KTweak", Snackbar.LENGTH_SHORT)
                            .setAction("Dismiss") {}
                            .show()
                    }

                    KTweak.Companion.ExecuteStatus.MISSING -> {
                        Snackbar.make(requireView(), "Failed to fetch KTweak script", Snackbar.LENGTH_SHORT)
                            .setAction("Dismiss") {}
                            .show()
                    }
                }
            }
        }
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        if (preference != null) when (preference.key) {
            getString(R.string.pref_run_ktweak) -> {
                runKtweak()
            }

            getString(R.string.pref_view_logs) -> {
                val intent = Intent(requireContext(), LogActivity::class.java)
                startActivity(intent)
            }

            getString(R.string.pref_clear_logs) -> {
                val log = File(requireContext().filesDir, KTweak.logName)
                log.delete()

                Snackbar.make(requireView(), "Successfully deleted log file", Snackbar.LENGTH_SHORT)
                    .setAction("Dismiss") {}
                    .show()
            }

            getString(R.string.pref_view_changelog) -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(KTweak.changelogURL)
                startActivity(intent)
            }

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

            getString(R.string.pref_licenses) -> {
                val intent = Intent(requireContext(), OssLicensesMenuActivity::class.java)
                startActivity(intent)
            }
        }

        return super.onPreferenceTreeClick(preference)
    }
}