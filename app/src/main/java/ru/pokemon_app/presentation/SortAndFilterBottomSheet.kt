package ru.pokemon_app.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.pokemon_app.R
import ru.pokemon_app.databinding.FragmentSortAndFilterBinding

class SortAndFilterBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentSortAndFilterBinding? = null
    private val binding get() = _binding!!

    var onApplyFilters: ((String?, Int?, Int?, Int?, String?) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSortAndFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val types = resources.getStringArray(R.array.pokemon_types)
        val adapter = TypeAdapter(requireContext(), types)
        binding.typeSpinner.adapter = adapter

        binding.applyButton.setOnClickListener {
            val type = binding.typeSpinner.selectedItem.toString()
                .takeIf { it != "Any" }

            val minHp = binding.minHpInput.text.toString().toIntOrNull()
            val minAttack = binding.minAttackInput.text.toString().toIntOrNull()
            val minDefense = binding.minDefenseInput.text.toString().toIntOrNull()

            val orderBy = when (binding.sortRadioGroup.checkedRadioButtonId) {
                binding.sortByName.id -> "name"
                binding.sortByHp.id -> "hp"
                binding.sortByAttack.id -> "attack"
                binding.sortByDefense.id -> "defense"
                else -> null
            }

            onApplyFilters?.invoke(type, minHp, minAttack, minDefense, orderBy)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}