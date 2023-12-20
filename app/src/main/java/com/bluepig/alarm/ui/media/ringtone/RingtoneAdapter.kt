package com.bluepig.alarm.ui.media.ringtone

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bluepig.alarm.databinding.ItemMediaBinding
import com.bluepig.alarm.domain.entity.alarm.media.RingtoneMedia
import com.bluepig.alarm.util.ext.checkNoPosition
import com.bluepig.alarm.util.ext.inflater

class RingtoneAdapter(
    private val _clickListener: (RingtoneMedia) -> Unit
) : ListAdapter<RingtoneMedia, RingtoneViewHolder>(differ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RingtoneViewHolder {
        val binding = ItemMediaBinding.inflate(
            parent.inflater,
            parent,
            false
        )
        return RingtoneViewHolder.create(binding).apply {
            binding.root.setOnClickListener {
                checkNoPosition {
                    _clickListener.invoke(currentList[bindingAdapterPosition])
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RingtoneViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val differ = object : DiffUtil.ItemCallback<RingtoneMedia>() {
            override fun areItemsTheSame(oldItem: RingtoneMedia, newItem: RingtoneMedia): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: RingtoneMedia,
                newItem: RingtoneMedia
            ): Boolean {
                return oldItem.title == newItem.title
                        && oldItem.uri == newItem.uri
            }
        }
    }
}