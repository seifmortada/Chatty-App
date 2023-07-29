package com.example.chatyapp.groupieAdapter

import android.widget.ImageView
import android.widget.TextView
import com.example.chatyapp.DataClass.FirebaseUser
import com.example.chatyapp.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class UserItemGroupieAdapter(val user: FirebaseUser) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        //Passing the items to the views
        val itemView = viewHolder.itemView
        val userName = itemView.findViewById<TextView>(R.id.username_item)
        val userImage = itemView.findViewById<ImageView>(R.id.userImage_item)
        userName.text = user.userName
        Picasso.get().load(user.imageURI).into(userImage)
    }

    override fun getLayout(): Int {
// Passing the item layout of the user
        return R.layout.user_item_layout
    }
}

