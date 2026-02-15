package com.example.movieapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.data.models.Contact
import com.example.movieapp.databinding.ContactItemBinding

class ContactsAdapter(
    private var contacts: List<Contact>,
    private val listener: RecyclerContactClickListener
) : RecyclerView.Adapter<ContactsAdapter.MyViewHolder>() {

    fun interface RecyclerContactClickListener {
        fun onItemClicked(contact: Contact)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // שימוש ב-ViewBinding כפי שנהוג בשאר הפרויקט שלך
        val binding = ContactItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // העברת האובייקט הספציפי לפונקציית ה-bind
        holder.bind(contacts[position])
    }

    override fun getItemCount(): Int = contacts.size

    fun setList(newList: List<Contact>) {
        contacts = newList
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val binding: ContactItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // החלפת ה-adapterPosition ב-bindingAdapterPosition העדכני
        fun bind(contact: Contact) {
            binding.contactName.text = contact.name

            // טיפול בטלפון
            if (contact.phoneNumber.isNotEmpty()) {
                binding.contactPhone.text = contact.phoneNumber
                binding.contactPhone.isVisible = true
            } else {
                binding.contactPhone.isVisible = false
            }

            // טיפול באימייל במידה וקיים במודל שלך
            if (contact.email.isNotEmpty()) {
                binding.contactEmail.text = contact.email
                binding.contactEmail.isVisible = true
            } else {
                binding.contactEmail.isVisible = false
            }

            // הגדרת הלחיצה בצורה בטוחה בתוך ה-bind
            itemView.setOnClickListener {
                listener.onItemClicked(contact)
            }
        }
    }
}