package com.example.chatyapp.chatActivites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.chatyapp.DataClass.FirebaseUser
import com.example.chatyapp.R
import com.example.chatyapp.databinding.ActivityNewMessageBinding
import com.example.chatyapp.groupieAdapter.UserItemGroupieAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class NewMessageActivity : AppCompatActivity() {
    companion object {
        const val tag = "NewMessageActivity"
        const val key = "USER_KEY"
    }

    private lateinit var binding: ActivityNewMessageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_message)
        supportActionBar?.title = "Select User"
        binding.imageBack.setOnClickListener { goBack() }
        getTheUsersFromFirebase()


    }

    private fun getTheUsersFromFirebase() {
        loading(true)
        //GETTING REFERENCE OF THE FIREBASE DATABASE TO GET ALL THE USERS
        val ref = Firebase.database.getReference("/users")

        //ADDING LISTENER FOR SINGLE VALUE TO MAKE OPERATION FOR EACH USER COMING
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            //OVERRIDES THE ONDATACHANGED TO GET ACCESS TO THE DATA
            override fun onDataChange(snapshot: DataSnapshot) {

                // USING THE GROUPIE ADAPTER AND THE GROUPIE VIEWHOLDER FOR THE RECYCLERVIEW WE HAVE
                val adapter = GroupAdapter<GroupieViewHolder>()

                //GETTING THE USERS OF THE DATA BY .CHILDERN ,THEN LOOPS THEM TO PASS EVERY NEW USER TO THE ADAPTER
                snapshot.children.forEach {

                    //THIS LOG SHOWS THAT WE GET ALL THE USERS FROM THE DATABASE(UID,USERNAME,USER_IMAGE_URI)
                    Log.i(tag, it.toString())

                    // WE SAVE THE USERS IN THE FORM OF THE FIREBASEUSER DATACLASS , THEN SAVE THEM TO THE VARIABLE
                    val user = it.getValue(FirebaseUser::class.java)

                    //WE ADD THE DATA TO THE ADAPTER BY USING THE GROUPIE ADAPTER CLASS , AND PASS THE USER AS A PARAMETER
                    if (FirebaseAuth.getInstance().uid != user?.uid) {
                        adapter.add(UserItemGroupieAdapter(user!!))
                        loading(false)

                    }

                    adapter.setOnItemClickListener { item, view ->
                        val user1 = item as UserItemGroupieAdapter
                        val intent = Intent(view.context, ChatLogActivity::class.java)
                        intent.putExtra(key, user1.user)
                        startActivity(intent)
                        finish()
                    }

                }
                //WE BIND THE ADAPTER OF THE RECYCLER VIEW IN THE XML TO THIS ADAPTER
                binding.recyclerViewNewMessages.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(tag, error.message)
            }
        })
    }

    private fun loading(b: Boolean) {
        if (b == true) {
            binding.progressBar4.visibility = View.VISIBLE
        } else binding.progressBar4.visibility = View.GONE
    }

    private fun goBack() {
        val intent = Intent(this@NewMessageActivity, MessagesActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
}