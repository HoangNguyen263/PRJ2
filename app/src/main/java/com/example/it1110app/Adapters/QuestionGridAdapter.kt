package com.example.it1110app.Adapters

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.it1110app.DbQuery
import com.example.it1110app.QuestionActivity
import com.example.it1110app.R

class QuestionGridAdapter(private var context : Context,private var numOfQues : Int) : BaseAdapter() {
    override fun getCount(): Int {
        return numOfQues
    }

    override fun getItem(position: Int): Any {
        return Any()
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var myView : View? = null
        if (convertView == null){
            myView = LayoutInflater.from(parent?.context).inflate(R.layout.ques_grid_item, parent, false)
        } else {
            myView = convertView
        }

        myView!!.setOnClickListener {
            if (context is QuestionActivity) {
                (context as QuestionActivity).goToQuestion(position)
            }
        }

        var quesTv : TextView = myView!!.findViewById(R.id.ques_num)
        quesTv.setText((position + 1).toString())

        when (DbQuery.g_questionList[position].status) {
            DbQuery.NOT_VISITED -> {
                quesTv.setBackgroundTintList(ColorStateList.valueOf(parent!!.context.resources.getColor(
                    R.color.gray
                )))
            }
            DbQuery.UNANSWERED -> {
                quesTv.setBackgroundTintList(ColorStateList.valueOf(parent!!.context.resources.getColor(
                    R.color.red
                )))
            }
            DbQuery.ANSWERED -> {
                quesTv.setBackgroundTintList(ColorStateList.valueOf(parent!!.context.resources.getColor(
                    R.color.green
                )))
            }
            DbQuery.REVIEW -> {
                quesTv.setBackgroundTintList(ColorStateList.valueOf(parent!!.context.resources.getColor(
                    R.color.pink
                )))
            }
        }

        return myView
    }
}