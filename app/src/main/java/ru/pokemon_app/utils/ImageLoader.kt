package ru.pokemon_app.utils

import android.content.Context
import android.widget.ImageView
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import ru.pokemon_app.R

object ImageLoader {
    fun loadPokemonImage(
        context: Context,
        imageView: ImageView,
        progressBar: ProgressBar? = null,
        pokemonId: Int
    ) {
        val pngUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$pokemonId.png"

        progressBar?.visibility = android.view.View.VISIBLE

        Glide.with(context)
            .load(pngUrl)
            .apply(RequestOptions()
                .placeholder(R.drawable.ic_pokemon_placeholder)
                .error(R.drawable.ic_error)
                .centerCrop()
            )
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(object : com.bumptech.glide.request.target.CustomTarget<android.graphics.drawable.Drawable>() {
                override fun onResourceReady(
                    resource: android.graphics.drawable.Drawable,
                    transition: com.bumptech.glide.request.transition.Transition<in android.graphics.drawable.Drawable>?
                ) {
                    imageView.setImageDrawable(resource)
                    progressBar?.visibility = android.view.View.GONE
                }

                override fun onLoadCleared(placeholder: android.graphics.drawable.Drawable?) {
                    progressBar?.visibility = android.view.View.GONE
                }

                override fun onLoadFailed(errorDrawable: android.graphics.drawable.Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    progressBar?.visibility = android.view.View.GONE
                }
            })
    }
}