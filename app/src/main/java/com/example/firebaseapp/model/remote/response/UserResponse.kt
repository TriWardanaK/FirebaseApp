package com.example.firebaseapp.model.remote.response

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserResponse(
    var documentId: String = "",
    val link: String = "",
    val user: String = ""
) : Parcelable