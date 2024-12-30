package com.example.it1110app.Adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.it1110app.DbQuery
import com.example.it1110app.Models.CategoryModel
import com.example.it1110app.R
import com.example.it1110app.TestActivity

class CategoryAdapter(private var cat_list: MutableList<CategoryModel>) : BaseAdapter() {

    public fun CategoryAdapter(cat_list : MutableList<CategoryModel>) {
        this.cat_list = cat_list
    }
    override fun getCount(): Int {
        return cat_list.size
    }

    override fun getItem(position: Int): Any {
        return cat_list[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val myView: View = convertView ?: LayoutInflater.from(parent?.context).inflate(R.layout.cat_item_layout, parent, false)

        myView.setOnClickListener {

            DbQuery.g_selected_cat_index = position //set the selected category index

            val intent = Intent(it.context, TestActivity::class.java)

            it.context.startActivity(intent)
        }

        val catName: TextView = myView.findViewById(R.id.catName)
        val noOfTest: TextView = myView.findViewById(R.id.no_of_tests)

        catName.text = cat_list[position].name
        noOfTest.text = cat_list[position].noOfTest.toString() + " Tests"

        return myView
    }
}