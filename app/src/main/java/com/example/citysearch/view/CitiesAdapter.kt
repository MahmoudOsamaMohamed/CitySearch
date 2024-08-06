package com.example.citysearch.view

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.citiesearch.model.pojos.City
import com.example.citiesearch.model.pojos.Coordinates
import com.example.citysearch.databinding.ItemCityBinding

class CitiesAdapter(
    private val onItemClickListener: (Coordinates) -> Unit
) : RecyclerView.Adapter<CitiesAdapter.ViewHolder>() {

    private var citiesList: List<City> = emptyList()

    class ViewHolder(val binding: ItemCityBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCityBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            val city = citiesList[position]
            Log.d("where is the error", city.toString())
            cityName.text = "${city.name}, ${city.country}"
            coordinates.text = "${city.coord.lat}, ${city.coord.lon}"
            root.setOnClickListener {
                onItemClickListener(city.coord)
            }
        }
    }

    override fun getItemCount(): Int {
        return citiesList.size
    }
// this function is used to update the list because I will need it meany times in show the result to the user
    fun submitList(list: List<City>) {
        citiesList = list
        notifyDataSetChanged()
    }
}
