package com.vbazh.navimeet.ui

import android.support.v7.widget.RecyclerView
import android.view.View
import com.vbazh.navimeet.data.User
import kotlinx.android.synthetic.main.item_contact.view.*

class ContactsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindMessage(user: User?) {
        with(user) {
            itemView.username.text = user?.login
        }
    }
}