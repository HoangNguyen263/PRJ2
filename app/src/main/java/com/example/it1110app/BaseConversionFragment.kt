package com.example.it1110app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

class BaseConversionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_base_conversion, container, false)

        val spinnerInputBase: Spinner = view.findViewById(R.id.spinner_input_base)
        val spinnerOutputBase: Spinner = view.findViewById(R.id.spinner_output_base)
        val editTextInputValue: EditText = view.findViewById(R.id.edittext_input_value)
        val buttonConvert: Button = view.findViewById(R.id.button_convert)
        val textViewOutputValue: TextView = view.findViewById(R.id.textview_output_value)

        val bases = arrayOf("Binary", "Octal", "Decimal", "Hexadecimal")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, bases)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerInputBase.adapter = adapter
        spinnerOutputBase.adapter = adapter

        buttonConvert.setOnClickListener {
            val inputBase = spinnerInputBase.selectedItem.toString()
            val outputBase = spinnerOutputBase.selectedItem.toString()
            val inputValue = editTextInputValue.text.toString()

            val result = convertBase(inputValue, inputBase, outputBase)
            textViewOutputValue.text = result
        }

        return view
    }

    private fun convertBase(value: String, inputBase: String, outputBase: String): String {
        val decimalValue = when (inputBase) {
            "Binary" -> Integer.parseInt(value, 2)
            "Octal" -> Integer.parseInt(value, 8)
            "Decimal" -> value.toInt()
            "Hexadecimal" -> Integer.parseInt(value, 16)
            else -> 0
        }

        return when (outputBase) {
            "Binary" -> Integer.toBinaryString(decimalValue)
            "Octal" -> Integer.toOctalString(decimalValue)
            "Decimal" -> decimalValue.toString()
            "Hexadecimal" -> Integer.toHexString(decimalValue)
            else -> ""
        }
    }
}