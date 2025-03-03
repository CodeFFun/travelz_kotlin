package com.example.travelapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.travelapp.R
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp.model.UserModel

class UserAdapter(private val userList : MutableList<UserModel>, private val createBooking: (String, String, String, String) -> Unit) : RecyclerView.Adapter<UserAdapter.UserViewHolder>(){

    class UserViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView) {
        val guideTitle : TextView = itemView.findViewById(R.id.guideTitle)
        val guideEmail: TextView = itemView.findViewById(R.id.guideEmail)
        val guideButton: Button = itemView.findViewById(R.id.guideButton)
        val bookingDate: TextView = itemView.findViewById(R.id.bookingDate)
        var userId: String = " "
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserViewHolder {
        val itemView  = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return UserViewHolder(itemView)
    }


    override fun onBindViewHolder(
        holder: UserViewHolder,
        position: Int
    ) {
        val currentItem = userList[position]
        holder.guideTitle.setText(currentItem.fullName)
        holder.guideEmail.setText(currentItem.email)
        holder.userId  = currentItem.userId

        holder.guideButton.setOnClickListener {
            createBooking(
                holder.userId, // The user's ID
                holder.guideTitle.text.toString(), // Guide's name
                holder.guideEmail.text.toString(), // Guide's email
                holder.bookingDate.text.toString() // Booking date
            )
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun updateData(newList: List<UserModel>) {
        userList.clear()
        userList.addAll(newList)
        notifyDataSetChanged()
    }


}