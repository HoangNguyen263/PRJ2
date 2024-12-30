package com.example.it1110app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import java.lang.Float.floatToIntBits
import java.lang.Float.intBitsToFloat

class IEEE754Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_i_e_e_e754, container, false)

        val floatInput: EditText = view.findViewById(R.id.float_input)
        val binaryInput: EditText = view.findViewById(R.id.binary_input)
        val convertToBinaryButton: Button = view.findViewById(R.id.convert_to_binary_button)
        val convertToFloatButton: Button = view.findViewById(R.id.convert_to_float_button)
        val ieee754Output: TextView = view.findViewById(R.id.ieee754_output)
        val floatOutput: TextView = view.findViewById(R.id.float_output)

        // Xử lý chuyển đổi Float -> IEEE 754
        convertToBinaryButton.setOnClickListener {
            val floatNumber = floatInput.text.toString().toFloatOrNull()
            if (floatNumber != null) {
                val ieee754String = floatToIEEE754(floatNumber)
                ieee754Output.text = "IEEE 754 String: $ieee754String"
            } else {
                ieee754Output.text = "Please enter a valid float number"
            }
        }

        // Xử lý chuyển đổi IEEE 754 -> Float
        convertToFloatButton.setOnClickListener {
            val binaryString = binaryInput.text.toString()
            if (binaryString.length == 32 && binaryString.all { it == '0' || it == '1' }) {
                val floatNumber = ieee754ToFloat(binaryString)
                floatOutput.text = "Float Number: $floatNumber"
            } else {
                floatOutput.text = "Please enter a valid 32-bit binary string"
            }
        }

        return view
    }

    // Hàm chuyển đổi Float -> IEEE 754 (chuỗi nhị phân 32 bit)
    private fun floatToIEEE754(number: Float): String {
        val bits = floatToIntBits(number)
        return bits.toUInt().toString(2).padStart(32, '0')
    }

    // Hàm chuyển đổi IEEE 754 (chuỗi nhị phân 32 bit) -> Float
    private fun ieee754ToFloat(binaryString: String): Float {
        val bits = binaryString.toUInt(2).toInt()
        return intBitsToFloat(bits)
    }
}
