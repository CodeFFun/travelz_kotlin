package com.example.travelapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp.R
import com.example.travelapp.model.BookingModel

class BookingAdapter(private val bookingList: MutableList<BookingModel>) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val bookingDate: TextView = itemView.findViewById(R.id.bookingDate)
        val guideName: TextView = itemView.findViewById(R.id.guideName)
        val guideEmail: TextView = itemView.findViewById(R.id.guideEmail)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BookingAdapter.BookingViewHolder {
        val itemView  = LayoutInflater.from(parent.context).inflate(R.layout.list_booking, parent, false)
        return BookingViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookingAdapter.BookingViewHolder, position: Int) {
        val booking = bookingList[position]
        holder.bookingDate.text = booking.bookingDate
        holder.guideName.text = booking.guideName
        holder.guideEmail.text = booking.guideEmail
    }

    override fun getItemCount(): Int {
        return bookingList.size
    }

    fun updateData(newList: List<BookingModel>) {
        bookingList.clear()
        bookingList.addAll(newList)
        notifyDataSetChanged()
    }


}