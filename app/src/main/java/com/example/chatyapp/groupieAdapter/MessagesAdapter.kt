package com.example.chatyapp.groupieAdapter

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.example.chatyapp.DataClass.ChatMessage
import com.example.chatyapp.DataClass.FirebaseUser
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.Item
import com.example.chatyapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder

class MessagesAdapter(private val message: ChatMessage) : Item<GroupieViewHolder>() {
    var chatParenterUser: FirebaseUser? = null

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val view = viewHolder.itemView
        val chatPartnerId: String

        //The MESSAGE TEXTVIEW
        val latestMessage=  view.findViewById<TextView>(R.id.lastChat_latestchat)
        latestMessage.text= message.text


        val usernameName = view.findViewById<TextView>(R.id.username_latestchat)

        if (message.fromId == FirebaseAuth.getInstance().uid) {
            chatPartnerId = message.toId
        } else {
            chatPartnerId = message.fromId
        }
        val ref = Firebase.database.getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                chatParenterUser = snapshot.getValue(FirebaseUser::class.java)
                usernameName.text = chatParenterUser?.userName
                Picasso.get().load(chatParenterUser?.imageURI)
                    .into(view.findViewById<ImageView>(R.id.imageview_latestchat))

            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("MessagesAdapter", "sss" + error.message)
            }
        })
    }

    override fun getLayout(): Int {
        return R.layout.lateset_messages_item
    }
}