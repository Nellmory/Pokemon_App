package ru.pokemon_app.presentation

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import ru.pokemon_app.R
import ru.pokemon_app.databinding.DialogPokemonDetailsBinding
import ru.pokemon_app.utils.ImageLoader
import ru.pokemon_app.utils.TypeColorProvider

@AndroidEntryPoint
class PokemonDetailsDialogFragment : DialogFragment() {

    private lateinit var binding: DialogPokemonDetailsBinding
    private val viewModel: PokemonDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.RoundedDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogPokemonDetailsBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pokemonId = arguments?.getInt(ARG_POKEMON_ID) ?: 0
        if (pokemonId != 0) {
            viewModel.loadPokemonDetails(pokemonId)
        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.pokemon.observe(viewLifecycleOwner) { pokemon ->
            binding.pokemonName.text = pokemon.name.replaceFirstChar { it.uppercase() }
            binding.pokemonTypesGroup.removeAllViews()
            pokemon.types.forEach { typeSlot ->
                val typeName = typeSlot.type.name.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase() else it.toString()
                }

                val chip = Chip(requireContext()).apply {
                    text = typeName
                    isClickable = false
                    isCheckable = false
                    setTextColor(requireContext().getColor(android.R.color.white))
                    chipBackgroundColor =
                        ColorStateList.valueOf(
                            TypeColorProvider.getTypeColor(requireContext(), typeSlot.type.name)
                        )
                }

                binding.pokemonTypesGroup.addView(chip)
            }

            val statsText = pokemon.stats.joinToString("\n") { stat ->
                "${
                    stat.stat.name.replaceFirstChar { c ->
                        if (c.isLowerCase()) c.titlecase() else c.toString()
                    }
                }: ${stat.baseStat}"
            }
            binding.pokemonStats.text = statsText

            ImageLoader.loadPokemonImage(
                context = requireContext(),
                imageView = binding.pokemonImage,
                progressBar = binding.imageProgressBar,
                pokemonId = pokemon.id
            )
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                binding.pokemonName.text = error
                binding.pokemonStats.text = ""
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    companion object {
        private const val ARG_POKEMON_ID = "pokemon_id"

        fun newInstance(pokemonId: Int): PokemonDetailsDialogFragment {
            return PokemonDetailsDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_POKEMON_ID, pokemonId)
                }
            }
        }
    }
}