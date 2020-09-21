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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_log, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var logText = items[position]
        var tag = ""

        if (logText.startsWith("DEBUG") ||
            logText.startsWith("WARNING") ||
            logText.startsWith("ERROR")) {
            tag = logText.split(" ")[0]
            logText = logText.replace("$tag ", "")
        }

        val drawableId = when(tag) {
            "ERROR" -> R.drawable.ic_baseline_stop_error_24
            "WARNING" -> R.drawable.ic_baseline_stop_warning_24
            else -> R.drawable.ic_baseline_stop_24
        }

        holder.log.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, drawableId), null, null, null)
        holder.log.text = logText
    }
}