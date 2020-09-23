package com.draco.ktweak.Fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.draco.ktweak.Activities.ChangelogActivity
import com.draco.ktweak.Activities.LogActivity
import com.draco.ktweak.BuildConfig
import com.draco.ktweak.Utils.Script
import com.draco.ktweak.R
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.util.*

class MainPreferenceFragment: PreferenceFragmentCompat() {
    private lateinit var script: Script
    private lateinit var progress: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /* Initialize variables */
        script = Script(requireContext())
        progress = requireActivity().findViewById(R.id.progress)

        /* Update the version code string */
        val version = findPreference<Preference>(getString(R.string.pref_version))!!
        val flavor = if (BuildConfig.DEBUG) "debug" else "release"
        version.summary = "${BuildConfig.VERSION_NAME}-${flavor}"

        /* Update available branches */
        val branch = findPreference<ListPreference>(getString(R.string.pref_branch))!!
        Thread {
            try {
                val branchEntryValues = script.branches().toTypedArray()
                val branchEntries = branchEntryValues.map {
                    it.replace("-", " ")
                        .split(" ")
                        .joinToString(" ") { word ->
                            word.toLowerCase(Locale.getDefault()).capitalize(Locale.getDefault())
                        }
                }.toTypedArray()
                activity?.runOnUiThread {
                    branch.entries = branchEntries
                    branch.entryValues = branchEntryValues
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main, rootKey)
    }

    private fun setProgressVisibility(visible: Boolean) {
        with(progress.animate()) {
            if (visible) progress.visibility = View.VISIBLE

            alpha(if (visible) 1f else 0f)
            duration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            setListener(object: AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    if (!visible) progress.visibility = View.GONE
                }
            })
        }
    }

    private fun runScript() {
        setProgressVisibility(true)
        val autoFetch = findPreference<SwitchPreference>(getString(R.string.pref_auto_fetch))!!.isChecked
        Thread {
            if (autoFetch)
                script.fetch()
            val ret = script.execute()
            activity?.runOnUiThread {
                setProgressVisibility(false)
                when (ret) {
                    Script.Companion.ExecuteStatus.SUCCESS -> {
                        Snackbar.make(requireView(), getString(R.string.snackbar_run_success),
                            Snackbar.LENGTH_SHORT)
                            .setAction(getString(R.string.snackbar_dismiss)) {}
                            .show()
                    }

                    Script.Companion.ExecuteStatus.FAILURE -> {
                        Snackbar.make(requireView(), getString(R.string.snackbar_run_failure),
                            Snackbar.LENGTH_SHORT)
                            .setAction(getString(R.string.snackbar_dismiss)) {}
                            .show()
                    }

                    Script.Companion.ExecuteStatus.MISSING -> {
                        Snackbar.make(requireView(), getString(R.string.snackbar_run_missing),
                            Snackbar.LENGTH_SHORT)
                            .setAction(getString(R.string.snackbar_dismiss)) {}
                            .show()
                    }
                }
            }
        }.start()
    }

    private fun fetchScript() {
        setProgressVisibility(true)
        Thread {
            val ret = script.fetch()
            activity?.runOnUiThread {
                setProgressVisibility(false)
                when (ret) {
                    Script.Companion.FetchStatus.SUCCESS -> {
                        Snackbar.make(requireView(), getString(R.string.snackbar_fetch_success),
                            Snackbar.LENGTH_SHORT)
                            .setAction(getString(R.string.snackbar_dismiss)) {}
                            .show()
                    }

                    Script.Companion.FetchStatus.FAILURE -> {
                        Snackbar.make(requireView(), getString(R.string.snackbar_fetch_failure),
                            Snackbar.LENGTH_SHORT)
                            .setAction(getString(R.string.snackbar_dismiss)) {}
                            .show()
                    }

                    Script.Companion.FetchStatus.UNCHANGED -> {
                        Snackbar.make(requireView(), getString(R.string.snackbar_fetch_unchanged),
                            Snackbar.LENGTH_SHORT)
                            .setAction(getString(R.string.snackbar_dismiss)) {}
                            .show()
                    }
                }
            }
        }.start()
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        if (preference != null) when (preference.key) {
            getString(R.string.pref_run) -> {
                runScript()
            }

            getString(R.string.pref_fetch) -> {
                fetchScript()
            }

            getString(R.string.pref_view_logs) -> {
                val intent = Intent(requireContext(), LogActivity::class.java)
                startActivity(intent)
            }

            getString(R.string.pref_clear_cached) -> {
                val script = File(requireContext().filesDir, Script.scriptName)

                if (!script.exists()) {
                    Snackbar.make(requireView(), getString(R.string.snackbar_clear_cached_failure),
                        Snackbar.LENGTH_SHORT)
                        .setAction(getString(R.string.snackbar_dismiss)) {}
                        .show()
                } else {
                    script.delete()
                    Snackbar.make(requireView(), getString(R.string.snackbar_clear_cached_success),
                        Snackbar.LENGTH_SHORT)
                        .setAction(getString(R.string.snackbar_dismiss)) {}
                        .show()
                }
            }

            getString(R.string.pref_view_changelog) -> {
                val intent = Intent(requireContext(), ChangelogActivity::class.java)
                startActivity(intent)
            }

            getString(R.string.pref_developer) -> {
                val fullName = requireContext().getString(R.string.git_full_name)
                    .replace(" ", "+")
                val uri = "https://play.google.com/store/apps/developer?id=$fullName"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Snackbar.make(requireView(), getString(R.string.snackbar_intent_failed), Snackbar.LENGTH_SHORT)
                        .setAction(getString(R.string.snackbar_dismiss)) {}
                        .show()
                }
            }

            getString(R.string.pref_version) -> {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Snackbar.make(requireView(), getString(R.string.snackbar_intent_failed), Snackbar.LENGTH_SHORT)
                        .setAction(getString(R.string.snackbar_dismiss)) {}
                        .show()
                }
            }

            getString(R.string.pref_source_code) -> {
                val uri = "https://github.com/" +
                        requireContext().getString(R.string.git_author) + "/" +
                        requireContext().getString(R.string.git_repo)
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Snackbar.make(requireView(), getString(R.string.snackbar_intent_failed),
                        Snackbar.LENGTH_SHORT)
                        .setAction(getString(R.string.snackbar_dismiss)) {}
                        .show()
                }
            }

            getString(R.string.pref_contact) -> {
                val email = requireContext().getString(R.string.git_email)
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:$email"))
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Snackbar.make(requireView(), getString(R.string.snackbar_intent_failed), Snackbar.LENGTH_SHORT)
                        .setAction(getString(R.string.snackbar_dismiss)) {}
                        .show()
                }
            }

            getString(R.string.pref_licenses) -> {
                val intent = Intent(requireContext(), OssLicensesMenuActivity::class.java)
                startActivity(intent)
            }
        }

        return super.onPreferenceTreeClick(preference)
    }
}