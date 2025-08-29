package ru.pokemon_app.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import ru.pokemon_app.R
import com.bumptech.glide.request.transition.Transition

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
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.ic_pokemon_placeholder)
                    .error(R.drawable.ic_error)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            )
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    imageView.setImageDrawable(resource)
                    progressBar?.visibility = View.GONE
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    progressBar?.visibility = View.GONE
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    Glide.with(context)
                        .load(pngUrl)
                        .onlyRetrieveFromCache(true)   // 💾
                        .apply(RequestOptions().centerCrop())
                        .into(imageView)

                    progressBar?.visibility = View.GONE
                }
            })
    }
}