package com.example.chatyapp.groupieAdapter

import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.example.chatyapp.R
import com.squareup.picasso.Picasso


class FriendAdapter(private val message: String, private val image2: Uri) :
    Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val view = viewHolder.itemView
        val image = view.findViewById<ImageView>(R.id.chatFrom_imageView)
        val message_send = view.findViewById<TextView>(R.id.chatFrom_TextView)
        message_send.text = message
        Picasso.get().load(image2).into(image)

    }

    override fun getLayout(): Int {
        return R.layout.chat_from_user_item
    }
}

