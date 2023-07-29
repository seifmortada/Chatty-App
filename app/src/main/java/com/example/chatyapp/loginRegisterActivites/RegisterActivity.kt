package com.example.chatyapp.loginRegisterActivites

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.chatyapp.DataClass.FirebaseUser
import com.example.chatyapp.R
import com.example.chatyapp.chatActivites.MessagesActivity
import com.example.chatyapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private var selectedPhotoUri: Uri? = null

    companion object {
        const val tag = "RegisterActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)

        binding.apply {
            registerButton.setOnClickListener {
                // Register the user
                performRegister()
            }
            accountImage.setOnClickListener {
                // Open Gallery and choose photo
                choosePhoto()
            }
            alreadyHaveAccountTextView.setOnClickListener {
                // Go to the log in page
                finish()
            }
        }


    }





    @Suppress("DEPRECATION")
    private fun choosePhoto() {
        // Opening the gallery using intent

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Toast.makeText(this@RegisterActivity, "photo selected right", Toast.LENGTH_SHORT).show()
            selectedPhotoUri = data.data
            try {
                val bitMap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
                binding.selectPhotoTextview.text = ""
                binding.accountImage.setImageBitmap(bitMap)
            } catch (e: IOException) {
                Toast.makeText(this@RegisterActivity, "${e.message}", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun performRegister() {

        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        if (email.isEmpty() || password.isEmpty() || selectedPhotoUri == null) {
            Toast.makeText(
                this@RegisterActivity,
                "Please enter your email or password or select image",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        //This method gets instance of the Authentication page in the firebase and create the user , Then adding onCompleteListener to check it
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                //Checking if the account created successfully
                if (it.isSuccessful) {
                    Log.i(tag, "sss Created account successfully with uid ${it.result.user?.uid}")
                    uploadImageToStorage()
                }
                //else if not successful

                else {
                    return@addOnCompleteListener
                }
            }
            // Adding onFailure to printout the cause of the failure
            .addOnFailureListener {
                Toast.makeText(
                    this@RegisterActivity,
                    "Failed to create user ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.i(tag, "sss Failed to create user ${it.message}")
            }

    }

    private fun uploadImageToStorage() {
        // Making Random file name
        val fileName = UUID.randomUUID().toString()
        //Getting instance of the firebase storage such as firebase authentication and we set the location of the file in the storage then our name ,
        // Then we upload the image using putFile , and we get the image url to store it in the database
        val reference = FirebaseStorage.getInstance().getReference("/images/$fileName")
        reference.putFile(selectedPhotoUri!!).addOnSuccessListener { data ->
            Log.i(tag, "sss image successfully uploaded with path ${data.metadata?.path}")
            reference.downloadUrl.addOnSuccessListener {
                Log.i(tag, "sss image url is $data")
                saveUserToFirebaseDatabase(data.toString())
            }.addOnFailureListener {
                Toast.makeText(this@RegisterActivity, "${it.message}", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this@RegisterActivity, "${it.message}", Toast.LENGTH_SHORT).show()
        }

    }

    private fun saveUserToFirebaseDatabase(imgUri: String) {
        //Getting instance of the Firebase auth to get the userID so we can create the files in the database using it
        val uid = FirebaseAuth.getInstance().uid ?: ""

        //Getting refrence of the database and pass the location of the users with the id
        val reference = Firebase.database.getReference("/users/$uid")

        //uploading the user data to the firebase
        val userName = binding.usernameEditText.text.toString()
        val user = FirebaseUser(uid, userName, imgUri)

        reference.setValue(user)
            .addOnSuccessListener {
                Log.i(tag, "sss Saved the user to the Firebase Database")
                val intent = Intent(this, MessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this@RegisterActivity, "${it.message}", Toast.LENGTH_SHORT).show()
            }


    }
}