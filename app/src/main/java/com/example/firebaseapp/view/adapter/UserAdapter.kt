package com.example.firebaseapp.view.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebaseapp.view.user.UserActivity
import com.example.firebaseapp.databinding.ItemUserBinding
import com.example.firebaseapp.model.remote.response.UserResponse
import com.example.firebaseapp.view.update.UpdateActivity

class UserAdapter(private val userActivity: UserActivity) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private var listUser = ArrayList<UserResponse>()

    @SuppressLint("NotifyDataSetChanged")
    fun setListUser(listUser: ArrayList<UserResponse>) {
        this.listUser = listUser
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        holder.bind(listUser[position])
    }

    override fun getItemCount(): Int {
        return listUser.size
    }

    inner class ViewHolder(private var binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(userResponse: UserResponse) {
            Glide.with(itemView.context)
                .load(userResponse.link)
                .into(binding.ivImg)

            binding.tvLink.text = userResponse.user

            binding.btnSetHapus.setOnClickListener {
                val documentId = userResponse.documentId
                userActivity.deleteData(documentId)
            }

            binding.btnSetUbah.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val intent = Intent(it.context, UpdateActivity::class.java)
                    intent.putExtra("id", userResponse.documentId)
                    itemView.context.startActivity(intent)
                }
            }
        }
    }
}