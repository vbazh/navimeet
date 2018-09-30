package com.vbazh.navimeet.ui

import android.support.v7.widget.RecyclerView
import android.view.View
import com.vbazh.navimeet.data.Interval
import kotlinx.android.synthetic.main.item_interval.view.*
import java.text.SimpleDateFormat
import java.util.*

class IntervalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindInterval(interval: Interval?) {

        with(interval) {
            itemView.startInterval.text =
                    SimpleDateFormat("yyyy.MM.dd ',' HH:mm").format(interval?.start?.let {
                        Date(
                            it
                        )
                    })
            itemView.endInterval.text =
                    SimpleDateFormat("yyyy.MM.dd ',' HH:mm").format(interval?.end?.let {
                        Date(
                            it
                        )
                    })
            itemView.address.text = interval?.address.toString()
            itemView.description.text = interval?.description.toString()
        }
    }
}