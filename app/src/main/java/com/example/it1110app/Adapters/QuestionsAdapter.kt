package com.example.it1110app.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.it1110app.DbQuery
import com.example.it1110app.Models.QuestionModel
import com.example.it1110app.R

class QuestionsAdapter(private var questionsList: MutableList<QuestionModel>) : RecyclerView.Adapter<QuestionsAdapter.ViewHolder>() {

    class ViewHolder(view: View, private val questionsList: MutableList<QuestionModel>) : RecyclerView.ViewHolder(view) {
        private val ques: TextView = view.findViewById(R.id.tv_question)
        private val optionA: Button = view.findViewById(R.id.optionA)
        private val optionB: Button = view.findViewById(R.id.optionB)
        private val optionC: Button = view.findViewById(R.id.optionC)
        private val optionD: Button = view.findViewById(R.id.optionD)
        private var prevSelectedB: Button? = null

        public fun setData(pos: Int) {
            var question = questionsList[pos]
            ques.text = question.question
            optionA.text = question.optionA
            optionB.text = question.optionB
            optionC.text = question.optionC
            optionD.text = question.optionD

            //set adapter intially and when adapter reinitialize itself
            setOptionBackground(optionA, 1, pos)
            setOptionBackground(optionB, 2, pos)
            setOptionBackground(optionC, 3, pos)
            setOptionBackground(optionD, 4, pos)

            optionA.setOnClickListener(View.OnClickListener {
                selectOption(optionA, 1, pos)
            })

            optionB.setOnClickListener(View.OnClickListener {
                selectOption(optionB, 2, pos)
            })

            optionC.setOnClickListener(View.OnClickListener {
                selectOption(optionC, 3, pos)
            })

            optionD.setOnClickListener(View.OnClickListener {
                selectOption(optionD, 4, pos)
            })
        }

        private fun selectOption(option: Button, option_num: Int, quesId : Int) {

            //we haven't selected any option yet
            if (prevSelectedB == null){
                option.setBackgroundResource(R.drawable.selected_btn)
                //store selected option to selected answer
                DbQuery.g_questionList[quesId].selectedAns = option_num

                changeStatus(quesId, DbQuery.ANSWERED)
                prevSelectedB = option
            }
            else {
                if (prevSelectedB?.id == option.id){
                    //deselect the option
                    option.setBackgroundResource(R.drawable.unselected_bt)
                    DbQuery.g_questionList[quesId].selectedAns = -1

                    changeStatus(quesId, DbQuery.UNANSWERED)
                    prevSelectedB = null
                }
                else {
                    //deselect the previous selected option
                    prevSelectedB?.setBackgroundResource(R.drawable.unselected_bt)
                    //select the new option
                    option.setBackgroundResource(R.drawable.selected_btn)
                    DbQuery.g_questionList[quesId].selectedAns = option_num

                    changeStatus(quesId, DbQuery.ANSWERED)
                    prevSelectedB = option
                }
            }
        }

        // Set background for selected option
        private fun setOptionBackground(option: Button, option_num: Int, quesId: Int) {
            if (DbQuery.g_questionList[quesId].selectedAns == option_num){
                option.setBackgroundResource(R.drawable.selected_btn)
            }
            else {
                option.setBackgroundResource(R.drawable.unselected_bt)
            }
        }

        /*update status of question
    * if user deselect option -> status of question will be change to UNANSWERED
    * if user select option -> status of question will be change from NOT_VISITED to ANSWERED
    * if user select review mark-> we don't change status of question
    * */
        private fun changeStatus(quesId: Int, status: Int) {
            //check if is a review
            if (DbQuery.g_questionList[quesId].status != DbQuery.REVIEW){
                DbQuery.g_questionList[quesId].status = status
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view : View = LayoutInflater.from(parent.context).inflate(R.layout.question_item_layout, parent, false)
        return ViewHolder(view,questionsList)
    }

    override fun getItemCount(): Int {
        return questionsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //set data
        holder.setData(position)

    }
}