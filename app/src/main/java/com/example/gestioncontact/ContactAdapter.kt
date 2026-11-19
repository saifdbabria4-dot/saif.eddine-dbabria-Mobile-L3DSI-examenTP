package com.example.gestioncontact

import android.content.Intent
import android.net.Uri
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(
    private var contactList: List<Contact>,
    private val textSizeSp: Float,
    private val textColor: Int,
    private val showCallButton: Boolean,
    private val onItemClick: (Long) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivItemPhoto: ImageView = view.findViewById(R.id.ivItemPhoto)
        val tvItemName: TextView = view.findViewById(R.id.tvItemName)
        val tvItemPhone: TextView = view.findViewById(R.id.tvItemPhone)
        val btnItemCall: Button = view.findViewById(R.id.btnItemCall)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contactList[position]

        holder.tvItemName.text = "${contact.nom} ${contact.prenom}"
        holder.tvItemName.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp)
        holder.tvItemName.setTextColor(textColor)

        holder.tvItemPhone.text = contact.telephone
        holder.tvItemPhone.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp - 2)

        if (!contact.photoUri.isNullOrEmpty()) {
            try {
                holder.ivItemPhoto.setImageURI(Uri.parse(contact.photoUri))
            } catch (e: Exception) {
                holder.ivItemPhoto.setImageResource(R.mipmap.ic_launcher)
            }
        } else {
            holder.ivItemPhoto.setImageResource(R.mipmap.ic_launcher)
        }

        if (showCallButton) {
            holder.btnItemCall.visibility = View.VISIBLE
            holder.btnItemCall.setOnClickListener {
                val context = holder.itemView.context
                val phone = contact.telephone.trim()
                if (phone.isNotEmpty()) {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Aucune application d'appel trouvée", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Numéro de téléphone vide", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            holder.btnItemCall.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            onItemClick(contact.id)
        }
    }

    override fun getItemCount(): Int = contactList.size

    fun updateList(newList: List<Contact>) {
        contactList = newList
        notifyDataSetChanged()
    }
}
