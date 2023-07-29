package com.example.chatyapp.groupieAdapter

import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import com.example.chatyapp.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class UserAdapter(private val message: String, private val imageUser: Uri) :
    Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val view = viewHolder.itemView
        val message_send = view.findViewById<TextView>(R.id.chatTo_TextView)
        val image = view.findViewById<ImageView>(R.id.chatTo_imageView)
        message_send.text = message
        Picasso.get().load(imageUser).into(image)

    }

    override fun getLayout(): Int {
        return R.layout.chat_to_user_item
    }
}
