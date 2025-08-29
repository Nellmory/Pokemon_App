package ru.pokemon_app.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.pokemon_app.R
import ru.pokemon_app.databinding.PokemonCardItemBinding
import ru.pokemon_app.databinding.ItemLoadingBinding
import ru.pokemon_app.domain.model.PokemonListItem
import ru.pokemon_app.utils.ImageLoader

class PokemonAdapter : ListAdapter<PokemonListItem, RecyclerView.ViewHolder>(PokemonDiffCallback()) {

    private val items = mutableListOf<PokemonListItem>()

    companion object {
        private const val TYPE_ITEM = 0
        private const val TYPE_LOADING = 1
    }

    var onItemClick: ((PokemonListItem) -> Unit)? = null
    private var isLoading = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_ITEM) {
            val binding = PokemonCardItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            PokemonViewHolder(binding)
        } else {
            val binding = ItemLoadingBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            LoadingViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_ITEM) {
            val pokemonItem = items[position]
            (holder as PokemonViewHolder).bind(pokemonItem)
        }
    }

    override fun getItemCount(): Int {
        return items.size + if (isLoading) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < items.size) TYPE_ITEM else TYPE_LOADING
    }


    fun setData(newList: List<PokemonListItem>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    fun appendData(newItems: List<PokemonListItem>) {
        val start = items.size
        items.addAll(newItems)
        notifyItemRangeInserted(start, newItems.size)
    }

    fun setLoading(loading: Boolean) {
        val wasLoading = isLoading
        isLoading = loading
        if (wasLoading != loading) {
            if (loading) {
                notifyItemInserted(items.size)
            } else {
                notifyItemRemoved(items.size)
            }
        }
    }

    inner class PokemonViewHolder(private val binding: PokemonCardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(items[adapterPosition])
                }
            }
        }

        fun bind(pokemonItem: PokemonListItem) {
            binding.pokemonName.text = pokemonItem.name.replaceFirstChar { it.uppercase() }

            val pokemonId = extractIdFromUrl(pokemonItem.url)

            ImageLoader.loadPokemonImage(
                context = binding.root.context,
                imageView = binding.pokemonImage,
                progressBar = binding.imageProgressBar,
                pokemonId = pokemonId
            )

            val type = pokemonItem.type ?: "normal"
            setTypeColor(type)
        }

        private fun extractIdFromUrl(url: String): Int =
            url.trimEnd('/').split('/').last().toInt()

        private fun setTypeColor(type: String) {
            val colorRes = when (type.lowercase()) {
                "normal" -> R.color.type_normal
                "fire" -> R.color.type_fire
                "water" -> R.color.type_water
                "electric" -> R.color.type_electric
                "grass" -> R.color.type_grass
                "ice" -> R.color.type_ice
                "fighting" -> R.color.type_fighting
                "poison" -> R.color.type_poison
                "ground" -> R.color.type_ground
                "flying" -> R.color.type_flying
                "psychic" -> R.color.type_psychic
                "bug" -> R.color.type_bug
                "rock" -> R.color.type_rock
                "ghost" -> R.color.type_ghost
                "dragon" -> R.color.type_dragon
                "dark" -> R.color.type_dark
                "steel" -> R.color.type_steel
                "fairy" -> R.color.type_fairy
                else -> R.color.accent_color
            }
            binding.pokemonCardView.setCardBackgroundColor(
                binding.root.context.getColor(colorRes)
            )
        }
    }

    class LoadingViewHolder(binding: ItemLoadingBinding) :
        RecyclerView.ViewHolder(binding.root)

    class PokemonDiffCallback : DiffUtil.ItemCallback<PokemonListItem>() {
        override fun areItemsTheSame(oldItem: PokemonListItem, newItem: PokemonListItem): Boolean {
            return oldItem.name == newItem.name && oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: PokemonListItem, newItem: PokemonListItem): Boolean {
            return oldItem == newItem
        }
    }
}