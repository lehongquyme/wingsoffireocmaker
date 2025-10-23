package com.example.wingsoffireocmaker.ui.intro

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.wingsoffireocmaker.core.extensions.select
import com.example.wingsoffireocmaker.databinding.ItemIntroBinding
import com.example.wingsoffireocmaker.data.model.IntroModel

class IntroAdapter(val context: Context, private val items: List<IntroModel>) :
    RecyclerView.Adapter<IntroAdapter.ItemIntroViewHolder>() {

    inner class ItemIntroViewHolder(private val binding: ItemIntroBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: IntroModel) {
            binding.apply {
                imvImage.setImageResource(item.image)
                txtContent.text = ContextCompat.getString(context, item.content)
                txtContent.select()

            }
        }
    }

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): ItemIntroViewHolder {
        val binding = ItemIntroBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemIntroViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemIntroViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}