package com.example.chatyapp.DataClass

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseUser(
    val uid: String = "",
    val userName: String = "",
    val imageURI: String = ""
) : Parcelable
