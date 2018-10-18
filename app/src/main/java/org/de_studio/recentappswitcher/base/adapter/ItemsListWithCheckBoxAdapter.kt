package org.de_studio.recentappswitcher.base.adapter

import android.content.Context
import android.content.pm.PackageManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.realm.OrderedRealmCollection
import io.realm.RealmList
import io.realm.RealmRecyclerViewAdapter
import org.de_studio.recentappswitcher.IconPackManager
import org.de_studio.recentappswitcher.R
import org.de_studio.recentappswitcher.Utility
import org.de_studio.recentappswitcher.model.Item
import org.de_studio.recentappswitcher.onClick

/**
 * Created by HaiNguyen on 12/3/16.
 */

class ItemsListWithCheckBoxAdapter(internal var context: Context, data: OrderedRealmCollection<Item>?, internal var packageManager: PackageManager, internal var iconPack: IconPackManager.IconPack?, internal var checkedItems: RealmList<Item>?)
    : RealmRecyclerViewAdapter<Item, ItemsListWithCheckBoxAdapter.ViewHolder>(data, true) {

    private val itemClickedRL = PublishRelay.create<Item>()

    fun onItemClicked(): Observable<Item> = itemClickedRL


    fun setCheckedItems(checkedItems: RealmList<Item>) {
        this.checkedItems = checkedItems
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_items_list_check_box, parent, false), packageManager, iconPack, itemClickedRL)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(getItem(position), checkedItems)
    }

    class ViewHolder(itemView: View, val packageManager: PackageManager, val iconPack: IconPackManager.IconPack?, val itemClickedRelay: PublishRelay<Item>) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.icon)
        val itemLabel: TextView = itemView.findViewById(R.id.label)
        val checkbox: CheckBox = itemView.findViewById(R.id.check_box)

        fun bindView(item: Item?, checkedItems: RealmList<Item>?) {
            item?.apply {
                Utility.setItemIcon(this, itemView.context, icon, packageManager, iconPack, false)
                itemLabel.text = label
                if (checkedItems != null) {
                    checkbox.isChecked = checkedItems.contains(this)
                }
                itemView.onClick { itemClickedRelay.accept(this) }
            }
        }

    }
}
