package com.draco.ktweak

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

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
        val logTextRaw = items[position]
        val tag = logTextRaw.split(" ")[1]
        val logText = logTextRaw.replace(tag, "").trim()

        /* Parse bash color codes */
        var drawableId = R.drawable.ic_baseline_arrow_debug_24
        if (tag.contains("[91m")) drawableId = R.drawable.ic_baseline_arrow_error_24
        if (tag.contains("[93m")) drawableId = R.drawable.ic_baseline_arrow_warn_24

        holder.log.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, drawableId), null, null, null)
        holder.log.text = logText
    }
}