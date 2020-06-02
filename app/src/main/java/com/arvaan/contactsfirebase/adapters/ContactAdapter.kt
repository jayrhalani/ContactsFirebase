package com.arvaan.contactsfirebase.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arvaan.contactsfirebase.R
import com.arvaan.contactsfirebase.data.Contact
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.ObjectKey

class ContactAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    private var contactList: List<Contact> = ArrayList()

    fun setContacts(contactList: List<Contact>) {
        this.contactList = contactList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.contact_list_item, parent, false)
        return ContactViewHolder(view)
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contactList[position]

        val contactNameString = "${contact.firstName} ${contact.lastName}"

        holder.contactName.text = contactNameString

        if (contact.avatar.isNotEmpty()) {
            val requestOptions = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .signature(ObjectKey(System.currentTimeMillis().toString()))

            Glide.with(context)
                .load(contact.avatar)
                .placeholder(R.drawable.ic_person_white_24dp)
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        holder.contactAvatar.setPadding(0, 0, 0, 0)
                        return false
                    }

                })
                .apply(requestOptions)
                .into(holder.contactAvatar)
        }

        holder.view.setOnClickListener {
            onItemClickListener.onItemClick(contact)
        }
    }

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactName: TextView = itemView.findViewById(R.id.tv_contact_name)
        val contactAvatar: ImageView = itemView.findViewById(R.id.iv_list_item_avatar)
        val view: View = itemView
    }

    interface OnItemClickListener {
        fun onItemClick(contact: Contact)
    }
}