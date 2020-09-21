package com.draco.ktweak.Fragments

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
import androidx.preference.SwitchPreference
import com.draco.ktweak.Activities.ChangelogActivity
import com.draco.ktweak.Activities.LogActivity
import com.draco.ktweak.BuildConfig
import com.draco.ktweak.Utils.KTweak
import com.draco.ktweak.R
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.snackbar.Snackbar
import java.io.File

class MainPreferenceFragment: PreferenceFragmentCompat() {
    private lateinit var ktweak: KTweak
    private lateinit var progress: ProgressBar

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main, rootKey)

        /* Initialize variables */
        ktweak = KTweak(requireContext())
        progress = requireActivity().findViewById(R.id.progress)

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
        val autoFetch = findPreference<SwitchPreference>(getString(R.string.pref_auto_fetch))!!.isChecked
        ktweak.execute(autoFetch) {
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
                        Snackbar.make(requireView(), "Cannot find KTweak script", Snackbar.LENGTH_SHORT)
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

                if (!log.exists()) {
                    Snackbar.make(requireView(), "No log file to delete", Snackbar.LENGTH_SHORT)
                        .setAction("Dismiss") {}
                        .show()
                } else {
                    log.delete()
                    Snackbar.make(requireView(), "Successfully deleted log file", Snackbar.LENGTH_SHORT)
                        .setAction("Dismiss") {}
                        .show()
                }
            }

            getString(R.string.pref_clear_cached) -> {
                val script = File(requireContext().filesDir, KTweak.scriptName)

                if (!script.exists()) {
                    Snackbar.make(requireView(), "No cached script to delete", Snackbar.LENGTH_SHORT)
                        .setAction("Dismiss") {}
                        .show()
                } else {
                    script.delete()
                    Snackbar.make(requireView(), "Successfully deleted log file", Snackbar.LENGTH_SHORT)
                        .setAction("Dismiss") {}
                        .show()
                }
            }

            getString(R.string.pref_view_changelog) -> {
                val intent = Intent(requireContext(), ChangelogActivity::class.java)
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

            getString(R.string.pref_source_code) -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/tytydraco/ktweak/"))
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