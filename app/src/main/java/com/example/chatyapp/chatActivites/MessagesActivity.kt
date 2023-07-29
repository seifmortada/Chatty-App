package com.example.chatyapp.chatActivites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.chatyapp.DataClass.ChatMessage
import com.example.chatyapp.DataClass.FirebaseUser
import com.example.chatyapp.R
import com.example.chatyapp.databinding.ActivityMessagesBinding
import com.example.chatyapp.groupieAdapter.MessagesAdapter
import com.example.chatyapp.loginRegisterActivites.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class MessagesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMessagesBinding
    private val adapter = GroupAdapter<GroupieViewHolder>()
    val latestMessagesMap = HashMap<String, ChatMessage>()
    lateinit var userImageProfile: String

    companion object {
        var currentUser: FirebaseUser? = null
        val context = this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_messages)

        binding.newMessageButton.setOnClickListener {
            val intent = Intent(this, NewMessageActivity::class.java)
            startActivity(intent)
        }
        binding.recyclerviewMessages.adapter = adapter
        binding.recyclerviewMessages.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.signout.setOnClickListener {
            signOut()
        }
        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(view.context, ChatLogActivity::class.java)
            val raw = item as MessagesAdapter
            intent.putExtra(NewMessageActivity.key, raw.chatParenterUser)
            startActivity(intent)
        }

        listenForLatestMessages()
        fetchCurrentUser()
        ifUserLoggedin()
    }

    private fun isLoading(answer: Boolean) {
        if (answer == true) {
            binding.progressBar.visibility = View.VISIBLE
        } else binding.progressBar.visibility = View.GONE

    }

    private fun listenForLatestMessages() {
        isLoading(true)
        val fromId = FirebaseAuth.getInstance().uid
        val ref = Firebase.database.getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val data = snapshot.getValue(ChatMessage::class.java) ?: return

                isLoading(false)
                latestMessagesMap[snapshot.key!!] = data
                refreshrecyclerView()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val data = snapshot.getValue(ChatMessage::class.java) ?: return
                isLoading(false)
                latestMessagesMap[snapshot.key!!] = data
                refreshrecyclerView()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }


    private fun refreshrecyclerView() {
        adapter.clear()
        latestMessagesMap.values.forEach {
            adapter.add(MessagesAdapter(it))
        }
    }

    private fun fetchCurrentUser() {
        val authUid = FirebaseAuth.getInstance().uid
        val ref = Firebase.database.getReference("/users/$authUid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(FirebaseUser::class.java)
                binding.username.text = currentUser!!.userName
                userImageProfile = currentUser!!.imageURI
                Picasso.get().load(userImageProfile).into(binding.imageProfile)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("MessagesActivity", error.message)
            }
        })
    }


    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this@MessagesActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun ifUserLoggedin() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}