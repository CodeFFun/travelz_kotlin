package com.example.travelapp.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp.R
import com.example.travelapp.model.DestinationModel

class DestinationAdapter (private val destinationList: MutableList<DestinationModel>, private val onDeleteClicked: (String) -> Unit, private val onUpdate: (String) -> Unit)
    : RecyclerView.Adapter<DestinationAdapter.DestinationViewHolder>(){

    class DestinationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val title : TextView = itemView.findViewById(R.id.destinationTitle)
        val desc : TextView = itemView.findViewById(R.id.destinationDesc)
        val image : ImageView = itemView.findViewById(R.id.destinationImg)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
        val updateButton: Button = itemView.findViewById(R.id.updateButton)
        var destinationId : String = ""
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DestinationAdapter.DestinationViewHolder {
        val itemView  = LayoutInflater.from(parent.context).inflate(R.layout.list_destination, parent, false)
        return DestinationViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: DestinationAdapter.DestinationViewHolder,
        position: Int
    ) {
        val destination = destinationList[position]
        holder.title.text = destination.title
        holder.desc.text = destination.desc
        val imageUri = Uri.parse(destination.imageUri) // Convert String to Uri
        holder.image.setImageURI(imageUri)
        holder.destinationId = destination.destinationId

        holder.deleteButton.setOnClickListener{
            onDeleteClicked(holder.destinationId)
        }

        holder.updateButton.setOnClickListener{
            onUpdate(holder.destinationId)
        }

    }

    override fun getItemCount(): Int {
        return destinationList.size
    }

    fun updateData(newList: List<DestinationModel>) {
        destinationList.clear()
        destinationList.addAll(newList)
        notifyDataSetChanged()
    }
}