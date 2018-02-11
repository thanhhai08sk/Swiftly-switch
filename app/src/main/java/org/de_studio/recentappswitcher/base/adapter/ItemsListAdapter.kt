package org.de_studio.recentappswitcher.base.adapter

import android.content.Context
import android.content.pm.PackageManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import com.jakewharton.rxrelay2.PublishRelay
import io.hainguyen.androidcoordinated.utils.onClick
import io.reactivex.Observable
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import org.de_studio.recentappswitcher.IconPackManager
import org.de_studio.recentappswitcher.R
import org.de_studio.recentappswitcher.Utility
import org.de_studio.recentappswitcher.model.Item

/**
 * Created by HaiNguyen on 11/18/16.
 */

class ItemsListAdapter(private val context: Context, data: OrderedRealmCollection<Item>?, internal var packageManager: PackageManager, internal var iconPack: IconPackManager.IconPack?, internal var itemRes: Int) : RealmRecyclerViewAdapter<Item,ItemsListAdapter.ViewHolder>(data, true) {
    private var currentItem: Item? = null
    private val itemClickedRL = PublishRelay.create<Item>()

    fun setCurrentItem(item: Item) {
        currentItem = item
        notifyDataSetChanged()
    }

    fun onItemClicked(): Observable<Item> = itemClickedRL

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(context).inflate(itemRes, parent, false), packageManager, iconPack, itemClickedRL)

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindView(getItem(position), currentItem)
    }

    class ViewHolder(itemView: View, val packageManager: PackageManager, val iconPack: IconPackManager.IconPack?, val itemClickedRL: PublishRelay<Item>) : RecyclerView.ViewHolder(itemView) {
        val radioButton: RadioButton? = itemView.findViewById(R.id.radio_button)
        val checkBox: CheckBox? = itemView.findViewById(R.id.check_box)
        val itemIcon: ImageView = itemView.findViewById(R.id.icon)
        val itemLabel: TextView = itemView.findViewById(R.id.label)

        fun bindView(item: Item?, currentItem: Item?) {
            item?.apply {
                try {
                    Utility.setItemIcon(item, itemView.context, itemIcon, packageManager, iconPack, false)
                } catch (e: Exception) { }
                itemLabel.text = label

                if (currentItem != null) {
                    if (radioButton != null) {
                        radioButton.isChecked = currentItem == item
                    }

                    if (checkBox != null) {
                        checkBox.isChecked = currentItem == item
                    }
                } else {
                    if (radioButton != null) {
                        radioButton.isChecked = false
                    }

                    if (checkBox != null) {
                        checkBox.isChecked = false
                    }
                }
                itemView.onClick { itemClickedRL.accept(this) }
            }
        }
    }

}
