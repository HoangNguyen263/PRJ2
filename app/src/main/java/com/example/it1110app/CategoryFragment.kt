package com.example.it1110app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.it1110app.Adapters.CategoryAdapter

class CategoryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var catView: GridView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_category, container, false)
        var toolbar : Toolbar = activity?.findViewById(R.id.toolbar)!!
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Categories"
        catView = view.findViewById(R.id.grid_category)

        //loadCategories()

        val adapter = CategoryAdapter(DbQuery.g_catList)
        catView?.adapter = adapter

        return view
    }

    fun loadCategories(){
//        catList.clear()
//        catList.add(CategoryModel("1", "Phần I: Tin Học Căn Bản", 8))
//        catList.add(CategoryModel("2", "Phần II: Giải Quyết Bài Toán", 2))
//        catList.add(CategoryModel("3", "Phần III: Lập Trình", 9))
    }

    companion object {

    }

}