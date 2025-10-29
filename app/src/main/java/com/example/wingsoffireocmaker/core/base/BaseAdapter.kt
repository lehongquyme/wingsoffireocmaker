package com.example.wingsoffireocmaker.core.base

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseAdapter<T, VB : ViewBinding>(
    private val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> VB,
    diffCallback: DiffUtil.ItemCallback<T>?=null
) : RecyclerView.Adapter<BaseAdapter<T, VB>.BaseViewHolder>() {

    protected val items = ArrayList<T>()
    var selectedPosition: Int = RecyclerView.NO_POSITION

    inner class BaseViewHolder(val binding: VB) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem(item: T, position: Int) = onBind(binding, item, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = bindingInflater(LayoutInflater.from(parent.context), parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bindItem(items[position], position)
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<T>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()

    }

    fun getItem(position: Int): T? = items.getOrNull(position)

    protected abstract fun onBind(binding: VB, item: T, position: Int)
}