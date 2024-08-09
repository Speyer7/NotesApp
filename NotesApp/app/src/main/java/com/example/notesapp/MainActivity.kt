package com.example.notesapp

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.adapter.NoteClickListener
import com.example.notesapp.adapter.NotesAdapter
import com.example.notesapp.database.NotesDatabase
import com.example.notesapp.dialogs.AddNoteListener
import com.example.notesapp.dialogs.NoteDialog
import com.example.notesapp.models.NoteItem
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(), AddNoteListener, NoteClickListener{

    private lateinit var notesDatabase: NotesDatabase
    private lateinit var notesBin: ImageView
    private var isNoteBinDrawableSet = false




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notesDatabase = NotesDatabase(this)

        val fabBtn = findViewById<FloatingActionButton>(R.id.notes_fab)

        fabBtn.setOnClickListener{
            val dialog = NoteDialog(this, null)
            dialog.show(supportFragmentManager, "Add Note")
        }

        getNotesFromDB()

        notesBin = findViewById(R.id.notes_bin)
        notesBin.setOnDragListener(dragListener)

    }

    private var dragListener = View.OnDragListener { view, dragEvent ->
        when(dragEvent.action){
            DragEvent.ACTION_DRAG_STARTED -> {
                view.invalidate()
                true
            }
            DragEvent.ACTION_DRAG_LOCATION -> true
            DragEvent.ACTION_DRAG_ENDED -> true

            DragEvent.ACTION_DRAG_EXITED -> {
                view.invalidate()
                true
            }

            DragEvent.ACTION_DROP -> {
                val item = dragEvent.clipData.getItemAt(0)
                showDeleteDialog(item.text.toString())


                true
            }

            else -> false
        }

    }


    private fun showDeleteDialog(noteId : String){
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to delete this note ?")
            .setCancelable(true)
            .setPositiveButton("Yes", object : DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    val result = notesDatabase.deleteNote(noteId.toInt())

                    if (result > 0){
                        getNotesFromDB()
                        setBinDrawable()
                        Toast.makeText(this@MainActivity, "note has been deleted", Toast.LENGTH_SHORT).show()
                    }else {
                        Toast.makeText(this@MainActivity, "error deleting note", Toast.LENGTH_SHORT).show()
                        if (p0 != null) {
                            p0.dismiss()
                        }
                    }
                }

            })
            .setNegativeButton("No", object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, p1: Int) {
                    if (dialog != null) {
                        dialog.dismiss()
                    }
                }
            }).create().show()
    }

    private fun setupRecyclerView(list: ArrayList<NoteItem>){
        val recyclerView = findViewById<RecyclerView>(R.id.notes_recyclerview)

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = NotesAdapter(list, this)
    }


    private fun setBinDrawable(){
        if (!isNoteBinDrawableSet){
            isNoteBinDrawableSet = true
            notesBin.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bin))
        }
    }

    private fun getNotesFromDB(){
        val notes = notesDatabase.getNotes()
        setupRecyclerView(notes)
    }


    override fun onClick(noteItem: NoteItem) {
        NoteDialog(this, noteItem).show(supportFragmentManager, "Edit Note")

    }

    override fun addNote(noteItem: NoteItem) {
        notesDatabase.addNote(noteItem)
        getNotesFromDB()
    }

    override fun editNote(note: String, title: String, id: Int) {
        notesDatabase.editNote(note, title, id)

        getNotesFromDB()
    }
}