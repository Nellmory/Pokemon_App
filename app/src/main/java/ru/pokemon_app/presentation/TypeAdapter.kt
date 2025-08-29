package ru.pokemon_app.presentation

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import ru.pokemon_app.R

class TypeAdapter(
    context: Context,
    types: Array<String>
) : ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, types) {

    init {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    private val typeColors = listOf(
        R.color.type_any,
        R.color.type_normal,
        R.color.type_fire,
        R.color.type_water,
        R.color.type_electric,
        R.color.type_grass,
        R.color.type_ice,
        R.color.type_fighting,
        R.color.type_poison,
        R.color.type_ground,
        R.color.type_flying,
        R.color.type_psychic,
        R.color.type_bug,
        R.color.type_rock,
        R.color.type_ghost,
        R.color.type_dragon,
        R.color.type_dark,
        R.color.type_steel,
        R.color.type_fairy
    )

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent) as TextView
        customizeTextView(view, position)
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent) as TextView
        customizeTextView(view, position)

        view.setBackgroundColor(Color.parseColor("#1F1F1F"))
        return view
    }

    private fun customizeTextView(view: TextView, position: Int) {
        val color = ContextCompat.getColor(context, typeColors[position])
        view.setTextColor(color)
        view.textSize = 18f
        view.setPadding(24, 16, 24, 16)
    }
}