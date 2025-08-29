package ru.pokemon_app.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.pokemon_app.databinding.ItemLoadingBinding
import ru.pokemon_app.databinding.PokemonCardItemBinding
import ru.pokemon_app.domain.model.PokemonListItem
import ru.pokemon_app.utils.ImageLoader
import ru.pokemon_app.utils.TypeColorProvider

class PokemonAdapter : ListAdapter<PokemonListItem, RecyclerView.ViewHolder>(PokemonDiffCallback()) {

    companion object {
        private const val TYPE_ITEM = 0
        private const val TYPE_LOADING = 1
    }

    var onItemClick: ((PokemonListItem) -> Unit)? = null
    private var isLoading = false

    override fun getItemViewType(position: Int): Int {
        return if (position < currentList.size) TYPE_ITEM else TYPE_LOADING
    }

    override fun getItemCount(): Int {
        return currentList.size + if (isLoading) 1 else 0
    }

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
            val pokemonItem = getItem(position)
            (holder as PokemonViewHolder).bind(pokemonItem)
        }
    }

    fun setLoading(loading: Boolean) {
        val wasLoading = isLoading
        isLoading = loading
        if (wasLoading != loading) {
            if (loading) notifyItemInserted(currentList.size)
            else notifyItemRemoved(currentList.size)
        }
    }

    inner class PokemonViewHolder(private val binding: PokemonCardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION &&
                    bindingAdapterPosition < currentList.size
                ) {
                    onItemClick?.invoke(getItem(bindingAdapterPosition))
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
            binding.pokemonCardView.setCardBackgroundColor(
                TypeColorProvider.getTypeColor(binding.root.context, type)
            )
        }

        private fun extractIdFromUrl(url: String): Int =
            url.trimEnd('/').split('/').last().toIntOrNull() ?: 0

    }

    class LoadingViewHolder(binding: ItemLoadingBinding) :
        RecyclerView.ViewHolder(binding.root)

    class PokemonDiffCallback : DiffUtil.ItemCallback<PokemonListItem>() {
        override fun areItemsTheSame(oldItem: PokemonListItem, newItem: PokemonListItem): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: PokemonListItem, newItem: PokemonListItem): Boolean {
            return oldItem == newItem
        }
    }
}