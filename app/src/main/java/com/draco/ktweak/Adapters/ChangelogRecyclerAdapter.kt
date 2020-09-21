package com.draco.ktweak.Adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.draco.ktweak.R
import com.draco.ktweak.Utils.ChangelogItem

class ChangelogRecyclerAdapter(
    private val context: Context,
    private val items: List<ChangelogItem>
): RecyclerView.Adapter<ChangelogRecyclerAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val message = view.findViewById<TextView>(R.id.message)!!
        val date = view.findViewById<TextView>(R.id.date)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_changelog, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val changelogItem = items[position]
        holder.message.text = changelogItem.message
        holder.date.text = changelogItem.date
        holder.itemView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(changelogItem.url))
            context.startActivity(intent)
        }
    }
}