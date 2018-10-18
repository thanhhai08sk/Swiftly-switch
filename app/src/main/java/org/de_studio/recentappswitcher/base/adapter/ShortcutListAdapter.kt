package org.de_studio.recentappswitcher.base.adapter

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.de_studio.recentappswitcher.R
import org.de_studio.recentappswitcher.onClick

/**
 * Created by HaiNguyen on 11/25/16.
 */

class ShortcutListAdapter(private val context: Context, private var resolveInfos: List<ResolveInfo>?) : RecyclerView.Adapter<ShortcutListAdapter.ViewHolder>() {
    private val packageManager: PackageManager = context.packageManager
    private val itemClickedRL = PublishRelay.create<ResolveInfo>()

    fun setData(resolveInfoList: List<ResolveInfo>) {
        this.resolveInfos = resolveInfoList
        notifyDataSetChanged()
    }

    fun onItemClicked(): Observable<ResolveInfo> = itemClickedRL

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_choose_shortcut_shortcut_list, parent, false), packageManager, itemClickedRL)

    override fun getItemCount(): Int {
        return if (resolveInfos == null) {
            0
        } else resolveInfos!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(resolveInfos!![position])
    }

    class ViewHolder(itemView: View, val packageManager: PackageManager, val itemClickedRL: PublishRelay<ResolveInfo>) : RecyclerView.ViewHolder(itemView) {
        val itemIcon: ImageView = itemView.findViewById(R.id.icon)
        val itemLabel: TextView = itemView.findViewById(R.id.label)

        fun bindView(info: ResolveInfo?) {
            info?.apply {
                itemIcon.setImageDrawable(loadIcon(packageManager))
                itemLabel.text = loadLabel(packageManager)
                itemView.onClick { itemClickedRL.accept(info) }
            }
        }
    }
}
