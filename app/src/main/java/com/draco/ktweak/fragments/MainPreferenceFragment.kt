package com.draco.ktweak.fragments

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
import com.draco.ktweak.BuildConfig
import com.draco.ktweak.R
import com.draco.ktweak.activities.ChangelogActivity
import com.draco.ktweak.activities.LogActivity
import com.draco.ktweak.retrofit.GitHub
import com.draco.ktweak.utils.Script
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.snackbar.Snackbar

class MainPreferenceFragment: PreferenceFragmentCompat() {
    private lateinit var script: Script
    private lateinit var github: GitHub
    private lateinit var progress: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /* Initialize variables */
        script = Script(requireContext())
        github = GitHub(requireContext())
        progress = requireActivity().findViewById(R.id.progress)

        /* Update the version code string */
        val version = findPreference<Preference>(getString(R.string.pref_version))!!
        val flavor = if (BuildConfig.DEBUG) "debug" else "release"
        version.summary = "${BuildConfig.VERSION_NAME}-${flavor}"

        /* Update available branches */
        val branch = findPreference<ListPreference>(getString(R.string.pref_branch))!!

        /* Update script on start */
        val updateOnStart = findPreference<SwitchPreference>(getString(R.string.pref_update_on_start))!!

        github.branches {
            activity?.runOnUiThread {
                branch.entries = it.toTypedArray()
                branch.entryValues = it.toTypedArray()

                /* Default to the first branch */
                if (branch.entry == null)
                    branch.setValueIndex(0)

                /* Update now, after the null check */
                if (updateOnStart.isChecked)
                    updateScript(false)
            }
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main, rootKey)
    }

    private fun setProgressVisibility(visible: Boolean) {
        val run = findPreference<Preference>(getString(R.string.pref_run))!!
        val update = findPreference<Preference>(getString(R.string.pref_update))!!

        run.isEnabled = !visible
        update.isEnabled = !visible

        with(progress.animate()) {
            if (visible)
                progress.visibility = View.VISIBLE

            val transparency = if (visible) 1f else 0f
            alpha(transparency)
            duration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            setListener(object: AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    if (!visible)
                        progress.visibility = View.GONE
                }
            })
        }
    }

    private fun runScript() {
        setProgressVisibility(true)
        Thread {
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

    private fun updateScript(showFailure: Boolean = true) {
        setProgressVisibility(true)
        Thread {
            val ret = script.update()
            activity?.runOnUiThread {
                setProgressVisibility(false)
                when (ret) {
                    Script.Companion.UpdateStatus.SUCCESS -> {
                        Snackbar.make(requireView(), getString(R.string.snackbar_update_success),
                            Snackbar.LENGTH_SHORT)
                            .setAction(getString(R.string.snackbar_dismiss)) {}
                            .show()
                    }

                    Script.Companion.UpdateStatus.FAILURE -> {
                        if (showFailure) Snackbar.make(requireView(), getString(R.string.snackbar_update_failure),
                            Snackbar.LENGTH_SHORT)
                            .setAction(getString(R.string.snackbar_dismiss)) {}
                            .show()
                    }

                    Script.Companion.UpdateStatus.UNCHANGED -> {
                        if (showFailure) Snackbar.make(requireView(), getString(R.string.snackbar_update_unchanged),
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

            getString(R.string.pref_update) -> {
                updateScript()
            }

            getString(R.string.pref_view_logs) -> {
                val intent = Intent(requireContext(), LogActivity::class.java)
                startActivity(intent)
            }

            getString(R.string.pref_view_changelog) -> {
                val intent = Intent(requireContext(), ChangelogActivity::class.java)
                startActivity(intent)
            }

            getString(R.string.pref_developer) -> {
                val fullName = requireContext().getString(R.string.git_full_name).replace(" ", "+")
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
                val gitAuthor = requireContext().getString(R.string.git_author)
                val gitRepo = requireContext().getString(R.string.git_repo)
                val uri = "https://github.com/$gitAuthor/$gitRepo"
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