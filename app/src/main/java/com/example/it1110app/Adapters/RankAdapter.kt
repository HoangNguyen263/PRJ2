package com.example.it1110app.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.it1110app.Models.RankModel
import com.example.it1110app.R

class RankAdapter : RecyclerView.Adapter<RankAdapter.ViewHolder> {
    private var userList: List<RankModel> = ArrayList()

    constructor(rankList: List<RankModel>) {
        this.userList = rankList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RankAdapter.ViewHolder {
        var view : View = LayoutInflater.from(parent.context).inflate(R.layout.rank_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var name: String = userList[position].name
        var score: Int = userList[position].score
        var rank: Int = userList[position].rank

        holder.setData(name, score, rank)
    }

    override fun getItemCount(): Int {
        if (userList.size > 10) {
            return 10
        } else {
            return userList.size
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var nameTV: TextView
        private lateinit var rankTV: TextView
        private lateinit var scoreTV: TextView
        private lateinit var imgTV: TextView

        init {
            nameTV = itemView.findViewById(R.id.name)
            rankTV = itemView.findViewById(R.id.rank)
            scoreTV = itemView.findViewById(R.id.score)
            imgTV = itemView.findViewById(R.id.img_text)
        }

        public fun setData(name: String, score : Int, rank: Int){
            nameTV.text = name
            scoreTV.text = "Score : " + score.toString()
            rankTV.text = "Rank - " + rank.toString()
            imgTV.text = name.uppercase().substring(0, 1)
        }
    }
}