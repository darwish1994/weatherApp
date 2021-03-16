package com.robustastudio.weather.main.adapter

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.robustastudio.weather.R
import com.robustastudio.weather.common.dp.TempHistory
import com.robustastudio.weather.common.listener.TempHistoryListener
import com.robustastudio.weather.common.utils.extention.loadFrom
import com.robustastudio.weather.databinding.ItemHistoryTempLayoutBinding
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import java.text.SimpleDateFormat
import java.util.*

class TemperatureHistoryAdapter(
    private val listener: TempHistoryListener,
    data: OrderedRealmCollection<TempHistory>?
) :
    RealmRecyclerViewAdapter<TempHistory, TemperatureHistoryAdapter.TempViewHolder>(
        data,
        true,
        true
    ) {
    private val simpleDateFormat by lazy {
        SimpleDateFormat(
            "dd MMM yyyy hh:mm a",
            Locale.getDefault()
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TempViewHolder {
        return TempViewHolder(
            ItemHistoryTempLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TempViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }

    }

    inner class TempViewHolder(binding: ItemHistoryTempLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val card = binding.root
        private val photo = binding.photo
        private val stats = binding.status
        private val temp = binding.temp
        private val maxTemp = binding.maxTemp
        private val minTemp = binding.minTemp
        private val wind = binding.wind
        private val location = binding.location
        private val date = binding.date
        private lateinit var tempHistory: TempHistory

        init {
            binding.shareBtn.setOnClickListener {
                listener.shareView(drawBitMapOfView())
            }
            binding.root.setOnClickListener {
                if (::tempHistory.isInitialized)
                    listener.onItemClick(tempHistory)
            }

        }

        fun bind(data: TempHistory) {
            tempHistory = data
            tempHistory.filePath?.let { photo.loadFrom(it) }
            stats.text = tempHistory.weather
            temp.text = itemView.context.getString(R.string.temp, tempHistory.temp.toString())
            maxTemp.text =
                itemView.context.getString(R.string.temp_max, tempHistory.temp.toString())
            minTemp.text =
                itemView.context.getString(R.string.temp_min, tempHistory.temp.toString())
            location.text = tempHistory.location
            wind.text = itemView.context.getString(R.string.wind_speed, tempHistory.temp.toString())
            tempHistory.id?.let {
                date.text = simpleDateFormat.format(Date(it))
            }

        }

        private fun drawBitMapOfView(): Bitmap {
            val b = Bitmap.createBitmap(
                card.width,
                card.height,
                Bitmap.Config.ARGB_8888
            )
            val c = Canvas(b)
            card.draw(c)
            return b
        }


    }
}