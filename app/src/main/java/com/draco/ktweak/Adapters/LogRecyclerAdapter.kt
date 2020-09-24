package com.draco.ktweak.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.draco.ktweak.R

class LogRecyclerAdapter(
    private val context: Context,
    private val items: List<String>
): RecyclerView.Adapter<LogRecyclerAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val log = view.findViewById<TextView>(R.id.log)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_log, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var logText = items[position]
        var tag = ""

        /* If this is a KTweak logging line, parse it */
        if (logText.startsWith("DEBUG") ||
            logText.startsWith("WARNING") ||
            logText.startsWith("ERROR")) {
            tag = logText.split(" ")[0]
            logText = logText.replace("$tag ", "")
        }

        /* Change drawable based on tag */
        val drawable = when(tag) {
            "DEBUG" -> ContextCompat.getDrawable(context, R.drawable.ic_baseline_check_24)
            "WARNING" -> ContextCompat.getDrawable(context, R.drawable.ic_baseline_warning_24)
            "ERROR" -> ContextCompat.getDrawable(context, R.drawable.ic_baseline_clear_24)
            else -> null
        }

        holder.log.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        holder.log.text = logText
    }
}