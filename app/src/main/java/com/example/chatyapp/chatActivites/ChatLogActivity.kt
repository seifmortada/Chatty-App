package com.example.chatyapp.chatActivites

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import com.example.chatyapp.DataClass.ChatMessage
import com.example.chatyapp.DataClass.FirebaseUser
import com.example.chatyapp.R
import com.example.chatyapp.databinding.ActivityChatLogBinding
import com.example.chatyapp.groupieAdapter.UserAdapter
import com.example.chatyapp.groupieAdapter.FriendAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder


class ChatLogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatLogBinding
    private lateinit var adapter: GroupAdapter<GroupieViewHolder>
    private lateinit var imageFriend: Uri
    private var currentUser: FirebaseUser = MessagesActivity.currentUser!!
    private lateinit var user: FirebaseUser

    companion object {
        const val tag = "ChatLogActivity"
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "channelId", "first channel", NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Test decription for my channel"
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()


        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_log)
        adapter = GroupAdapter<GroupieViewHolder>()
        @Suppress("DEPRECATION")
        user =
            intent.getParcelableExtra<FirebaseUser>(NewMessageActivity.key)!!

        imageFriend = user.imageURI.toUri()

        getMessageInfoFromFirebase()
        binding.apply {
            recyclerviewChatLog.adapter = adapter
            sendMessageButton.setOnClickListener {
                val message_toBeSent = enterMessageEditText.text.toString()
                sendMessageToFirebase(message_toBeSent, user)
            }
            username2.text = user.userName
            imageBack2.setOnClickListener { goBack() }
        }


    }

    private fun goBack() {
        val intent = Intent(this@ChatLogActivity, MessagesActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun sendMessageToFirebase(message: String, toUser: FirebaseUser) {
        //We get the values for the userID , FriendId,The referenceId , and finally the time in seconds and pass it to the dataClass then upload it to the firebase
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser.uid
        // getting the reference ,Then Making the node of the message if not exist then
        val ref = Firebase.database.getReference("/user-messages/$fromId/$toId").push()

        //We Make this reverse refrence so we can see the same message on the other side we we log into another account
        val reverseRef = Firebase.database.getReference("/user-messages/$toId/$fromId").push()

        if (fromId == null) return
        val messageInfo =
            ChatMessage(ref.key!!, message, fromId, toId, System.currentTimeMillis() / 1000)
        ref.setValue(messageInfo).addOnSuccessListener {
            Log.i(tag, "message uploaded ${ref.key}")
            binding.enterMessageEditText.text.clear()
            binding.recyclerviewChatLog.scrollToPosition(adapter.itemCount - 1)
        }.addOnFailureListener { Log.i(tag, "Failed to upload message because ${it.message}") }
        reverseRef.setValue(messageInfo)
        val messagesRef = Firebase.database.getReference("/latest-messages/$fromId/$toId")
        messagesRef.setValue(messageInfo)
        val messagesRefReverse = Firebase.database.getReference("/latest-messages/$toId/$fromId")
        messagesRefReverse.setValue(messageInfo)

    }

    private fun getMessageInfoFromFirebase() {
        loading(true)
        val fromId = FirebaseAuth.getInstance().uid
        val toId = user.uid
        val reference = Firebase.database.getReference("/user-messages/$fromId/$toId")

        //HERE WE USED ADD_CHILD_EVENT_LISTENER BECAUSE OT READS THE FIREBASE continuously AND MONITORING ANY CHANGE
        //NOT LIKE THE SINGLE_EVENT_LISTENER WHICH READS THE FIREBASE ONCE THEN DETACHES ITSELF FROM IT'S REFERENCE
        reference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val messageInfo = snapshot.getValue(ChatMessage::class.java)
                Log.i(tag, "The data retrieved is $messageInfo")
                if (messageInfo?.fromId == FirebaseAuth.getInstance().uid) {
                    adapter.add(UserAdapter(messageInfo!!.text, currentUser.imageURI.toUri()))
                } else {
                    com.example.chatyapp.NotificationUtil().notificationBuilder(
                        this@ChatLogActivity, messageInfo!!.toId, messageInfo.text
                    )
                    loading(false)
                    createNotificationChannel()
                    adapter.add(FriendAdapter(messageInfo.text, imageFriend))
                }
                binding.recyclerviewChatLog.scrollToPosition(adapter.itemCount - 1)
            }


            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

    }

    private fun loading(b: Boolean) {
        if (b==true) {
            binding.progressBar2.visibility = View.VISIBLE
        } else binding.progressBar2.visibility = View.GONE

    }
}