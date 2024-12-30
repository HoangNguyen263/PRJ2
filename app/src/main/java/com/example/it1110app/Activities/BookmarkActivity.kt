package com.example.it1110app.Activities

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.it1110app.Adapters.BookmarkAdapter
import com.example.it1110app.DbQuery
import com.example.it1110app.MyCompleteListener
import com.example.it1110app.R

class BookmarkActivity : AppCompatActivity() {
    private lateinit var questionsView: RecyclerView
    private lateinit var toolbar : Toolbar
    private lateinit var progressDialog: Dialog
    private lateinit var dialogText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark)

        toolbar = findViewById(R.id.bm_toolbar)
        questionsView = findViewById(R.id.bm_recycler_view)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Load categories and set toolbar title
        if (DbQuery.g_catList.isNotEmpty() && DbQuery.g_selected_cat_index < DbQuery.g_catList.size) {
            supportActionBar?.title = "Saved Questions"
        } else {
            Toast.makeText(this, "Category data is missing!", Toast.LENGTH_SHORT).show()
            Log.e("TestActivity", "g_catList is empty or selected index is out of bounds.")
            finish()
            return
        }

        progressDialog = Dialog(this@BookmarkActivity)
        progressDialog.setContentView(R.layout.dialog_layout)
        progressDialog.setCancelable(false) // Prevent dialog from closing when touched outside
        progressDialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialogText = progressDialog.findViewById(R.id.dialog_text)
        dialogText.text = "Loading..."

        progressDialog.show()

        val layoutManager = LinearLayoutManager(this@BookmarkActivity)
        layoutManager.orientation = RecyclerView.VERTICAL
        questionsView.layoutManager = layoutManager


        DbQuery().loadbookmarkQuestions(object : MyCompleteListener {
            override fun onSuccess() {
                progressDialog.dismiss()
                var adapter = BookmarkAdapter(DbQuery.g_bookmarkList)
                questionsView.adapter = adapter

            }

            override fun onFailure() {
                progressDialog.dismiss()
                Toast.makeText(this@BookmarkActivity, "Failed to load questions!", Toast.LENGTH_SHORT).show()
            }
        })



    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}