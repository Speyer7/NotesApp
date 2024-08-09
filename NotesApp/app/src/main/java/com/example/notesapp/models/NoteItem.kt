package com.example.notesapp.models

import java.sql.Timestamp

data class NoteItem(
    val id: Int? = null,
    val title: String? = null,
    val note : String? = null,
    val timeStamp: String? = null
)
