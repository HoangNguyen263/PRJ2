package com.example.it1110app.Adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.it1110app.DbQuery
import com.example.it1110app.Models.TestModel
import com.example.it1110app.R
import com.example.it1110app.Activities.StartTestActivity

class TestAdapter(private val testList: MutableList<TestModel>) : RecyclerView.Adapter<TestAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val testNo: TextView = view.findViewById(R.id.testNo)
        private val topScore: TextView = view.findViewById(R.id.scoretext)
        private val progressBar: ProgressBar = view.findViewById(R.id.testProgress)
        private val completedTick: ImageView = view.findViewById(R.id.completed_tick)
        fun setData(position: Int, progress: Int) {
            testNo.text = "Test No: ${position + 1}"
            topScore.text = "${progress}%"
            progressBar.progress = progress

            itemView.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {

                        DbQuery.g_selected_test_index = position

                        var intent : Intent = Intent(v?.context, StartTestActivity::class.java)
                        itemView.context.startActivity(intent)
                    }
            })

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view : View = LayoutInflater.from(parent.context).inflate(R.layout.test_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return testList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var progress : Int = testList.get(position).topScore
        holder.setData(position,progress)
    }


}