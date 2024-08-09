package com.example.notesapp.adapter

import android.content.ClipData
import android.content.ClipDescription
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.R
import com.example.notesapp.models.NoteItem

class NotesAdapter(
    private var list: MutableList<NoteItem>,
    private var clickListener: NoteClickListener
) : RecyclerView.Adapter<ListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return  ListViewHolder(inflater, parent)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val noteItem = list[position]
        val itemView = holder.itemView
        val titleText = itemView.findViewById<TextView>(R.id.note_list_item_title)
        val noteText = itemView.findViewById<TextView>(R.id.note_list_item_note)

        titleText.text = noteItem.title
        noteText.text = noteItem.note

        itemView.setOnLongClickListener{
            val clipData = noteItem.id.toString()
            val item =  ClipData.Item(clipData)
            val mimeType = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)

            val data = ClipData(clipData, mimeType, item)

            val dragShadowBuilder = View.DragShadowBuilder(it)
            it.startDragAndDrop(data, dragShadowBuilder, it, 0)

            true
        }

        itemView.setOnClickListener{
            clickListener.onClick(noteItem)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


}

class ListViewHolder(inflater: LayoutInflater, parent: ViewGroup):
        RecyclerView.ViewHolder(inflater.inflate(R.layout.note_list_item, parent, false))

interface NoteClickListener{
    fun onClick(noteItem: NoteItem)
}