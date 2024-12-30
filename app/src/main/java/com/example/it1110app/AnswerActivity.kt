package com.example.it1110app

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.it1110app.Adapters.AnswerAdapter

class AnswerActivity : AppCompatActivity() {
    private lateinit var toolbar : Toolbar
    private lateinit var answerView : RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answer)
        // Set up Toolbar
        toolbar = findViewById(R.id.aa_toolbar)
        answerView = findViewById(R.id.answer_recycler_view)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Load categories and set toolbar title
        if (DbQuery.g_catList.isNotEmpty() && DbQuery.g_selected_cat_index < DbQuery.g_catList.size) {
            supportActionBar?.title = "ANSWERS"
        } else {
            Toast.makeText(this, "Category data is missing!", Toast.LENGTH_SHORT).show()
            Log.e("TestActivity", "g_catList is empty or selected index is out of bounds.")
            finish()
            return
        }

        val layoutManager = LinearLayoutManager(this@AnswerActivity)
        layoutManager.orientation = RecyclerView.VERTICAL
        answerView.layoutManager = layoutManager

        val answerAdapter = AnswerAdapter(DbQuery.g_questionList)
        answerView.adapter = answerAdapter

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}