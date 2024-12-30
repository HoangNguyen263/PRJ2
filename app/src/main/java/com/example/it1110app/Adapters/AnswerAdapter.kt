package com.example.it1110app.Adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.widget.TextView
import com.example.it1110app.DbQuery
import com.example.it1110app.Models.QuestionModel
import com.example.it1110app.R

class AnswerAdapter(private var quesList : MutableList<QuestionModel>) : RecyclerView.Adapter<AnswerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view : View = LayoutInflater.from(parent.context).inflate(R.layout.answer_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var ques : String = quesList[position].question!!
        var a : String = quesList[position].optionA!!
        var b : String = quesList[position].optionB!!
        var c : String = quesList[position].optionC!!
        var d : String = quesList[position].optionD!!
        var selected : Int = quesList[position].selectedAns!!
        var correctAns : Int = quesList[position].correctAns!!

        holder.setData(position, ques, a, b, c, d, selected, correctAns)
    }

    override fun getItemCount(): Int {
        return quesList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var quesNo : TextView
        private lateinit var question : TextView
        private lateinit var optionA : TextView
        private lateinit var optionB : TextView
        private lateinit var optionC : TextView
        private lateinit var optionD : TextView
        private lateinit var result : TextView
        init {
            quesNo = itemView.findViewById(R.id.quesNo)
            question = itemView.findViewById(R.id.question)
            optionA = itemView.findViewById(R.id.optionA)
            optionB = itemView.findViewById(R.id.optionB)
            optionC = itemView.findViewById(R.id.optionC)
            optionD = itemView.findViewById(R.id.optionD)
            result = itemView.findViewById(R.id.result)
        }

        fun setData(pos: Int, question : String, a : String, b : String, c : String, d : String, selected : Int, correctAns: Int){
            quesNo.text = "Question No. " + (pos+1).toString()
            this.question.text = question
            optionA.text = "A. " + a
            optionB.text = "B. " + b
            optionC.text = "C. " + c
            optionD.text = "D. " + d

            if (selected == -1){
                result.text = "UN-ANSWERED"
                result.setTextColor(itemView.context.resources.getColor(R.color.black))
                setOptionColor(selected, R.color.text_normal)
            } else {
                if (selected == correctAns){
                    result.text = "CORRECT"
                    result.setTextColor(itemView.context.resources.getColor(R.color.green))
                    setOptionColor(selected, R.color.green)
                } else {
                    result.text = "WRONG" + " (Correct Option: " + (correctAns + 64).toChar() + ")"
                    result.setTextColor(itemView.context.resources.getColor(R.color.red))
                    setOptionColor(selected, R.color.red)
                }
            }
        }

        fun setOptionColor(option: Int, color: Int){
            if (option == 1) {
                optionA.setTextColor(itemView.context.resources.getColor(color))
            } else {
                optionA.setTextColor(itemView.context.resources.getColor(R.color.text_normal))
            }

            if (option == 2) {
                optionB.setTextColor(itemView.context.resources.getColor(color))
            } else {
                optionB.setTextColor(itemView.context.resources.getColor(R.color.text_normal))
            }

            if (option == 3) {
                optionC.setTextColor(itemView.context.resources.getColor(color))
            } else {
                optionC.setTextColor(itemView.context.resources.getColor(R.color.text_normal))
            }

            if (option == 4) {
                optionD.setTextColor(itemView.context.resources.getColor(color))
            } else {
                optionD.setTextColor(itemView.context.resources.getColor(R.color.text_normal))
            }

        }
    }

}